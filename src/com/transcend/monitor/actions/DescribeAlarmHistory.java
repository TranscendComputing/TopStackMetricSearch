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
import com.transcend.monitor.message.DescribeAlarmHistoryMessage.DescribeAlarmHistoryRequest;
import com.transcend.monitor.message.DescribeAlarmHistoryMessage.DescribeAlarmHistoryResponse;
import com.transcend.monitor.transform.DescribeAlarmHistoryRequestUnmarshaller;
import com.transcend.monitor.transform.DescribeAlarmHistoryResultMarshaller;

public class DescribeAlarmHistory extends
AbstractQueuedAction<DescribeAlarmHistoryRequest, DescribeAlarmHistoryResponse> {

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public DescribeAlarmHistoryRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final DescribeAlarmHistoryRequest r = DescribeAlarmHistoryRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());
        return r;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            DescribeAlarmHistoryResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    private String marshall(final DescribeAlarmHistoryResponse response) {
        final MarshallStruct<DescribeAlarmHistoryResponse> ms = new MarshallStruct<DescribeAlarmHistoryResponse>(response,
                response.getRequestId());
        return new DescribeAlarmHistoryResultMarshaller().marshall(ms);
    }

}
