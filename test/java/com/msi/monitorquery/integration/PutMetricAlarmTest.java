package com.msi.monitorquery.integration;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.msi.monitorquery.helper.AlarmHelper;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.monitor.DimensionBean;

public class PutMetricAlarmTest extends AbstractBaseMonitorQueryTest {

    private static Logger logger = Appctx
            .getLogger(PutMetricAlarmTest.class.getName());

    private final String alarmBaseName = UUID.randomUUID().toString()
            .substring(0, 8);
    private final String alarmName = "put:" + alarmBaseName;

    @Autowired
    public String targetServer;

    @Test(expected=AmazonServiceException.class)
    public void testPutMetricAlarmEmpty() {
        final PutMetricAlarmRequest request = new PutMetricAlarmRequest();
        // Should fail, none of the required data.
        getCloudWatchClientV2().putMetricAlarm(request);
    }

    @Test
    public void testPutMinimalMetricAlarm() {
        try {
            logger.info("Put alarm at: " + targetServer);
            final PutMetricAlarmRequest request =
                    AlarmHelper.metricAlarmRequest(alarmName);
            getCloudWatchClientV2().putMetricAlarm(request);
        }
        finally {
            logger.debug("Deleting minimal alarm " + alarmName);
            try {
                AlarmHelper.deleteAlarm(alarmName, getCloudWatchClientV2());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testPutMetricAlarmFull() {
        MetricAlarm alarm;
        final PutMetricAlarmRequest request =
                AlarmHelper.metricAlarmRequest(alarmName);
        try {
            logger.debug("Making alarm " + alarmName + "at: " + targetServer);
            request.withActionsEnabled(true)
                .withAlarmActions("arn:aws:bogus:but:legal:action")
                .withDimensions(new Dimension()
                .withName(DimensionBean.DIMENSION_INSTANCE_ID)
                .withValue("instance123"));
            getCloudWatchClientV2().putMetricAlarm(request);
            alarm = AlarmHelper.describeSingleAlarm(alarmName,
                    getCloudWatchClientV2());
        }
        catch (RuntimeException re) {
            logger.warn("Exception while creating alarm: " + alarmName, re);
            throw re;
        }
        finally {
            try {
                logger.debug("Deleting full alarm " + alarmName);
                AlarmHelper.deleteAlarm(alarmName, getCloudWatchClientV2());
            } catch (RuntimeException e) {
                logger.warn("Failed to delete alarm: " + alarmName);
                AlarmHelper.deleteAlarm(alarmName, getCloudWatchClientV2());
                logger.warn("2nd attempt to delete succeeded on "  + alarmName);
            }
        }
        assertEquals("Expect created alarms match.",
                alarm.getAlarmDescription(), alarmName);
        List<Dimension> dims = alarm.getDimensions();
        assertEquals("Expect created alarms match.",
                1, dims.size());
        assertEquals("Expect created dimensions match.",
                DimensionBean.DIMENSION_INSTANCE_ID, dims.get(0).getName());
        assertEquals("Expect created dimensions match.",
                "instance123", dims.get(0).getValue());
    }
}
