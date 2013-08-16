/**
 *
 */
package com.transcend.monitor.worker;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.monitor.AlarmBean;
import com.msi.tough.monitor.common.model.helper.AlarmModelHelper;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.monitor.message.DisableAlarmActionsMessage.DisableAlarmActionsRequest;
import com.transcend.monitor.message.DisableAlarmActionsMessage.DisableAlarmActionsResponse;

public class DisableAlarmActionsWorker extends
        AbstractWorker<DisableAlarmActionsRequest, DisableAlarmActionsResponse> {

    private static final Logger logger = Appctx
            .getLogger(DisableAlarmActionsWorker.class.getName());

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
    public DisableAlarmActionsResponse doWork(DisableAlarmActionsRequest req)
            throws Exception {
        logger.debug("Performing work for DisableAlarmActions.");
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
    public DisableAlarmActionsResponse doWork0(
            DisableAlarmActionsRequest request, ServiceRequestContext context)
            throws Exception {
        final DisableAlarmActionsResponse.Builder result = DisableAlarmActionsResponse
                .newBuilder();
        AccountBean account = context.getAccountBean();

        final long acctId = account.getId();

        final List<String> alrmNames = request.getAlarmNameList();

        final List<AlarmBean> alarms = AlarmModelHelper.describeAlarms(
                alrmNames, null, null, null, null, acctId);
        for (final AlarmBean alarm : alarms) {
            alarm.setEnabled(false);
            getSession().save(alarm);
        }

        return result.buildPartial();
    }
}
