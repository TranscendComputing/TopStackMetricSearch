package com.msi.monitorquery.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.cloudwatch.model.AlarmHistoryItem;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmHistoryRequest;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmHistoryResult;
import com.msi.monitorquery.helper.AlarmHelper;
import com.msi.tough.core.Appctx;

/**
 * Test describe alarm history.
 *
 * Example from AWS:
 * {AlarmHistoryItems:
 * [{AlarmName: awsec2-johns-test-i-97c988e6-High-CPU-Utilization,
 * Timestamp: Mon Jan 14 12:40:47 CST 2013, HistoryItemType: Action,
 * HistorySummary: Stop EC2 Instance 'i-97c988e6' action completed successfully,
 * HistoryData: {"actionState":"Succeeded","error":null,
 * "stateUpdateTimestamp":null,
 * "notificationResource":"arn:aws:automate:us-east-1:ec2:stop",
 * "publishedMessage":null}, },

 * {AlarmName: awsec2-johns-test-i-97c988e6-High-CPU-Utilization,
 * Timestamp: Mon Jan 14 12:40:45 CST 2013, HistoryItemType: StateUpdate,
 * HistorySummary: Alarm updated from INSUFFICIENT_DATA to ALARM,
 * HistoryData: {"version":"1.0",
 * "oldState":{"stateValue":"INSUFFICIENT_DATA",
 * "stateReason":"Unchecked: Initial alarm creation"},
 * "newState":{"stateValue":"ALARM",
 * "stateReason":"Threshold Crossed: 1 datapoint (0.20400000000000001) was less than the threshold (10.0).",
 * "stateReasonData":{"version":"1.0",
 * "queryDate":"2013-01-14T18:40:45.944+0000",
 * "startDate":"2013-01-14T18:35:00.000+0000",
 * "statistic":"Average","period":60,
 * "recentDatapoints":[0.20400000000000001],"threshold":10.0}}}, },
 *
 * {AlarmName: awsec2-johns-test-i-97c988e6-High-CPU-Utilization,
 * Timestamp: Mon Jan 14 12:40:45 CST 2013, HistoryItemType: Action,
 * HistorySummary: arn:aws:automate:us-east-1:ec2:stop is in progress,
 * HistoryData: {"actionState":"InProgress",
 * "stateUpdateTimestamp":"2013-01-14T18:40:45.962+0000",
 * "notificationResource":"arn:aws:automate:us-east-1:ec2:stop"}, },
 *
 * {AlarmName: awsec2-johns-test-i-97c988e6-High-CPU-Utilization,
 * Timestamp: Mon Jan 14 12:38:31 CST 2013,
 * HistoryItemType: ConfigurationUpdate,
 * HistorySummary: Alarm "awsec2-johns-test-i-97c988e6-High-CPU-Utilization" created,
 * HistoryData: {"version":"1.0","type":"Create",
 * "createdAlarm":{"threshold":10.0,"namespace":"AWS/EC2",
 * "stateValue":"INSUFFICIENT_DATA","dimensions":
 * [{"name":"InstanceId","value":"i-97c988e6"}],"okactions":[],
 * "alarmActions":["arn:aws:automate:us-east-1:ec2:stop"],"evaluationPeriods":2,
 * "comparisonOperator":"LessThanThreshold","metricName":"CPUUtilization",
 * "period":60,"alarmName":"awsec2-johns-test-i-97c988e6-High-CPU-Utilization",
 * "statistic":"Average",
 * "alarmArn":"arn:aws:cloudwatch:us-east-1:016581053775:alarm:awsec2-johns-test-i-97c988e6-High-CPU-Utilization",
 * "alarmConfigurationUpdatedTimestamp":"2013-01-14T18:38:31.745+0000",
 * "stateUpdatedTimestamp":"2013-01-14T18:38:31.745+0000",
 * "insufficientDataActions":[],"alarmDescription":"Created from EC2 Console",
 * "actionsEnabled":true}}, }], }
 *
 *
 * @author jgardner
 *
 */
public class DescribeAlarmHistoryTest extends AbstractBaseMonitorQueryTest {

    private static Logger logger = Appctx.getLogger(DescribeAlarmHistoryTest.class
            .getName());

    private final String alarmBaseName = UUID.randomUUID().toString();

    @Autowired
    public String targetServer;

    @Test
    public void testDescribeAlarmHistoryBasic() {
        DescribeAlarmHistoryResult result = null;
        logger.info("Describing alarm history from: " + targetServer);
        final DescribeAlarmHistoryRequest request =
                new DescribeAlarmHistoryRequest();
        result = getCloudWatchClientV2().describeAlarmHistory(request);
        assertNotNull(result);
        logger.debug("Got result: " + result);
    }

    @Test
    public void testDescribeAlarmHistoryWithKnowns() {
        DescribeAlarmHistoryResult result = null;
        String name1 = "describe:1:"+alarmBaseName;
        String name2 = "describe:2:"+alarmBaseName;
        try {
            AlarmHelper.putMetricAlarm(name1, getCloudWatchClientV2());
            AlarmHelper.putMetricAlarm(name2, getCloudWatchClientV2());
            final DescribeAlarmHistoryRequest request =
                    new DescribeAlarmHistoryRequest();
            request.withAlarmName(name1);
            result = getCloudWatchClientV2().describeAlarmHistory(request);
        } finally {
            AlarmHelper.deleteAllCreatedAlarms(getCloudWatchClientV2());
        }
        assertNotNull(result);
        logger.debug("Got result: " + result);
        List<AlarmHistoryItem> foundAlarms = result.getAlarmHistoryItems();
        assertTrue("Expect at least created alarm histories to be found.",
                foundAlarms.size() >= 1);
        for (AlarmHistoryItem historyItem : foundAlarms) {
            assertEquals("Expect only histories for requested",
                    name1, historyItem.getAlarmName());
        }
    }

}
