package com.msi.monitorquery.integration;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-monitorQueryContext.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public abstract class AbstractBaseMonitorQueryTest {

    public static final String DEFAULT_NAMESPACE = "AWS/EC2";

	@Resource(name="basicAWSCredentials")
	private AWSCredentials creds;

    //@Resource(name="cloudWatchClient")
	//private AmazonCloudWatchClient cloudWatchClient;

    @Resource(name="cloudWatchClientV2")
    private AmazonCloudWatchClient cloudWatchClientV2;

    @Resource(name="cloudWatchClientBadCreds")
    private AmazonCloudWatchClient cloudWatchClientBadCreds;

	@Autowired
	private String defaultAvailabilityZone;

    public AWSCredentials getCreds() {
		return creds;
	}
	public void setCreds(AWSCredentials creds) {
		this.creds = creds;
	}
	/*
	public AmazonCloudWatchClient getCloudWatchClient() {
		return cloudWatchClient;
	}
	public void setCloudWatchClient(AmazonCloudWatchClient cwc) {
		this.cloudWatchClient = cwc;
	}*/
    public AmazonCloudWatchClient getCloudWatchClientV2() {
        return cloudWatchClientV2;
    }
    public void setCloudWatchClientV2(AmazonCloudWatchClient cwc) {
        this.cloudWatchClientV2 = cwc;
    }
    public String getDefaultAvailabilityZone() {
        return defaultAvailabilityZone;
    }
    public void setDefaultAvailabilityZone(String defaultAvailabilityZone) {
        this.defaultAvailabilityZone = defaultAvailabilityZone;
    }
    /**
     * @return the cloudWatchClientBadCreds
     */
    public AmazonCloudWatchClient getCloudWatchClientBadCreds() {
        return cloudWatchClientBadCreds;
    }
    /**
     * @param cloudWatchClientBadCreds the cloudWatchClientBadCreds to set
     */
    public void setCloudWatchClientBadCreds(AmazonCloudWatchClient cloudWatchClientBadCreds) {
        this.cloudWatchClientBadCreds = cloudWatchClientBadCreds;
    }
}
