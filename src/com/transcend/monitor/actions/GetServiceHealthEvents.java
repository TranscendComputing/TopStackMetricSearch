package com.transcend.monitor.actions;

import java.util.ArrayList;
//import java.util.Calendar;
import java.util.Date;
//import java.util.Iterator;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

//import com.msi.tough.core.DateHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
//import org.hibernate.criterion.*;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;

import com.generationjava.io.xml.XMLNode;

import com.msi.tough.model.ServiceBean;
import com.msi.tough.model.monitor.ServiceHealthEventBean;
import com.msi.tough.model.monitor.enums.ServiceHealthStatus;

import com.msi.tough.monitor.common.*;

import com.msi.tough.query.AbstractAction;
//import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;

public class GetServiceHealthEvents extends AbstractAction<Object>
{
    private static final Logger logger = Appctx.getLogger(GetServiceHealthEvents.class
        .getName());
    private static final String RootNodeName = "GetServiceHealthEvents";
    private static final String RootNodeNameResult = "GetServiceHealthEventsResult";


    private Date startDate ;
    private Date endDate ;
    private boolean returnLatestPerDay = false ;

    private List<ServiceHealthEventBean> events = null;

    // This action is not AWS Compatible, so the Marshall/UnMarshall code
    // is defined here, not in the AWS SDK

	public String marshall(
			MarshallStruct<Object> input,
			HttpServletResponse resp) throws Exception
	{
		logger.debug("Marshalling GetServiceHealthEvents...");

		XMLNode messageNode = new XMLNode(RootNodeName);

		XMLNode resultWrapNode = QueryUtil.addNode(messageNode, RootNodeNameResult);

		if( events != null )
		{
			for( ServiceHealthEventBean event : events)
			{
				MarshallingUtils.marshallServiceHealthEvent( resultWrapNode, event ) ;
			}
		}

		input.addResponseMetadata(messageNode, null);

		return messageNode.toString();

	}


	public void unmarshall(HttpServletRequest req, Map<String, String[]> map)
	{
		startDate = QueryUtil.requiredDate(map, "StartTime");
		endDate = QueryUtil.requiredDate(map,"EndTime");

		// Defaults to false
		returnLatestPerDay = QueryUtil.getBoolean(map, "LatestPerDay");
	}

/*
	private long fromString( String s, long defaultValue )
	{
		long converted;

		try
        {
			converted = Long.parseLong(s);
        }
        catch (NumberFormatException nfe)
        {
        	converted = defaultValue ;
        }

		return converted ;
	}
*/

    @Override
    public Object process0
    (
    	Session session,
    	HttpServletRequest req,
        HttpServletResponse resp,
        Map<String, String[]> map
    ) throws Exception
    {
		logger.debug("GetServiceHealthEvent Process0");

    	unmarshall(req, map);

    	// Possible form for a left join in a criteria query -- untested
    	// Criteria criteria = session
    	//		.createCriteria(ServiceBean.class).createAlias("", "", CriteriaSpecification.LEFT_JOIN);


    	// The goal is to list All services from the service table, and every
    	// service health event for the service on the date range given, with null where
    	// none exist (Defaulted to "healthy").
    	// This turns out to be an exotic left join where the join constraint is on both the service ID
    	// and the timeframe.  It is not possible to express the date portion
    	// of this query in a WHERE clause.
    	//
    	// Because of this, a direct SQL query is used, with Hibernate mapping the two
    	// entities.  Nothing in this query would inhibit portability to another RDS
    	// WARNING:  Hibernate will become confused hydrating the entities if the same
    	// column name, e.g. "ID" is in both tables.

    	String sql = "SELECT s.*, e.* FROM service s "
    			+ "LEFT JOIN service_health_event e ON "
    			+ "s.service_id = e.service_service_id AND "
    			+ "e.created_time BETWEEN :startTime AND :endTime "
    			+ "order by s.service_name ASC, e.created_time DESC";

    	Query query = session.createSQLQuery( sql )
    			.addEntity(ServiceBean.class)
    			.addEntity(ServiceHealthEventBean.class)
    			.setDate("startTime", startDate)
    			.setDate("endTime", endDate);

		// This will log the text of the query but not the substituted parameters
    	// logger.debug(query.getQueryString());

    	@SuppressWarnings("rawtypes")
		List results = query.list();

    	events = new ArrayList<ServiceHealthEventBean>() ;

    	int lastService = 0 ;
    	int lastDayOfYear = -1;
    	Calendar cal = Calendar.getInstance();
    	for( Object o : results)
    	{
    		Object[] os = (Object[])o ;

    		ServiceBean sb = (ServiceBean)os[0] ;
    		if( sb.getId() != lastService)
    		{
    			lastService = sb.getId();
    			lastDayOfYear = -1;
    		}

    		ServiceHealthEventBean event ;

    		if( os[1] != null)
    		{
    			event = (ServiceHealthEventBean)os[1];

    			if( returnLatestPerDay )
    			{
	    			// Find the day of year of the event
	    			cal.setTime(event.getCreatedTime());
	    			int dayOfYear = cal.get(Calendar.DAY_OF_YEAR) ;

	    			// If this is a new day of year (or new service) add the event
	    			if( dayOfYear != lastDayOfYear )
	    			{
	    	    		lastDayOfYear = dayOfYear ;
	    	    		events.add(event);
	    			}
    			}
    			else
    			{
    	    		events.add(event);
    			}
    		}
    		else
    		{
    			event = new ServiceHealthEventBean(sb,"","","",ServiceHealthStatus.Healthy);
        		events.add(event);
    		}
    	}

/*
    	// Example of NOT IN Criteria Query
		List<ServiceBean> missingServices = session.createCriteria(ServiceBean.class)
			.add(Restrictions.not(Restrictions.in("id", present)))
    		.list();
*/

/*    	Warning:  Using .iterate() on a Query in this manner caused
 * 			Hibernate to issue separate queries for each row
    	for( Iterator it = q.iterate(); it.hasNext();)
    	{
    		Object[] row = (Object[])it.next();

    		if( row[0] != null)
    		{
    			ServiceBean sb = (ServiceBean)row[0];
    		}

    		if( row[1] != null)
    		{
    			ServiceHealthEventBean serviceHealthBean = (ServiceHealthEventBean)row[1];
    		}
    	}
*/

        logger.info("-------------------------------------------------");

        return MonitorConstants.EMPTYSTRING;
    }

}
