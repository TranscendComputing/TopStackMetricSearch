package com.msi.monitorquery.longrunning;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;

import com.msi.monitorquery.integration.AbstractBaseMonitorQueryTest;
import com.msi.tough.core.Appctx;

public class GetMetricStatisticsServerTest extends AbstractBaseMonitorQueryTest {

    private static Logger logger = Appctx
            .getLogger(GetMetricStatisticsServerTest.class.getName());

    @Test
    public void testGetMetricStatistics() throws Exception {

        // TODO: launch a tracked instance, collect some stats for better test.
        logger.debug("No-op test.");
        assertEquals(1, 1);
    }

    @After
    public void tearDown() {
    }
}
