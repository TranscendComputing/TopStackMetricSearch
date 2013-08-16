package com.transcend.monitor.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.msi.tough.model.monitor.AlarmBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.msi.tough.utils.CWUtil;
import com.transcend.monitor.message.SetAlarmStateMessage.SetAlarmStateRequest;
import com.transcend.monitor.message.SetAlarmStateMessage.SetAlarmStateResponse;
import com.transcend.monitor.transform.SetAlarmStateRequestUnmarshaller;

public class SetAlarmState extends
        AbstractQueuedAction<SetAlarmStateRequest, SetAlarmStateResponse> {

    static final String RootNodeName = "SetAlarmState";

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public SetAlarmStateRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final SetAlarmStateRequest r = SetAlarmStateRequestUnmarshaller
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
            SetAlarmStateResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    private String marshall(final SetAlarmStateResponse response) {
        final MarshallStruct<SetAlarmStateResponse> ms = new MarshallStruct<SetAlarmStateResponse>(
                response, response.getRequestId());
        return ms.addResponseMetadata(null, RootNodeName).toString();
    }

}
