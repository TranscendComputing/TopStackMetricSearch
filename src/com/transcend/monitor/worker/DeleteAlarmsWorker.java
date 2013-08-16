/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2013
 * All Rights Reserved.
 */
package com.transcend.monitor.worker;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.model.AccountBean;
import com.msi.tough.monitor.common.manager.db.AlarmStoreClient;
import com.msi.tough.monitor.common.manager.db.AlarmStoreClient.AlarmNotFoundException;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.monitor.message.DeleteAlarmsMessage.DeleteAlarmsRequest;
import com.transcend.monitor.message.DeleteAlarmsMessage.DeleteAlarmsResponse;

public class DeleteAlarmsWorker extends
        AbstractWorker<DeleteAlarmsRequest, DeleteAlarmsResponse> {

    private static final Logger logger = Appctx.getLogger(DeleteAlarmsWorker.class
            .getName());

    private AlarmStoreClient alarmStoreClient = null;

    /**
     * We need a local copy of this doWork to provide the transactional
     * annotation.  Transaction management is handled by the annotation, which
     * can only be on a concrete class.
     * @param req
     * @return
     * @throws Exception
     */
    @Transactional
    public DeleteAlarmsResponse doWork(
            DeleteAlarmsRequest req) throws Exception {
        logger.debug("Performing work for DeleteAlarms.");
        return super.doWork(req, getSession());
    }

    /* (non-Javadoc)
     * @see com.transcend.compute.worker.AbstractWorker#doWork0(com.google.protobuf.Message)
     */
    @Override
    @Transactional
    public DeleteAlarmsResponse
        doWork0(DeleteAlarmsRequest request,
                ServiceRequestContext context) throws Exception {
        final DeleteAlarmsResponse.Builder result =
                DeleteAlarmsResponse.newBuilder();
        AccountBean account = context.getAccountBean();

        final long acctId = account.getId();
        final List<String> nameList = request.getAlarmNameList();

        try {
            getAlarmStoreClient().deleteAlarms(nameList, acctId);
        } catch (AlarmNotFoundException anfe) {
            throw ErrorResponse.notFound();
        }
        return result.buildPartial();
    }

    public void setAlarmStoreClient(AlarmStoreClient alarmStoreClient) {
        this.alarmStoreClient = alarmStoreClient;
    }

    public AlarmStoreClient getAlarmStoreClient() {
        if (alarmStoreClient == null) {
            alarmStoreClient = Appctx.getBean("MON_ALARMSTORE");
        }
        return alarmStoreClient;
    }

}
