package com.transcend.monitor.actions;

import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.monitor.message.GetMetricStatisticsMessage.GetMetricStatisticsRequest;
import com.transcend.monitor.message.GetMetricStatisticsMessage.GetMetricStatisticsResponse;
import com.transcend.monitor.transform.GetMetricStatisticsRequestUnmarshaller;
import com.transcend.monitor.transform.GetMetricStatisticsResultMarshaller;

public class GetMetricStatistics extends
    AbstractQueuedAction<GetMetricStatisticsRequest, GetMetricStatisticsResponse> {

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public GetMetricStatisticsRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final GetMetricStatisticsRequest r = GetMetricStatisticsRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());
        return r;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            GetMetricStatisticsResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    private String marshall(final GetMetricStatisticsResponse response) {
        final MarshallStruct<GetMetricStatisticsResponse> ms = new MarshallStruct<GetMetricStatisticsResponse>(response,
                response.getRequestId());
        return new GetMetricStatisticsResultMarshaller().marshall(ms);
    }
}
