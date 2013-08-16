/**
 *
 */
package com.transcend.monitor.actions;

import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.monitor.message.PutMetricDataMessage.PutMetricDataRequest;
import com.transcend.monitor.message.PutMetricDataMessage.PutMetricDataResponse;
import com.transcend.monitor.transform.PutMetricDataRequestUnmarshaller;

public class PutMetricData extends AbstractQueuedAction
    <PutMetricDataRequest, PutMetricDataResponse> {

	static final String RootNodeName = "ResponseNodeName";

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public PutMetricDataRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final PutMetricDataRequest r = PutMetricDataRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());
        return r;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            PutMetricDataResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    private String marshall(final PutMetricDataResponse response) {
        final MarshallStruct<PutMetricDataResponse> ms = new MarshallStruct<PutMetricDataResponse>(response,
                response.getRequestId());
        return ms.addResponseMetadata(null, RootNodeName).toString();
    }

}
