package com.transcend.monitor.worker;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.monitor.AlarmBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.CWUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.monitor.message.SetAlarmStateMessage.SetAlarmStateRequest;
import com.transcend.monitor.message.SetAlarmStateMessage.SetAlarmStateResponse;

public class SetAlarmStateWorker extends
        AbstractWorker<SetAlarmStateRequest, SetAlarmStateResponse> {

    private static final Logger logger = Appctx
            .getLogger(SetAlarmStateWorker.class.getName());

    /**
     * We need a local copy of this doWork to provide the transactional
     * annotation. Transaction management is handled by the annotation, which
     * can only be on a concrete class.
     *
     * @param req
     * @return
     * @throws Exception
     */
    @Transactional
    public SetAlarmStateResponse doWork(SetAlarmStateRequest req)
            throws Exception {
        logger.debug("Performing work for SetAlarmState.");
        return super.doWork(req, getSession());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.transcend.compute.worker.AbstractWorker#doWork0(com.google.protobuf
     * .Message)
     */
    @Override
    @Transactional
    public SetAlarmStateResponse doWork0(SetAlarmStateRequest request,
            ServiceRequestContext context) throws Exception {
        final SetAlarmStateResponse.Builder result = SetAlarmStateResponse
                .newBuilder();
        AccountBean account = context.getAccountBean();

        Session session = getSession();

        final AlarmBean alarm = CWUtil.getAlarmBean(session, account.getId(),
                request.getAlarmName());
        if (alarm != null) {
            alarm.setState(request.getStateValue().toString());
            if (request.hasStateReason()) {
                alarm.setStateReason(request.getStateReason());
            }
            if (request.hasStateReasonData()) {
                alarm.setStateReasonData(request.getStateReasonData());
            }
            session.save(alarm);
        }

        return result.buildPartial();
    }

}
