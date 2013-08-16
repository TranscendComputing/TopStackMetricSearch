package com.msi.monitorquery.helper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.ComparisonOperator;
import com.amazonaws.services.cloudwatch.model.DeleteAlarmsRequest;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmsRequest;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmsResult;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.amazonaws.services.cloudwatch.model.Statistic;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.monitor.DimensionBean;

public class AlarmHelper {
    private static Logger logger = Appctx.getLogger(AlarmHelper.class
            .getName());

    public static final String DEFAULT_NAMESPACE = "AWS/EC2";
    public static final String DEFAULT_METRIC = "CPUUtilization";

    public static final String CPU_METRIC = "CPUUtilization";

    private static Set<String> alarms = new HashSet<String>();

    // TODO: use describe instead.
    private static String genericInstance = "instance123";

    /**
     * Construct a minimal valid alarm request.
     *
     * @param alarmName
     * @return
     */
    public static PutMetricAlarmRequest metricAlarmRequest(String alarmName) {
        final PutMetricAlarmRequest request = new PutMetricAlarmRequest()
            .withNamespace(DEFAULT_NAMESPACE)
            .withAlarmName(alarmName)
            .withAlarmDescription(alarmName)
            .withComparisonOperator(ComparisonOperator.GreaterThanOrEqualToThreshold)
            .withEvaluationPeriods(2)
            .withMetricName(DEFAULT_METRIC)
            .withPeriod(60)
            .withStatistic(Statistic.Average)
            .withThreshold(0.1d);
        return request;
    }

    /**
     * Put a metric alarm (don't care about the details, just need one.
     *
     * @param alarmName
     */
    public static void putMetricAlarm(String alarmName,
            AmazonCloudWatchClient client) {
        PutMetricAlarmRequest request = metricAlarmRequest(alarmName);
        request.withDimensions(new Dimension()
        .withName(DimensionBean.DIMENSION_INSTANCE_ID)
        .withValue(genericInstance));
        client.putMetricAlarm(request);
        alarms.add(alarmName);
    }

    /**
     * Delete an alarm with the given name.
     *
     * @param alarmName
     * @param client
     */
    public static void deleteAlarm(String alarmName,
            AmazonCloudWatchClient client) {
        final DeleteAlarmsRequest request = new DeleteAlarmsRequest()
        .withAlarmNames(alarmName);
        client.deleteAlarms(request);
        alarms.remove(alarmName);
    }

    /**
     * Delete an alarm with the given name.
     *
     * @param alarmName
     * @param client
     */
    public static void deleteAllCreatedAlarms(AmazonCloudWatchClient client) {
        DeleteAlarmsRequest request = new DeleteAlarmsRequest()
        .withAlarmNames(alarms);
        if (logger.isDebugEnabled()) {
            for (String name : alarms) {
                logger.debug("deleteAll: Deleting alarm: " + name);
            }
        }
        try {
            client.deleteAlarms(request);
        } catch(Exception deleteAllEx) {
            logger.warn("Got an error in deleteAll, deleting individually.");
            for (String alarmName : alarms) {
                request = new DeleteAlarmsRequest()
                .withAlarmNames(alarmName);
                try {
                    client.deleteAlarms(request);
                } catch (Exception deleteEx) {
                    logger.warn("Failed to delete: " + alarmName, deleteEx);
                }
            }
        }
        alarms.clear();
    }

    /**
     * Get an alarm from the collection.
     *
     * @param alarms
     * @param name
     * @return matching alarm
     */
    public static MetricAlarm getAlarmByName(List<MetricAlarm> alarms,
            String name) {
        for (MetricAlarm alarm : alarms) {
            if (alarm.getAlarmName().equals(name)) {
                return alarm;
            }
        }
        return null;
    }

    /**
     * Get an alarm from the collection.
     *
     * @param alarms
     * @param name
     * @return matching alarm
     */
    public static MetricAlarm describeSingleAlarm(String alarmName,
            AmazonCloudWatchClient client) {
        DescribeAlarmsResult result = null;
        final DescribeAlarmsRequest describeRequest =
                new DescribeAlarmsRequest();
        result = client.describeAlarms(describeRequest);
        return getAlarmByName(result.getMetricAlarms(), alarmName);
    }
}
