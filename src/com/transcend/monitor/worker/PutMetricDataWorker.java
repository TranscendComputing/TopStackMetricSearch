/**
 *
 */
package com.transcend.monitor.worker;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.DateHelper;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.monitor.DimensionBean;
import com.msi.tough.model.monitor.MeasureBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.CWUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.monitor.message.MetricAlarmMessage.Dimension;
import com.transcend.monitor.message.PutMetricDataMessage.MetricDatum;
import com.transcend.monitor.message.PutMetricDataMessage.PutMetricDataRequest;
import com.transcend.monitor.message.PutMetricDataMessage.PutMetricDataResponse;

public class PutMetricDataWorker extends
        AbstractWorker<PutMetricDataRequest, PutMetricDataResponse> {

    private static final Logger logger = Appctx
            .getLogger(PutMetricDataWorker.class.getName());

    /**
     * We need a local copy of this doWork to provide the transactional
     * annotation. Transaction management is handled by the annotation, which
     * can only be on a concrete class.
     *
     * @param req
     * @return
     * @throws Exception
     */
    @Transactional
    public PutMetricDataResponse doWork(PutMetricDataRequest req)
            throws Exception {
        logger.debug("Performing work for PutMetricData.");
        return super.doWork(req, getSession());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.transcend.compute.worker.AbstractWorker#doWork0(com.google.protobuf
     * .Message)
     */
    @Override
    @Transactional
    public PutMetricDataResponse doWork0(PutMetricDataRequest request,
            ServiceRequestContext context) throws Exception {
        final PutMetricDataResponse.Builder result = PutMetricDataResponse
                .newBuilder();
        AccountBean account = context.getAccountBean();

        Session session = getSession();

        final String namespace = request.getNamespace();
        final List<MeasureBean> data = new ArrayList<MeasureBean>();
        final List<MetricDatum> datum = request.getMetricDataList();
        for (final MetricDatum md : datum) {
            final MeasureBean d = new MeasureBean();
            final List<Dimension> mDims = md.getDimensionList();
            if (mDims != null) {
                final Set<DimensionBean> dimBeanSet = new HashSet<DimensionBean>();
                for (final Dimension mDim : mDims) {
                    final DimensionBean dBean = CWUtil.getDimensionBean(
                            session, account.getId(), mDim.getValue(),
                            mDim.getValue(), true);
                    dimBeanSet.add(dBean);
                }
                d.setDimensions(dimBeanSet);
            }
            d.setNamespace(namespace);
            d.setName(md.getMetricName());
            d.setUnit(md.getUnit().toString());
            d.setValue(md.getValue());
            Date ts = DateHelper.getCalendarFromISO8601String(md.getTimestamp(),
                    TimeZone.getTimeZone("GMT")).getTime();

            d.setTimestamp(ts);
            data.add(d);
        }

        for (final MeasureBean m : data) {
            m.save(session);
        }

        return result.buildPartial();
    }

}
