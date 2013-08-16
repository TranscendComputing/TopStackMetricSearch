package com.transcend.monitor.worker;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.monitor.AlarmBean;
import com.msi.tough.monitor.common.manager.db.AlarmStoreClient;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.monitor.helper.AlarmModelHelper;
import com.transcend.monitor.message.DescribeAlarmsForMetricMessage.DescribeAlarmsForMetricRequest;
import com.transcend.monitor.message.DescribeAlarmsForMetricMessage.DescribeAlarmsForMetricResponse;
import com.transcend.monitor.message.MetricAlarmMessage.Dimension;
import com.transcend.monitor.message.MetricAlarmMessage.MetricAlarm;

public class DescribeAlarmsForMetricWorker
        extends
        AbstractWorker<DescribeAlarmsForMetricRequest, DescribeAlarmsForMetricResponse> {

    private static final Logger logger = Appctx
            .getLogger(DescribeAlarmsForMetricWorker.class.getName());

    private AlarmStoreClient alarmStoreClient = null;

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
    public DescribeAlarmsForMetricResponse doWork(
            DescribeAlarmsForMetricRequest req) throws Exception {
        logger.debug("Performing work for DescribeAlarms.");
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
    public DescribeAlarmsForMetricResponse doWork0(
            DescribeAlarmsForMetricRequest request,
            ServiceRequestContext context) throws Exception {
        final DescribeAlarmsForMetricResponse.Builder result = DescribeAlarmsForMetricResponse
                .newBuilder();
        AccountBean account = context.getAccountBean();

        String metricName = request.getMetricName();
        String namespace = request.getNamespace();
        BigInteger period = null;
        String statistic = null;
        String unit = null;
        Map<String, String> dimensions = null;

        if (request.hasPeriod()) {
            period = BigInteger.valueOf(request.getPeriod());
        }
        if (request.hasStatistic()) {
            statistic = request.getStatistic().toString();
        }
        if (request.hasUnit()) {
            unit = request.getUnit().toString();
        }
        if (request.getDimensionCount() > 0) {
            dimensions = new HashMap<String, String>();
            for (Dimension d : request.getDimensionList()) {
                dimensions.put(d.getName(), d.getValue());
            }
        }

        MetricAlarm awsMetricAlarms[] = describeAlarmsForMetric(metricName,
                namespace, dimensions, period, statistic, unit, account.getId());
        Collection<MetricAlarm> collMetricAlarms = Arrays
                .asList(awsMetricAlarms);
        result.addAllMetricAlarm(collMetricAlarms);

        return result.buildPartial();
    }

    public void setAlarmStoreClient(AlarmStoreClient alarmStoreClient) {
        this.alarmStoreClient = alarmStoreClient;
    }

    public AlarmStoreClient getAlarmStoreClient() {
        if (alarmStoreClient == null) {
            alarmStoreClient = Appctx.getBean("MON_ALARMSTORE");
        }
        return alarmStoreClient;
    }

    /**
    *
    */
    @Transactional
    public MetricAlarm[] describeAlarmsForMetric(final String metricName,
            final String namespace, final Map<String, String> dimensions,
            final BigInteger period, final String statistic, final String unit,
            final long acctId) {

        logger.info("---------------describeAlarmsForMetric-------------------");
        final Session session = HibernateUtil.getSession();
        final List<MetricAlarm> alarms = new ArrayList<MetricAlarm>();
        final Criteria criteria = session.createCriteria(AlarmBean.class);
        // criteria.add(Restrictions.eq("ownerId", acctId));
        criteria.add(Restrictions.eq("metricName", metricName));
        if (namespace != null) {
            if ("AWS/EC2".equals(namespace) || "MSI/EC2".equals(namespace)) {
                criteria.add(Restrictions.or(Restrictions.eq("namespace", namespace),
                        Restrictions.eq("namespace", "MSI/EC2")));
            } else {
                criteria.add(Restrictions.eq("namespace", namespace));
            }
        }
        if (period != null) {
            criteria.add(Restrictions.eq("period", period));
        }
        if (statistic != null) {
            criteria.add(Restrictions.eq("statistic", statistic));
        }
        if (unit != null) {
            criteria.add(Restrictions.eq("unit", unit));
        }
        // List<String> dimensionValues = new ArrayList<String>();
        // if (dimensions != null && dimensions.size() > 0) {
        // dimensions.values();
        // }
        // criteria.add(Restrictions.in("dimensions", dimensions))
        if (dimensions != null && dimensions.size() > 0) {
            // System.out.println("1. SIZE: "+ criteria.list().size());
            criteria.createCriteria("dimensions").add(
                    Restrictions.in("value", dimensions.values()));
            // System.out.println("2. SIZE: "+criteria.list().size());
        }
        @SuppressWarnings("unchecked")
        final List<AlarmBean> resAlarms = criteria.list();

        for (final AlarmBean alarm : resAlarms) {
            MetricAlarm ma = AlarmModelHelper.toMetricAlarm(alarm);
            alarms.add(ma);
        }

        logger.info("----------------------------------------------------------");
        return alarms.toArray(new MetricAlarm[alarms.size()]);
    }

}
