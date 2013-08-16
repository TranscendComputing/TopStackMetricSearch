/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2013
 * All Rights Reserved.
 */
package com.transcend.monitor.worker;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.monitor.AlarmBean;
import com.msi.tough.model.monitor.DimensionBean;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.CWUtil;
import com.msi.tough.utils.Constants;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.monitor.helper.AlarmModelHelper;
import com.transcend.monitor.message.MetricAlarmMessage.Dimension;
import com.transcend.monitor.message.MetricAlarmMessage.MetricAlarmToPut;
import com.transcend.monitor.message.PutMetricAlarmMessage.PutMetricAlarmRequest;
import com.transcend.monitor.message.PutMetricAlarmMessage.PutMetricAlarmResponse;

public class PutMetricAlarmWorker extends
    AbstractWorker<PutMetricAlarmRequest, PutMetricAlarmResponse> {

    private static final Logger logger = Appctx.getLogger(PutMetricAlarmWorker.class
            .getName());

    /**
     * We need a local copy of this doWork to provide the transactional
     * annotation.  Transaction management is handled by the annotation, which
     * can only be on a concrete class.
     * @param req
     * @return
     * @throws Exception
     */
    @Transactional
    public PutMetricAlarmResponse doWork(
            PutMetricAlarmRequest req) throws Exception {
        logger.debug("Performing work for PutMetricAlarm.");
        return super.doWork(req, getSession());
    }

    /* (non-Javadoc)
     * @see com.transcend.compute.worker.AbstractWorker#doWork0(com.google.protobuf.Message)
     */
    @Override
    @Transactional
    public PutMetricAlarmResponse
        doWork0(PutMetricAlarmRequest request,
                ServiceRequestContext context) throws Exception {
        final PutMetricAlarmResponse.Builder result =
                PutMetricAlarmResponse.newBuilder();
        AccountBean account = context.getAccountBean();

        Session session = getSession();
        MetricAlarmToPut toPut = request.getMetricAlarm();
        final List<AlarmBean> existingAlarms = AlarmModelHelper.describeAlarms(
                Arrays.asList(new String[] { toPut.getAlarmName() }),
                null, null, new BigInteger("1"),
                null, account.getId());
        if (existingAlarms.size() > 0) {
            throw ErrorResponse.invalidParameterValue("Duplicate alarm name.");
        }
        final AlarmBean alarm = new AlarmBean();
        final CommaObject cnames = new CommaObject(toPut.getAlarmActionList());
        alarm.setActionNames(cnames.toString());
        alarm.setAlarmName(toPut.getAlarmName());
        alarm.setComparator(toPut.getComparisonOperator().toString());
        alarm.setDescription(toPut.getAlarmDescription());
        alarm.setEnabled(toPut.getActionsEnabled());
        alarm.setEvaluationPeriods(BigInteger.valueOf(toPut.getEvaluationPeriods()));
        final CommaObject cin = new CommaObject(toPut.getInsufficientDataActionList());
        alarm.setInsufficientDataActions(cin.toString());
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        alarm.setLastUpdate(cal.getTime());
        alarm.setMetricName(toPut.getMetricName());
        alarm.setNamespace(toPut.getNamespace());
        if ("AWS/EC2".equals(alarm.getNamespace())) {
            // Since we are AWS compatible, accept AWS namespace, sub our own.
            alarm.setNamespace("MSI/EC2");
        }
        final CommaObject cok = new CommaObject(toPut.getOKActionList());
        alarm.setOkActions(cok.toString());
        alarm.setPeriod(BigInteger.valueOf(toPut.getPeriod()));
        alarm.setState(Constants.STATE_INSUFFICIENT_DATA);
        alarm.setStateReason("Initial Creation.");
        alarm.setStateReasonData("{}");
        alarm.setStatistic(toPut.getStatistic().toString());
        alarm.setThreshold(toPut.getThreshold());
        alarm.setUnit(toPut.getUnit());
        alarm.setUserId(account.getId());

        final Set<DimensionBean> dbList = new HashSet<DimensionBean>();
        if (toPut.getDimensionList() != null) {
            for (final Dimension d : toPut.getDimensionList()) {
                final DimensionBean save = CWUtil.getDimensionBean(session,
                        account.getId(), d.getName(), d.getValue(), true);
                dbList.add(save);
            }
            alarm.setDimensions(dbList);
        }
        AlarmModelHelper.configurationUpdate(alarm, "old", "new");

        return result.buildPartial();
    }
}
