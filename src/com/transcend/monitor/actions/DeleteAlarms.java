/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2013
 * All Rights Reserved.
 */
package com.transcend.monitor.actions;

import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.monitor.message.DeleteAlarmsMessage.DeleteAlarmsRequest;
import com.transcend.monitor.message.DeleteAlarmsMessage.DeleteAlarmsResponse;
import com.transcend.monitor.transform.DeleteAlarmsRequestUnmarshaller;

public class DeleteAlarms extends
        AbstractQueuedAction<DeleteAlarmsRequest, DeleteAlarmsResponse> {

    private static final String RootNodeName = "DeleteAlarmsResponse";

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public DeleteAlarmsRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final DeleteAlarmsRequest r = DeleteAlarmsRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());
        return r;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            DeleteAlarmsResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    private String marshall(final DeleteAlarmsResponse response) {
        final MarshallStruct<DeleteAlarmsResponse> ms = new MarshallStruct<DeleteAlarmsResponse>(response,
                response.getRequestId());
        return ms.addResponseMetadata(null, RootNodeName).toString();
    }
}
