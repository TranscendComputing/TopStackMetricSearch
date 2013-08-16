/**
 *
 */
package com.transcend.monitor.worker;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.monitor.AlarmBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.monitor.helper.AlarmModelHelper;
import com.transcend.monitor.message.DescribeAlarmsMessage.DescribeAlarmsRequest;
import com.transcend.monitor.message.DescribeAlarmsMessage.DescribeAlarmsResponse;
import com.transcend.monitor.message.MetricAlarmMessage.MetricAlarm;

public class DescribeAlarmsWorker extends AbstractWorker<DescribeAlarmsRequest, DescribeAlarmsResponse> {

    private static final Logger logger = Appctx.getLogger(DescribeAlarmsWorker.class
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
    public DescribeAlarmsResponse doWork(
            DescribeAlarmsRequest req) throws Exception {
        logger.debug("Performing work for DescribeAlarms.");
        return super.doWork(req, getSession());
    }

    /* (non-Javadoc)
     * @see com.transcend.compute.worker.AbstractWorker#doWork0(com.google.protobuf.Message)
     */
    @Override
    @Transactional
    public DescribeAlarmsResponse
        doWork0(DescribeAlarmsRequest request,
                ServiceRequestContext context) throws Exception {
        final DescribeAlarmsResponse.Builder result =
                DescribeAlarmsResponse.newBuilder();
        AccountBean account = context.getAccountBean();

        final long acctId = account.getId();

        final List<String> alrmNames = request.getAlarmNameList();
        final String actionPrefix = request.getActionPrefix();
        final String alarmNamePrefix = request.getAlarmNamePrefix();
        BigInteger maxRecords = null;
        if (request.hasMaxRecords()) {
            maxRecords = new BigInteger(""+request.getMaxRecords());
        }
        final String stateValue = request.hasStateValue()?
                request.getStateValue().toString() : "";

        final List<AlarmBean> alarms = AlarmModelHelper.describeAlarms(
                alrmNames, actionPrefix, alarmNamePrefix, maxRecords,
                stateValue, acctId);
        final List<MetricAlarm> collMetricAlarms = new ArrayList<MetricAlarm>();
        for (final AlarmBean alarm : alarms) {
            MetricAlarm ma = AlarmModelHelper.toMetricAlarm(alarm);
            collMetricAlarms.add(ma);
        }
        result.addAllMetricAlarm(collMetricAlarms);

        // TODO: add a next token; currently this implementation returns all
        // data, but should limit that to 10 and store the remainder, or
        // at least store cursors or some such method. So, for now, no next
        // token
        // actually exists, therefore the assumption remains default value.
        // result.setNextToken(null);

        return result.buildPartial();
    }
}
