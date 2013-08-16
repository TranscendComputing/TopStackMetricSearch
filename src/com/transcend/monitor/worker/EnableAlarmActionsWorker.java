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
import com.transcend.monitor.message.EnableAlarmActionsMessage.EnableAlarmActionsRequest;
import com.transcend.monitor.message.EnableAlarmActionsMessage.EnableAlarmActionsResponse;

public class EnableAlarmActionsWorker extends
        AbstractWorker<EnableAlarmActionsRequest, EnableAlarmActionsResponse> {

    private static final Logger logger = Appctx
            .getLogger(EnableAlarmActionsWorker.class.getName());

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
    public EnableAlarmActionsResponse doWork(EnableAlarmActionsRequest req)
            throws Exception {
        logger.debug("Performing work for EnableAlarmActions.");
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
    public EnableAlarmActionsResponse doWork0(
            EnableAlarmActionsRequest request, ServiceRequestContext context)
            throws Exception {
        final EnableAlarmActionsResponse.Builder result = EnableAlarmActionsResponse
                .newBuilder();
        AccountBean account = context.getAccountBean();

        final long acctId = account.getId();

        final List<String> alrmNames = request.getAlarmNameList();

        final List<AlarmBean> alarms = AlarmModelHelper.describeAlarms(
                alrmNames, null, null, null, null, acctId);
        for (final AlarmBean alarm : alarms) {
            alarm.setEnabled(true);
            getSession().save(alarm);
        }

        return result.buildPartial();
    }
}
