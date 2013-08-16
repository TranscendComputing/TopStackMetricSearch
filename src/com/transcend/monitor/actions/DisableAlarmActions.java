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
import com.transcend.monitor.message.DisableAlarmActionsMessage.DisableAlarmActionsRequest;
import com.transcend.monitor.message.DisableAlarmActionsMessage.DisableAlarmActionsResponse;
import com.transcend.monitor.transform.DisableAlarmActionsRequestUnmarshaller;

public class DisableAlarmActions extends AbstractQueuedAction
    <DisableAlarmActionsRequest, DisableAlarmActionsResponse> {

	    private static final String RootNodeName = "DisableAlarmActions";

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public DisableAlarmActionsRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final DisableAlarmActionsRequest r = DisableAlarmActionsRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());
        return r;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            DisableAlarmActionsResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    private String marshall(final DisableAlarmActionsResponse response) {
        final MarshallStruct<DisableAlarmActionsResponse> ms = new MarshallStruct<DisableAlarmActionsResponse>(response,
                response.getRequestId());
        return ms.addResponseMetadata(null, RootNodeName).toString();
    }

}

