package com.msi.monitorquery.integration;

import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudwatch.model.DeleteAlarmsRequest;
import com.msi.monitorquery.helper.AlarmHelper;
import com.msi.tough.core.Appctx;

public class DeleteAlarmsTest extends AbstractBaseMonitorQueryTest {

    private static Logger logger = Appctx
            .getLogger(DeleteAlarmsTest.class.getName());

    private final String alarmName = "delete:" + UUID.randomUUID().toString();

    @Autowired
    public String targetServer;

    @Test(expected=AmazonServiceException.class)
    public void testDeleteMetricAlarmNotThere() {
        logger.info("Delete alarm at: " + targetServer);
        final DeleteAlarmsRequest request = new DeleteAlarmsRequest()
            .withAlarmNames("not-a-real-name");
        getCloudWatchClientV2().deleteAlarms(request);
    }

    @Test
    public void testDeleteAlarm() {
        logger.debug("Creating alarm: " + alarmName);
        AlarmHelper.putMetricAlarm(alarmName, getCloudWatchClientV2());
        logger.info("Delete alarm at: " + targetServer);
        AlarmHelper.deleteAlarm(alarmName, getCloudWatchClientV2());
    }

    @Test
    @Ignore // Selectively comment this to run a one-off delete for debugging.
    /**
     * Private utility to delete a named alarm.
     */
    public void testDeleteSpecificAlarm() {
        String specificAlarm = "terminate-on-idle:01edaced";
        logger.info("Delete alarm at: " + targetServer);
        AlarmHelper.deleteAlarm(specificAlarm, getCloudWatchClientV2());
    }
}
