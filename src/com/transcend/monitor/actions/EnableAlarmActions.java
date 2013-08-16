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
import com.transcend.monitor.message.EnableAlarmActionsMessage.EnableAlarmActionsRequest;
import com.transcend.monitor.message.EnableAlarmActionsMessage.EnableAlarmActionsResponse;
import com.transcend.monitor.transform.EnableAlarmActionsRequestUnmarshaller;

public class EnableAlarmActions extends AbstractQueuedAction
    <EnableAlarmActionsRequest, EnableAlarmActionsResponse> {

	    private static final String RootNodeName = "EnableAlarmActions";

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public EnableAlarmActionsRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final EnableAlarmActionsRequest r = EnableAlarmActionsRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());
        return r;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            EnableAlarmActionsResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    private String marshall(final EnableAlarmActionsResponse response) {
        final MarshallStruct<EnableAlarmActionsResponse> ms = new MarshallStruct<EnableAlarmActionsResponse>(response,
                response.getRequestId());
        return ms.addResponseMetadata(null, RootNodeName).toString();
    }

}

