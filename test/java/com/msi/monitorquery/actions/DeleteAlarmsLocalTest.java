package com.msi.monitorquery.actions;

import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.msi.monitorquery.helper.AlarmLocalHelper;
import com.msi.monitorquery.integration.AbstractBaseMonitorQueryTest;
import com.msi.tough.monitor.common.manager.db.AlarmStoreClient;
import com.msi.tough.query.ActionTestHelper;
import com.msi.tough.query.ErrorResponse;
import com.transcend.monitor.message.DeleteAlarmsMessage.DeleteAlarmsRequest;
import com.transcend.monitor.worker.DeleteAlarmsWorker;

/**
 * Test delete alarms locally.
 *
 * Example from AWS:
 *
 *
 * @author jgardner
 *
 */
public class DeleteAlarmsLocalTest extends AbstractBaseMonitorQueryTest {

    private final String alarmBaseName = UUID.randomUUID().toString()
            .substring(0, 8);

    private final String bogusName = "not-a-real-alarm";

    String name1 = "delLocal:1:" + alarmBaseName;
    String name2 = "delLocal:2:" + alarmBaseName;

    private HashSet<String> remaining = new HashSet<String>();

    @Autowired
    private ActionTestHelper actionHelper = null;

    @Resource(name="MON_ALARMSTORE")
    AlarmStoreClient alarmStoreClient = null;

    @Resource
    DeleteAlarmsWorker deleteAlarmsWorker = null;

    @Before
    public void putAlarms() throws Exception {
        AlarmLocalHelper.putMetricAlarm(name1);
        remaining.add(name1);
        AlarmLocalHelper.putMetricAlarm(name2);
        remaining.add(name2);
    }

    @Test(expected=ErrorResponse.class)
    public void testDeleteAlarmsNone() throws Exception {
        DeleteAlarmsRequest request =
                AlarmLocalHelper.deleteAlarmsRequest(bogusName).build();
        Object result = deleteAlarmsWorker.doWork(request);
        assertNotNull(result);
    }

    @Test
    public void testDeleteAlarmsByName() throws Exception {
        Object result = null;
        DeleteAlarmsRequest request =
                AlarmLocalHelper.deleteAlarmsRequest(name1).build();
        result = deleteAlarmsWorker.doWork(request);
        assertNotNull(result);
        remaining.remove(name1);
    }

    @Test
    public void testDeleteAlarmChildren() throws Exception {
        Object result = null;
        DeleteAlarmsRequest request =
                AlarmLocalHelper.deleteAlarmsRequest(name1).build();
        result = deleteAlarmsWorker.doWork(request);
        assertNotNull(result);
        remaining.remove(name1);
    }

    @After
    public void cleanupCreated() throws Exception {
        if (remaining.size() == 0) {
            return;
        }

        DeleteAlarmsRequest.Builder request = null;
        for (String alarmName : remaining) {
            if (request == null) {
                request = AlarmLocalHelper.deleteAlarmsRequest(alarmName);
            } else {
                request.addAlarmName(alarmName);
            }
        }
        deleteAlarmsWorker.doWork(request.build());
    }

    /**
     * Selectively enabled for cleanups.
     * @throws Exception
     */
    //@After
    public void cleanupManual() throws Exception {
        DeleteAlarmsRequest.Builder request = null;
        String[] vals = {
                "delLocal:2:157ba863",
                "delLocal:2:0fd7fd0a",
                "delLocal:2:2699439e",
                "delLocal:2:054bda65"
        };
        for (String alarmName : vals) {
            if (request == null) {
                request = AlarmLocalHelper.deleteAlarmsRequest(alarmName);
            } else {
                request.addAlarmName(alarmName);
            }
        }
        try {
            deleteAlarmsWorker.doWork(request.build());
        } catch (Exception e) {
            System.out.println("Didn't delete manual items.");
        }

    }
}
