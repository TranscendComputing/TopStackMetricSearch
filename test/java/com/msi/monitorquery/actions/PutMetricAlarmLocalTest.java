package com.msi.monitorquery.actions;

import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.msi.monitorquery.helper.AlarmLocalHelper;
import com.msi.monitorquery.integration.AbstractBaseMonitorQueryTest;
import com.msi.tough.monitor.common.manager.db.AlarmStoreClient;
import com.msi.tough.query.ActionTestHelper;
import com.msi.tough.query.ErrorResponse;
import com.transcend.monitor.message.MetricAlarmMessage.MetricAlarmToPut;
import com.transcend.monitor.message.PutMetricAlarmMessage.PutMetricAlarmRequest;
import com.transcend.monitor.worker.PutMetricAlarmWorker;

/**
 * Test create metric alarm.
 *
 * Example from AWS:
 *
 *
 * @author jgardner
 *
 */
public class PutMetricAlarmLocalTest extends AbstractBaseMonitorQueryTest {

    @Autowired
    private ActionTestHelper actionHelper = null;

    @Resource(name="MON_ALARMSTORE")
    AlarmStoreClient alarmStoreClient = null;

    @Resource
    PutMetricAlarmWorker putMetricAlarmWorker = null;

    private final String alarmName = "putLocal:"+
            UUID.randomUUID().toString().substring(0, 8);

    /**
     * Construct a minimal valid configure health check request.
     *
     * @param lbName
     * @return
     */
    public PutMetricAlarmRequest.Builder putAlarmRequest(String lbName) {
        final PutMetricAlarmRequest.Builder request =
                PutMetricAlarmRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(getCreds().getAWSAccessKeyId());
        request.setRequestId(lbName);
        request.setAction("PutMA");
        MetricAlarmToPut.Builder alarm = MetricAlarmToPut.newBuilder();
        request.setMetricAlarm(alarm.build());
        return request;
    }

    @Test(expected=Exception.class)//(expected=ErrorResponse.class)
    public void testPutMetricAlarmEmpty() throws Exception {
        final PutMetricAlarmRequest.Builder request =
                AlarmLocalHelper.metricAlarmRequest(alarmName);
        request.clearMetricAlarm();
        putMetricAlarmWorker.doWork(request.build());
    }

    @Test
    public void testPutMinimalMetricAlarm() {
    }

    @Test
    public void testPutMetricAlarmFull() throws Exception {
        try {
            AlarmLocalHelper.putMetricAlarm(alarmName);
        }
        finally {
            AlarmLocalHelper.deleteAlarm(alarmName);
        }
    }

    @Test(expected=ErrorResponse.class)
    public void testPutMetricAlarmDup() throws Exception {
        try {
            AlarmLocalHelper.putMetricAlarm(alarmName);
            AlarmLocalHelper.putMetricAlarm(alarmName);
        }
        finally {
            AlarmLocalHelper.deleteAlarm(alarmName);
        }
    }
}
