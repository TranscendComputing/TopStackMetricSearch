package com.msi.monitorquery.longrunning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.services.cloudwatch.model.AlarmHistoryItem;
import com.amazonaws.services.cloudwatch.model.ComparisonOperator;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmHistoryRequest;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmHistoryResult;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.amazonaws.services.cloudwatch.model.Statistic;
import com.msi.monitorquery.integration.AbstractBaseMonitorQueryTest;
import com.msi.tough.core.Appctx;

/**
 * Monitor should be able to terminate instances based on alarm conditions.
 * @author jgardner
 *
 */
public class AlarmTerminateTest extends AbstractBaseMonitorQueryTest {

    private static boolean DEBUG = true;

    private static Logger logger = Appctx.getLogger(AlarmTerminateTest.class
            .getName());

    private String instanceId = "da8e73a8-4c9e-4eab-b272-e9a084068f7d";
    private final String alarmName = "terminate-on-idle:" +
            UUID.randomUUID().toString().substring(0,8);
    //@Before
    public void startAnInstanceAndWait() throws Exception {
        //TODO: need compute service to have run instances completed.
        // Look for CPU Usage less than 90% for 2 periods of 1 minute.
        // (which should happen as soon as 2 minutes are collected, since
        // the instance is idle.)
        final PutMetricAlarmRequest request = new PutMetricAlarmRequest()
        .withNamespace(DEFAULT_NAMESPACE)
        .withActionsEnabled(true)
        .withAlarmName(alarmName)
        .withAlarmDescription(alarmName)
        .withEvaluationPeriods(2)
        .withMetricName("CPUUtilization")
        .withPeriod(60)
        .withStatistic(Statistic.Average)
        .withComparisonOperator(ComparisonOperator.LessThanOrEqualToThreshold)
        .withThreshold(90d)
        .withDimensions(new Dimension().withName("InstanceId")
                .withValue(instanceId));
        // Send a stop command when the alarm fires.  Region is ignored.
        request.withAlarmActions("arn:aws:automate::ec2:stop");
        getCloudWatchClientV2().putMetricAlarm(request);
    }

    @Test
    public void testAlarmCanTerminateInstance() throws Exception {
        final DescribeAlarmHistoryRequest request =
                new DescribeAlarmHistoryRequest()
                    .withAlarmName(alarmName);
        DescribeAlarmHistoryResult result =
                getCloudWatchClientV2().describeAlarmHistory(request);
        assertNotNull(result);
        if (DEBUG) {
            logger.debug("Got result:"+result);
        }
        List<AlarmHistoryItem> alarmHistoryItems =
                result.getAlarmHistoryItems();
        for (AlarmHistoryItem item : alarmHistoryItems) {
            assertEquals("Expect alarms to fire.",
                    alarmName, item.getAlarmName());
            assertNotNull(item.getTimestamp());
            assertNotNull(item.getHistoryData());
            assertNotNull(item.getHistoryItemType());
            assertNotNull(item.getHistorySummary());
        }
        assertTrue("Expect alarms to fire.",
                alarmHistoryItems.size() >= 1);
        //TODO: run describe instances and verify instance is gone.
    }

    @After
    public void tearDown() {
        //TODO: need compute service to have terminate instances completed.
        //TODO: terminate created instanced, in case of test failure.
        //AlarmHelper.deleteAlarm(alarmName, getCloudWatchClient());
    }
}
