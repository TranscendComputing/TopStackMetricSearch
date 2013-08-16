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
import com.transcend.monitor.message.PutMetricAlarmMessage.PutMetricAlarmRequest;
import com.transcend.monitor.message.PutMetricAlarmMessage.PutMetricAlarmResponse;
import com.transcend.monitor.transform.PutMetricAlarmRequestUnmarshaller;

public class PutMetricAlarm extends AbstractQueuedAction
    <PutMetricAlarmRequest, PutMetricAlarmResponse> {

    private static final String RootNodeName = "PutMetricAlarmResponse";

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public PutMetricAlarmRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final PutMetricAlarmRequest r = PutMetricAlarmRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());
        return r;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            PutMetricAlarmResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    private String marshall(final PutMetricAlarmResponse response) {
        final MarshallStruct<PutMetricAlarmResponse> ms = new MarshallStruct<PutMetricAlarmResponse>(response,
                response.getRequestId());
        return ms.addResponseMetadata(null, RootNodeName).toString();
    }

}
