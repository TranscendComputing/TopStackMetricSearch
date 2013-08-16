package com.msi.monitorquery.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.msi.monitorquery.helper.AlarmLocalHelper;
import com.msi.monitorquery.integration.AbstractBaseMonitorQueryTest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.ActionTestHelper;
import com.transcend.monitor.message.DescribeAlarmHistoryMessage.DescribeAlarmHistoryRequest;
import com.transcend.monitor.message.DescribeAlarmHistoryMessage.DescribeAlarmHistoryResponse;
import com.transcend.monitor.worker.DescribeAlarmHistoryWorker;

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
public class DescribeAlarmHistoryLocalTest extends AbstractBaseMonitorQueryTest {

    private static Logger logger = Appctx
            .getLogger(DescribeAlarmHistoryLocalTest.class.getName());

    @Autowired
    private ActionTestHelper actionHelper = null;

    @Resource
    DescribeAlarmHistoryWorker describeAlarmHistoryWorker = null;

    private final String alarmBaseName = UUID.randomUUID().toString()
            .substring(0, 8);

    private final String alarmName = "descHistLocal:"+alarmBaseName;

    private final String otherAlarmName = "descHistLocalOther:"+alarmBaseName;

    private final String bogusName = "not-a-real-alarm";

    /**
     * Construct a minimal valid describe alarm history request.
     *
     * @param alarmName
     * @return
     */
    public DescribeAlarmHistoryRequest.Builder describeAlarmRequest(String alarmName) {
        final DescribeAlarmHistoryRequest.Builder request =
                DescribeAlarmHistoryRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(actionHelper.getAccessKey());
        request.setRequestId(alarmName);
        request.setAction("DescAH");
        request.setAlarmName(alarmName);
        return request;
    }

    @Test
    public void testDescribeAlarmHistoryNone() throws Exception {
        DescribeAlarmHistoryResponse result = null;
        DescribeAlarmHistoryRequest.Builder request = describeAlarmRequest(bogusName);
        result = describeAlarmHistoryWorker.doWork(request.build());
        assertNotNull(result);
        assertEquals("Expect none with non-existent name",
                0, result.getAlarmHistoryItemList().size());
    }

    @Test
    public void testDescribeAlarmHistory() throws Exception {
        DescribeAlarmHistoryResponse result = null;
        DescribeAlarmHistoryRequest.Builder request = describeAlarmRequest(alarmName);
        try {
            AlarmLocalHelper.putMetricAlarm(alarmName);
            AlarmLocalHelper.putMetricAlarm(otherAlarmName);
            result = describeAlarmHistoryWorker.doWork(request.build());
            logger.debug("Got count:" + result.getAlarmHistoryItemList().size());
            logger.debug("Got result:" + result.toString());
        }
        finally {
            AlarmLocalHelper.deleteAllCreatedAlarms();
        }
        assertNotNull(result);
        assertEquals("Expect some history with created alarm",
                1, result.getAlarmHistoryItemList().size());
    }

}
