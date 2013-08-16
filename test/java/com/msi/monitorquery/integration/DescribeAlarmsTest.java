package com.msi.monitorquery.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.cloudwatch.model.DescribeAlarmsRequest;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmsResult;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;
import com.msi.monitorquery.helper.AlarmHelper;
import com.msi.tough.core.Appctx;

/**
 * Test describe alarms.
 *
 * Example from AWS:
 * {MetricAlarms: [{AlarmName: Terminate-if-idle,
 * AlarmArn: arn:aws:cloudwatch:us-east-1:016581053775:alarm:Terminate-if-idle,
 * AlarmConfigurationUpdatedTimestamp: Thu Jan 10 15:04:17 CST 2013,
 * ActionsEnabled: true, AlarmActions: [arn:aws:automate:us-east-1:ec2:stop],
 * StateValue: INSUFFICIENT_DATA,
 * StateReason: Insufficient Data: 12 datapoints were unknown.,
 * StateReasonData: {"version":"1.0","queryDate":"2013-01-10T22:43:06.332+0000",
 *                   "statistic":"Average","period":300,"recentDatapoints":[],
 *                   "threshold":5.0},
 * StateUpdatedTimestamp: Thu Jan 10 16:43:06 CST 2013,
 * MetricName: CPUUtilization, Namespace: AWS/EC2,
 * Statistic: Average,
 * Dimensions: [{Name: InstanceId, Value: i-0ff46c7e, }],
 * Period: 300, EvaluationPeriods: 12,
 * Threshold: 5.0, ComparisonOperator: LessThanThreshold, }]
 *
 * @author jgardner
 *
 */
public class DescribeAlarmsTest extends AbstractBaseMonitorQueryTest {

    private static Logger logger = Appctx.getLogger(DescribeAlarmsTest.class
            .getName());

    private final String alarmBaseName = UUID.randomUUID().toString()
            .substring(0, 8);

    @Autowired
    public String targetServer;

    private String name1 = "describe:1:"+alarmBaseName;
    private String name2 = "describe:2:"+alarmBaseName;

    private int created = 0;

    @Before
    public void setup() {
        created = 2;
        AlarmHelper.putMetricAlarm(name1, getCloudWatchClientV2());
        AlarmHelper.putMetricAlarm(name2, getCloudWatchClientV2());
    }

    @Test
    public void testDescribeAlarmsBasic() {
        DescribeAlarmsResult result = null;
        logger.info("Describing alarms from: " + targetServer);
        final DescribeAlarmsRequest request = new DescribeAlarmsRequest();
        result = getCloudWatchClientV2().describeAlarms(request);
        assertNotNull(result);
        logger.debug("Got result: " + result);
    }

    @Test
    public void testDescribeAlarmsWithKnowns() {
        DescribeAlarmsResult result = null;
        logger.info("Describing alarms from: " + targetServer);
        final DescribeAlarmsRequest request = new DescribeAlarmsRequest();
        result = getCloudWatchClientV2().describeAlarms(request);
        assertNotNull(result);
        List<MetricAlarm> foundAlarms = result.getMetricAlarms();
        assertTrue("Expect at least created alarms to be found.",
                foundAlarms.size() >= created);
        int expectToFind = 0;
        for (MetricAlarm alarm : foundAlarms) {
            logger.debug("Found alarm: " + alarm.getAlarmName());
            if (alarm.getAlarmName().equals(name1)) {
                expectToFind++;
            }
            if (alarm.getAlarmName().equals(name2)) {
                expectToFind++;
            }
        }
        assertEquals("Expect created alarms to be found among the " +
                foundAlarms.size() + " alarms found:",
                created, expectToFind);
    }

    @After
    public void teardown() {
        AlarmHelper.deleteAllCreatedAlarms(getCloudWatchClientV2());
    }
}
