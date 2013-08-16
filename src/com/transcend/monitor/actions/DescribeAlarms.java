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
import com.transcend.monitor.message.DescribeAlarmsMessage.DescribeAlarmsRequest;
import com.transcend.monitor.message.DescribeAlarmsMessage.DescribeAlarmsResponse;
import com.transcend.monitor.transform.DescribeAlarmsRequestUnmarshaller;
import com.transcend.monitor.transform.DescribeAlarmsResultMarshaller;

public class DescribeAlarms extends
AbstractQueuedAction<DescribeAlarmsRequest, DescribeAlarmsResponse> {

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public DescribeAlarmsRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final DescribeAlarmsRequest r = DescribeAlarmsRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());
        return r;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            DescribeAlarmsResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    private String marshall(final DescribeAlarmsResponse response) {
        final MarshallStruct<DescribeAlarmsResponse> ms = new MarshallStruct<DescribeAlarmsResponse>(response,
                response.getRequestId());
        return new DescribeAlarmsResultMarshaller().marshall(ms);
    }

}
