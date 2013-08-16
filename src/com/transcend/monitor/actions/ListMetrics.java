/**
 *
 */
package com.transcend.monitor.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.monitor.common.manager.db.RDBMSDataStore;
import com.msi.tough.monitor.common.model.exception.MSIMonitorException;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.msi.tough.utils.InstanceUtil;
import com.transcend.monitor.message.ListMetricsMessage.ListMetricsRequest;
import com.transcend.monitor.message.ListMetricsMessage.ListMetricsResponse;
import com.transcend.monitor.transform.ListMetricsRequestUnmarshaller;
import com.transcend.monitor.transform.ListMetricsResultMarshaller;

public class ListMetrics
        extends
        AbstractQueuedAction<ListMetricsRequest, ListMetricsResponse> {

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public ListMetricsRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final ListMetricsRequest r = ListMetricsRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());
        return r;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.
     * query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            ListMetricsResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    private String marshall(final ListMetricsResponse response) {
        final MarshallStruct<ListMetricsResponse> ms = new MarshallStruct<ListMetricsResponse>(
                response, response.getRequestId());
        return new ListMetricsResultMarshaller().marshall(ms);
    }

}
