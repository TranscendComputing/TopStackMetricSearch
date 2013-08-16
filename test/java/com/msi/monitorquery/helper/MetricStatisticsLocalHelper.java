package com.msi.monitorquery.helper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.amazonaws.services.cloudwatch.model.ComparisonOperator;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;
import com.amazonaws.services.cloudwatch.model.Statistic;
import com.msi.tough.monitor.common.MonitorConstants;
import com.msi.tough.query.ActionRequest;

/**
 * Metric statistics helper for non-web tests (using actions in-VM).
 *
 * @author jgardner
 *
 */
@Component
public class MetricStatisticsLocalHelper {

    public static final String DEFAULT_NAMESPACE = "AWS/EC2";
    public static final String DEFAULT_METRIC = "CPUUtilization";

    /**
     * Construct a minimal valid alarm request.
     *
     * @param alarmName
     * @return
     */
    public static GetMetricStatisticsMockRequest getStatsRequest(String alarmName) {
        final GetMetricStatisticsMockRequest request =
                new GetMetricStatisticsMockRequest();
        request.put(MonitorConstants.NODE_NAMESPACE, DEFAULT_NAMESPACE);
        request.put(MonitorConstants.NODE_ALARMNAME, alarmName);
        request.put(MonitorConstants.NODE_ALARMDESCRIPTION, alarmName);
        request.put(MonitorConstants.NODE_COMPARISONOPERATOR,
                ComparisonOperator.GreaterThanOrEqualToThreshold.toString());
        request.put(MonitorConstants.NODE_EVALUATIONPERIODS, 2);
        request.put(MonitorConstants.NODE_METRICNAME, DEFAULT_METRIC);
        request.put(MonitorConstants.NODE_PERIOD, 60);
        request.put(MonitorConstants.NODE_STATISTIC,
                Statistic.Average.toString());
        request.put(MonitorConstants.NODE_THRESHOLD, 0.1d);
        return request;
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

    public static class GetMetricStatisticsMockRequest extends ActionRequest {

        private int currentDimension = 0;
        private int currentStatistic = 0;
        /**
        *
        */
       public GetMetricStatisticsMockRequest() {
           super();
           put(MonitorConstants.NODE_NAMESPACE, DEFAULT_NAMESPACE);
       }

       public GetMetricStatisticsMockRequest withStatistic(String name) {
           currentStatistic++;
           String prefix = "Statistics.member." + currentStatistic;
           put(prefix, name);
           return this;
       }

       public GetMetricStatisticsMockRequest withDimensionName(String name) {
           currentDimension++;
           String prefix = "Dimensions.member." + currentDimension + ".";
           put(prefix + MonitorConstants.NODE_NAME, name);
           return this;
       }

       public GetMetricStatisticsMockRequest withDimensionValue(String value) {
           String prefix = "Dimensions.member." + currentDimension + ".";
           put(prefix + MonitorConstants.NODE_VALUE, value);
           return this;
       }

       public void reset() {
           super.reset();
           currentDimension = 0;
           currentStatistic = 0;
       }
    }
}
