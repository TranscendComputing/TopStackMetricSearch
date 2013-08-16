package com.transcend.monitor.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;

import com.generationjava.io.xml.XMLNode;

import com.msi.tough.model.ServiceBean;
import com.msi.tough.model.monitor.ServiceHealthEventBean;
import com.msi.tough.model.monitor.enums.ServiceHealthStatus;
import com.msi.tough.monitor.common.MonitorConstants;
import com.msi.tough.monitor.common.MarshallingUtils;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;

public class PutServiceHealthEvent extends AbstractAction<Object>
{
    private static final Logger logger = Appctx.getLogger(PutServiceHealthEvent.class
        .getName());
    private static final String RootNodeName = "PutServiceHealthEvent";
    private static final String RootNodeNameResult = "PutServiceHealthEventResult";

    private String serviceAbbreviation ;
    private String region;
    private String availabilityZone;
    private String healthEventDescription ;
    private ServiceHealthStatus serviceHealthStatus ;

//    private ServiceBean serviceBean ;
    private ServiceHealthEventBean serviceHealthEventBean ;


    // This action is not AWS Compatible, so the Marshall/UnMarshall code
    // is defined here, not related to the AWS SDK


	public String marshall(
			MarshallStruct<Object> input,
			HttpServletResponse resp) throws Exception
	{
		logger.debug("Marshalling PutServiceHealthEvent...");

		XMLNode messageNode = new XMLNode(RootNodeName);

		XMLNode resultWrapNode = QueryUtil.addNode(messageNode, RootNodeNameResult);

		MarshallingUtils.marshallServiceHealthEvent( resultWrapNode, serviceHealthEventBean ) ;

		input.addResponseMetadata(messageNode, null);

		return messageNode.toString();
	}


	public void unmarshall(HttpServletRequest req, Map<String, String[]> map)
	{
		logger.debug("Unmarshalling PutServiceHealthEvent");

		serviceAbbreviation = QueryUtil.requiredString(map, "Service");

		region = QueryUtil.getString(map, "Region");

		availabilityZone = QueryUtil.getString(map, "AvailabilityZone");

		final String status = QueryUtil.requiredString(map, "Status");
		serviceHealthStatus = ServiceHealthStatus.valueOf(status);

		healthEventDescription = req.getParameter("HealthEventDescription");
		if( healthEventDescription == null || healthEventDescription.length() == 0 )
		{
			throw ErrorResponse.invlidData("HealthEventDescription is required");
		}
	}

    @Override
    public Object process0
    (
    	Session session,
    	HttpServletRequest req,
        HttpServletResponse resp,
        Map<String, String[]> map
    ) throws Exception
    {
		logger.debug("PutServiceHealthEvent Process0");

    	unmarshall(req, map);

    	ServiceBean serviceBean = (ServiceBean)session
    			.createCriteria(ServiceBean.class)
    			.add(Restrictions.eq("serviceNameAbbreviation",serviceAbbreviation))
    			.uniqueResult();

    	if( null == serviceBean)
    	{
			throw ErrorResponse.invlidData("Service is invalid");
    	}

/*
        ServiceHealthEventBean preExistingString = (ServiceHealthEventBean)session
        		.createCriteria(ServiceHealthEventBean.class)
        		.add(Restrictions.eq("serviceHealthEventDescription", healthEventDescription))
        		.uniqueResult() ;

    	if( preExistingString != null )
    	{
        	logger.debug("Found existing record:" + preExistingString.getId());

    		serviceHealthEventBean = preExistingString ;

    		return Constants.EMPTYSTRING;
    	}
*/

    	serviceHealthEventBean = new ServiceHealthEventBean
    	(
    		serviceBean,
    		region,
    		availabilityZone,
    		healthEventDescription,
    		serviceHealthStatus
    	) ;

        try
        {
        	// The goal was to keep duplicate strings from our list
        	// Hibernate does enforce unique constraints on Save(), but
        	// it seemed the session wasn't active and could not lookup
        	// the ID of the item that caused the constraint violation to
        	// prevent duplicate strings
        	session.save(serviceHealthEventBean);
        }
        catch( Exception ex )
        {
        	logger.debug("PutServiceHealthEvent exception on save");
        	logger.debug(ex.getMessage());
        }
        logger.info("-------------------------------------------------");

        return MonitorConstants.EMPTYSTRING;
    }

}
