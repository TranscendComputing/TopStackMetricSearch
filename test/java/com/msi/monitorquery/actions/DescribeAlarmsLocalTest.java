package com.msi.monitorquery.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.msi.monitorquery.helper.AlarmLocalHelper;
import com.msi.monitorquery.integration.AbstractBaseMonitorQueryTest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.ActionTestHelper;
import com.msi.tough.query.ErrorResponse;
import com.transcend.monitor.message.DescribeAlarmsMessage.DescribeAlarmsRequest;
import com.transcend.monitor.message.DescribeAlarmsMessage.DescribeAlarmsResponse;
import com.transcend.monitor.message.MetricAlarmMessage.MetricAlarm;
import com.transcend.monitor.worker.DescribeAlarmsWorker;

/**
 * Test describe alarm in local.
 *
 * Example from AWS:
 *
 *
 * @author jgardner
 *
 */
public class DescribeAlarmsLocalTest extends AbstractBaseMonitorQueryTest {

    private static Logger logger = Appctx
            .getLogger(DescribeAlarmHistoryLocalTest.class.getName());

    @Resource
    private ActionTestHelper actionTestHelper = null;

    @Resource
    private DescribeAlarmsWorker describeAlarmsWorker = null;

    private final String alarmBaseName = UUID.randomUUID().toString()
            .substring(0, 8);

    private final String bogusName = "not-a-real-alarm";

    String name1 = "descLocal:1:" + alarmBaseName;
    String name2 = "descLocal:2:" + alarmBaseName;

    private int created = 0;

    @Before
    public void putAlarm() throws Exception {
        AlarmLocalHelper.putMetricAlarm(name1);
        AlarmLocalHelper.putMetricAlarm(name2);
        created += 2;
    }

    @Test
    public void testDescribeAlarmsNone() throws Exception {
        DescribeAlarmsResponse result = null;
        final DescribeAlarmsRequest.Builder request = AlarmLocalHelper.describeAlarmRequest(bogusName);
        result = describeAlarmsWorker.doWork(request.build());
        assertNotNull(result);
        assertEquals("Expect none with non-existent name", 0, result
                .getMetricAlarmList().size());
    }

    @Test(expected=ErrorResponse.class)
    public void testDescribeAlarmsBadState() throws Exception {
        // Using Protobuf with an enum, can't actually set bad value.
        throw new ErrorResponse("", "", "");
    }

    @Test
    public void testDescribeAlarmsWithKnowns() throws Exception {
        DescribeAlarmsResponse resultAll = null;
        DescribeAlarmsResponse resultOne = null;
        final DescribeAlarmsRequest.Builder request = AlarmLocalHelper.describeAlarmRequest(name1);
        resultOne = describeAlarmsWorker.doWork(request.build());
        request.clearAlarmName();
        resultAll = describeAlarmsWorker.doWork(request.build());
        assertNotNull(resultAll);
        logger.debug("Got result:" + resultAll.toString());

        List<MetricAlarm> foundAlarms = resultAll.getMetricAlarmList();
        assertTrue("Expect at least created alarms to be found.",
                foundAlarms.size() >= created);
        int expectToFind = 0;
        for (MetricAlarm alarm : foundAlarms) {
            if (alarm.getAlarmName().equals(name1)) {
                expectToFind++;
            }
            if (alarm.getAlarmName().equals(name2)) {
                expectToFind++;
            }
        }
        assertEquals("Expect created alarms to be found.", created,
                expectToFind);
        foundAlarms = resultOne.getMetricAlarmList();
        assertEquals("Expect created alarms to be found.", 1,
                foundAlarms.size());
    }

    @Test
    public void testDescribeAlarmsByName() throws Exception {
        DescribeAlarmsResponse result = null;
        final DescribeAlarmsRequest.Builder request = AlarmLocalHelper.describeAlarmRequest(name1);
        result = describeAlarmsWorker.doWork(request.build());
        assertNotNull(result);
        List<MetricAlarm> foundAlarms = result.getMetricAlarmList();
        assertEquals("Expect only requested # alarms to be found.",
                1, foundAlarms.size());
        assertEquals("Expect requested alarm to be found.",
                name1, foundAlarms.get(0).getAlarmName());
    }

    @Test
    public void testDescribeAlarmsMaxRecords() throws Exception {
        DescribeAlarmsResponse result = null;
        final DescribeAlarmsRequest.Builder request = AlarmLocalHelper.describeAlarmRequest(name1);
        request.clearAlarmName();
        request.setMaxRecords(1);
        result = describeAlarmsWorker.doWork(request.build());
        assertNotNull(result);
        List<MetricAlarm> foundAlarms = result.getMetricAlarmList();
        assertEquals("Expect only requested # alarms to be found.",
                1, foundAlarms.size());
    }

    @After
    public void cleanupCreated() throws Exception {
        AlarmLocalHelper.deleteAllCreatedAlarms();
    }
}
