package com.msi.monitorquery.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;

import com.msi.monitorquery.helper.MetricStatisticsLocalHelper.GetMetricStatisticsMockRequest;
import com.msi.monitorquery.integration.AbstractBaseMonitorQueryTest;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.DateHelper;
import com.msi.tough.monitor.common.MonitorConstants;
import com.msi.tough.query.ActionTestHelper;
import com.msi.tough.query.ErrorResponse;
import com.transcend.monitor.actions.GetMetricStatistics;
import com.transcend.monitor.message.GetMetricStatisticsMessage.GetMetricStatisticsRequest;
import com.transcend.monitor.message.GetMetricStatisticsMessage.GetMetricStatisticsResponse;
import com.transcend.monitor.message.GetMetricStatisticsMessage.GetMetricStatisticsResponse.Datapoint;
import com.transcend.monitor.worker.GetMetricStatisticsWorker;

/**
 * Test describe alarm by metric.
 *
 * Example from AWS:
 *
 *
 * @author jgardner
 *
 */
public class GetMetricStatisticsLocalTest extends AbstractBaseMonitorQueryTest {
    private static Logger logger = Appctx.getLogger(GetMetricStatisticsLocalTest.class
            .getName());

    @Resource
    private ActionTestHelper actionTestHelper = null;

    @Resource
    private GetMetricStatistics getMetricStatistics = null;

    @Resource
    private GetMetricStatisticsWorker getMetricStatisticsWorker = null;

    private final String bogusName = "not-a-real-metric";

    private final String metricName = "CPUUtilization";
    private final String metricUnit = "Percent";

    /**
     * Construct a minimal valid describe alarms for metric request.
     *
     * @param alarmName
     * @return
     */
    public GetMetricStatisticsRequest.Builder getStatsRequest(String metricName) {
        final GetMetricStatisticsRequest.Builder request =
                GetMetricStatisticsRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(actionTestHelper.getAccessKey());
        request.setRequestId(metricName);
        request.setAction("GetMetStat");
        request.setNamespace(DEFAULT_NAMESPACE);
        request.setMetricName(metricName);
        return request;
    }

    @Test(expected=ErrorResponse.class)
    public void testGetMetricStatisticsNone() throws Exception {
        final GetMetricStatisticsMockRequest request =
                new GetMetricStatisticsMockRequest();
        request.put(MonitorConstants.NODE_METRICNAME, bogusName);
        GetMetricStatistics getMetricStatistics =
                new GetMetricStatistics();
        actionTestHelper.invokeRequest(getMetricStatistics, request);
    }

    @Test(expected=ErrorResponse.class)
    public void testGetMetricStatisticsNoDates() throws Exception {
        final GetMetricStatisticsMockRequest request =
                new GetMetricStatisticsMockRequest();
        request.put(MonitorConstants.NODE_METRICNAME, metricName);
        request.put(MonitorConstants.NODE_PERIOD, 120);
        request.withStatistic("Average");
        GetMetricStatistics getMetricStatistics =
                new GetMetricStatistics();
        actionTestHelper.invokeRequest(getMetricStatistics, request);
    }

    @Test(expected=ErrorResponse.class)
    public void testGetMetricStatisticsTimesDontOverlap() throws Exception {
        final GetMetricStatisticsMockRequest request =
                new GetMetricStatisticsMockRequest();
        request.put(MonitorConstants.NODE_METRICNAME, metricName);
        request.put(MonitorConstants.NODE_PERIOD, 120);
        final Calendar time = Calendar.getInstance(TimeZone
                .getTimeZone("GMT"));
        time.add(Calendar.DAY_OF_YEAR, -2);
        request.put(MonitorConstants.NODE_ENDTIME,
                DateHelper.getISO8601Date(time.getTime()));
        time.add(Calendar.DAY_OF_YEAR, +1);
        request.put(MonitorConstants.NODE_STARTTIME,
                DateHelper.getISO8601Date(time.getTime()));
        request.withStatistic("Average");
        GetMetricStatistics getMetricStatistics =
                new GetMetricStatistics();
        actionTestHelper.invokeRequest(getMetricStatistics, request);
    }

