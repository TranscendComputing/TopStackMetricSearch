package com.msi.monitorquery.basic;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.msi.monitorquery.integration.AbstractBaseMonitorQueryTest;

public class ConfigTest extends AbstractBaseMonitorQueryTest {

    @Autowired
    public String targetServer;

	@Test
	public void testSuccessfulConfigure() {
	    // Executing this method means no stacktrace on config load.
	}
}
