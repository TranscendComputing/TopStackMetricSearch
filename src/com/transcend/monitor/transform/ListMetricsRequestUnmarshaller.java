/**
 *
 */
package com.transcend.monitor.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.monitor.common.MonitorConstants;
import com.transcend.monitor.message.ListMetricsMessage.ListMetricsRequest;

public class ListMetricsRequestUnmarshaller extends
    BaseMonitorUnmarshaller<ListMetricsRequest>
{
    private final static Logger logger = Appctx
        .getLogger(ListMetricsRequestUnmarshaller.class.getName());

    private static ListMetricsRequestUnmarshaller instance;

    public static ListMetricsRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new ListMetricsRequestUnmarshaller();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
     */
    @Override
    public ListMetricsRequest unmarshall(Map<String, String[]> mapIn)
    {
        final ListMetricsRequest.Builder request = ListMetricsRequest.newBuilder();
        request.addAllDimension(this.unmarshallDimensions(mapIn));
        request.setMetricName(MarshallingUtils.unmarshallString(mapIn,
            MonitorConstants.NODE_METRICNAME, MonitorConstants.EMPTYSTRING, logger));
        request.setNextToken(MarshallingUtils.unmarshallString(mapIn,
            MonitorConstants.NODE_NEXTTOKEN, MonitorConstants.EMPTYSTRING, logger));
        try {
            return unmarshall(request.buildPartial(), mapIn);
        } catch (Exception e) {
            // Namespace is optional.
            return request.buildPartial();
        }
    }
}
