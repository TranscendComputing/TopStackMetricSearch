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
import com.transcend.monitor.message.DescribeAlarmsForMetricMessage.DescribeAlarmsForMetricRequest;
import com.transcend.monitor.message.DescribeAlarmsForMetricMessage.DescribeAlarmsForMetricResponse;
import com.transcend.monitor.transform.DescribeAlarmsForMetricRequestUnmarshaller;
import com.transcend.monitor.transform.DescribeAlarmsForMetricResultMarshaller;

public class DescribeAlarmsForMetric extends
    AbstractQueuedAction<DescribeAlarmsForMetricRequest, DescribeAlarmsForMetricResponse> {

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public DescribeAlarmsForMetricRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final DescribeAlarmsForMetricRequest r = DescribeAlarmsForMetricRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());
        return r;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            DescribeAlarmsForMetricResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    private String marshall(final DescribeAlarmsForMetricResponse response) {
        final MarshallStruct<DescribeAlarmsForMetricResponse> ms = new MarshallStruct<DescribeAlarmsForMetricResponse>(response,
                response.getRequestId());
        return new DescribeAlarmsForMetricResultMarshaller().marshall(ms);
    }

}
