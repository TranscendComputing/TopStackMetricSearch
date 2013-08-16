/**
 *
 */
package com.transcend.monitor.worker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.model.AccountBean;
import com.msi.tough.monitor.common.manager.db.DataStoreClient;
import com.msi.tough.monitor.common.model.exception.MSIMonitorException;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.InstanceUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.monitor.message.ListMetricsMessage.ListMetricsRequest;
import com.transcend.monitor.message.ListMetricsMessage.ListMetricsResponse;
import com.transcend.monitor.message.MetricAlarmMessage.Dimension;
import com.transcend.monitor.message.MetricMessage.Metric;

public class ListMetricsWorker extends
        AbstractWorker<ListMetricsRequest, ListMetricsResponse> {

    private static final Logger logger = Appctx
            .getLogger(ListMetricsWorker.class.getName());

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
    public ListMetricsResponse doWork(ListMetricsRequest req) throws Exception {
        logger.debug("Performing work for ListMetrics.");
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
    public ListMetricsResponse doWork0(ListMetricsRequest request,
            ServiceRequestContext context) throws Exception {

        final ListMetricsResponse.Builder result = ListMetricsResponse
                .newBuilder();
        AccountBean account = context.getAccountBean();
        final String metricName = request.getMetricName();
        final String namespace = request.getNamespace();
        List<String> dimensions = null;

        Session session = getSession();
        if (request.getDimensionCount() > 0) {
            dimensions = new ArrayList<String>();
            for (final Dimension d : request.getDimensionList()) {
                String dim = d.getValue();
                if (d.getName().equals("InstanceId")) {
                    if (!dim.startsWith("i-")) {
                        dim = InstanceUtil.UUIDtoEc2(session, account.getId(),
                                dim);
                    }
                }
                dimensions.add(dim);
            }
        }
        final Collection<Metric> met = listUserMetrics(metricName, namespace,
                dimensions, account.getId(), null);

        logger.info("Calling listUserMetrics.");
        result.addAllMetric(met);
        logger.info("Got metrics");
        result.setNextToken(request.getNextToken());

        return result.buildPartial();
    }

    public Collection<Metric> listUserMetrics(final String metricName,
            final String namespace, final List<String> dimensions,
            final long acid, final String nextToken) throws MSIMonitorException {

        Collection<Metric> collMetrics = Collections.emptyList();

        if (collMetrics.isEmpty()) {
            logger.info("No Metrics found.");
        } else {
            logger.info("Number of metrics returned [" + collMetrics.size()
                    + "]");
        }

        return collMetrics;
    }

}
