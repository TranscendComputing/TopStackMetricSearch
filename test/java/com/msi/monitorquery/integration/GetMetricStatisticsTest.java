package com.msi.monitorquery.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.msi.tough.core.Appctx;

public class GetMetricStatisticsTest extends AbstractBaseMonitorQueryTest {

    private static Logger logger = Appctx
            .getLogger(GetMetricStatisticsTest.class.getName());

    @Autowired
    public String targetServer;

    @Test(expected = AmazonServiceException.class)
    public void testGetMetricStatisticsSecurity() {

        final GetMetricStatisticsRequest getMetricStatisticsRequest =
                new GetMetricStatisticsRequest();
        getCloudWatchClientBadCreds().getMetricStatistics(
                getMetricStatisticsRequest);
    }

    @Test
    public void testGetMetricStatistics() {
        GetMetricStatisticsResult result = null;
        logger.info("Getting metric statistics from: " + targetServer);
        final GetMetricStatisticsRequest getMetricStatisticsRequest =
                new GetMetricStatisticsRequest();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date dayBefore = cal.getTime();
        getMetricStatisticsRequest
                .withNamespace("AWS/EC2")
                .withDimensions(
                        new Dimension().withName("InstanceId").withValue(
                                "i-00000561")).withPeriod(300)
                .withStartTime(dayBefore).withEndTime(yesterday)
                .withMetricName("CPUUtilization").withStatistics("Average")
                .withUnit(StandardUnit.Percent);
        result = getCloudWatchClientV2().getMetricStatistics(
                getMetricStatisticsRequest);
        // logger.debug(result.toString());
        assertNotNull(result);
        assertEquals(getMetricStatisticsRequest.getMetricName(),
                result.getLabel());
    }

    @Test
    public void testGetMetricStatisticsSampleCount() {
        GetMetricStatisticsResult result = null;
        logger.info("Getting metric statistics from: " + targetServer);
        final GetMetricStatisticsRequest getMetricStatisticsRequest =
                new GetMetricStatisticsRequest();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date dayBefore = cal.getTime();
        getMetricStatisticsRequest
                .withNamespace(DEFAULT_NAMESPACE)
                .withDimensions(
                        new Dimension().withName("InstanceId").withValue(
                                "i-00000561")).withPeriod(60)
                .withStartTime(dayBefore).withEndTime(yesterday)
                .withMetricName("CPUUtilization").withStatistics("SampleCount")
                .withUnit(StandardUnit.Count);
        result = getCloudWatchClientV2().getMetricStatistics(
                getMetricStatisticsRequest);
        // logger.debug(result.toString());
        assertNotNull(result);
        assertEquals(getMetricStatisticsRequest.getMetricName(),
                result.getLabel());
    }
}