    @Test(expected=ErrorResponse.class)
    public void testGetMetricStatisticsNoStats() throws Exception {
        final GetMetricStatisticsMockRequest request =
                new GetMetricStatisticsMockRequest();
        request.put(MonitorConstants.NODE_METRICNAME, metricName);
        request.put(MonitorConstants.NODE_PERIOD, 120);
        final Calendar time = Calendar.getInstance(TimeZone
                .getTimeZone("GMT"));
        time.add(Calendar.DAY_OF_YEAR, -2);
        request.put(MonitorConstants.NODE_ENDTIME,
                DateHelper.getISO8601Date(time.getTime()));
        time.add(Calendar.DAY_OF_YEAR, -3);
        request.put(MonitorConstants.NODE_STARTTIME,
                DateHelper.getISO8601Date(time.getTime()));
        GetMetricStatistics getMetricStatistics =
                new GetMetricStatistics();
        actionTestHelper.invokeRequest(getMetricStatistics, request);
    }

    @Test(expected=ErrorResponse.class)
    public void testGetMetricStatisticsBadStat() throws Exception {
        final GetMetricStatisticsMockRequest request =
                new GetMetricStatisticsMockRequest();
        request.put(MonitorConstants.NODE_METRICNAME, metricName);
        request.put(MonitorConstants.NODE_PERIOD, 120);
        final Calendar time = Calendar.getInstance(TimeZone
                .getTimeZone("GMT"));
        time.add(Calendar.DAY_OF_YEAR, -2);
        request.put(MonitorConstants.NODE_ENDTIME,
                DateHelper.getISO8601Date(time.getTime()));
        time.add(Calendar.DAY_OF_YEAR, -3);
        request.put(MonitorConstants.NODE_STARTTIME,
                DateHelper.getISO8601Date(time.getTime()));
        request.withStatistic("Badonk-a-donk");
        GetMetricStatistics getMetricStatistics =
                new GetMetricStatistics();
        actionTestHelper.invokeRequest(getMetricStatistics, request);
    }

    @Test
    public void testGetMetricStatistics() throws Exception {
        GetMetricStatisticsResponse result = null;
        final GetMetricStatisticsMockRequest request =
                new GetMetricStatisticsMockRequest();
        request.put(MonitorConstants.NODE_METRICNAME, metricName);
        request.put(MonitorConstants.NODE_PERIOD, 60);
        final Calendar time = Calendar.getInstance(TimeZone
                .getTimeZone("GMT"));
        time.add(Calendar.DAY_OF_YEAR, -2);
        request.put(MonitorConstants.NODE_ENDTIME,
                DateHelper.getISO8601Date(time.getTime()));
        time.add(Calendar.DAY_OF_YEAR, -3);
        request.put(MonitorConstants.NODE_STARTTIME,
                DateHelper.getISO8601Date(time.getTime()));
        request.withStatistic("Average");
        request.withDimensionName("InstanceId");
        request.withDimensionValue("da8e73a8-4c9e-4eab-b272-e9a084068f7d");
        GetMetricStatistics getMetricStatistics =
                new GetMetricStatistics();
        GetMetricStatisticsRequest builtRequest = null;
        builtRequest = actionTestHelper.invokeRequest(getMetricStatistics, request);
        result = getMetricStatisticsWorker.doWork(builtRequest);
        List<Datapoint> foundPoints = result.getDataPointList();
        logger.debug("Got "+foundPoints.size()+" datapoints.");
        for (int i = 0; i < foundPoints.size() && i < 10; i++) {
            Datapoint point = foundPoints.get(i);
            assertNotNull(point.getUnit());
            assertEquals("Expect unit matching metric",
                    metricUnit, point.getUnit());
            assertNotNull(foundPoints.get(i).getTimestamp());
        }
        /* TODO: Ensure some datapoints are captured.
        assertTrue("Expect at least basic metrics to be found.",
                foundMeasures.size() >= created);
        int expectToFind = 0;
        for (Datapoint point : foundMeasures) {
            // validate
        }
        assertEquals("Expect datapoints to be found.",
                0, foundPoints.size());
        */
    }

}
