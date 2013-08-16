package com.msi.monitorquery.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.msi.monitorquery.helper.AlarmLocalHelper;
import com.msi.monitorquery.integration.AbstractBaseMonitorQueryTest;
import com.msi.tough.core.Appctx;
import com.msi.tough.monitor.common.manager.db.AlarmStoreClient;
import com.msi.tough.query.ActionTestHelper;
import com.transcend.monitor.message.DescribeAlarmsForMetricMessage.DescribeAlarmsForMetricRequest;
import com.transcend.monitor.message.DescribeAlarmsForMetricMessage.DescribeAlarmsForMetricResponse;
import com.transcend.monitor.message.MetricAlarmMessage.MetricAlarm;
import com.transcend.monitor.worker.DescribeAlarmsForMetricWorker;

/**
 * Test describe alarm by metric.
 *
 * Example from AWS:
 *
 *
 * @author jgardner
 *
 */
public class DescribeAlarmsForMetricLocalTest extends AbstractBaseMonitorQueryTest {

    private static Logger logger = Appctx
            .getLogger(DescribeAlarmsForMetricLocalTest.class.getName());

    @Autowired
    private ActionTestHelper actionHelper = null;

    @Resource
    private DescribeAlarmsForMetricWorker describeAlarmsForMetricWorker = null;

    @Resource(name="MON_ALARMSTORE")
    AlarmStoreClient alarmStoreClient = null;

    private final String alarmName = "descForMLocal:"+
            UUID.randomUUID().toString().substring(0, 8);

    private final String bogusName = "not-a-real-metric";

    private final String metricName = "CPUUtilization";

    /**
     * Construct a minimal valid describe alarms for metric request.
     *
     * @param alarmName
     * @return
     */
    public DescribeAlarmsForMetricRequest.Builder describeAlarmRequest(String metricName) {
        final DescribeAlarmsForMetricRequest.Builder request =
                DescribeAlarmsForMetricRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(actionHelper.getAccessKey());
        request.setRequestId(alarmName);
        request.setAction("DescAFM");
        request.setNamespace(DEFAULT_NAMESPACE);
        request.setMetricName(metricName);
        return request;
    }


    @Test
    public void testDescribeAlarmsForMetricNone() throws Exception {
        DescribeAlarmsForMetricResponse result = null;
        DescribeAlarmsForMetricRequest.Builder request = describeAlarmRequest(bogusName);
        result = describeAlarmsForMetricWorker.doWork(request.build());
        assertNotNull(result);
        assertEquals("Expect none with non-existent name",
                0, result.getMetricAlarmList().size());
    }

    @Test
    public void testDescribeAlarmsForMetric() throws Exception {
        int created = 0;

        DescribeAlarmsForMetricResponse result = null;
        DescribeAlarmsForMetricRequest.Builder request = describeAlarmRequest(metricName);
        try {
            AlarmLocalHelper.putMetricAlarm(alarmName);
            created++;
            result = describeAlarmsForMetricWorker.doWork(request.build());
        }
        finally {
            AlarmLocalHelper.deleteAlarm(alarmName);
        }
        assertNotNull(result);
        List<MetricAlarm> foundAlarms = result.getMetricAlarmList();
        assertTrue("Expect at least created alarms to be found.",
                foundAlarms.size() >= created);
        int expectToFind = 0;
        for (MetricAlarm alarm : foundAlarms) {
            logger.debug("Found alarm: " + alarm.getAlarmName());
            if (alarm.getAlarmName().equals(alarmName)) {
                expectToFind++;
            }
        }
        assertEquals("Expect created alarms to be found.",
                created, expectToFind);
    }

}
