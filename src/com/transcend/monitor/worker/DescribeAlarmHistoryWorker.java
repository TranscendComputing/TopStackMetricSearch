/**
 *
 */
package com.transcend.monitor.worker;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.DateHelper;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.StringHelper;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.monitor.AlarmHistoryDetailBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.monitor.message.DescribeAlarmHistoryMessage.AlarmHistoryItem;
import com.transcend.monitor.message.DescribeAlarmHistoryMessage.AlarmHistoryItem.HistoryItemType;
import com.transcend.monitor.message.DescribeAlarmHistoryMessage.DescribeAlarmHistoryRequest;
import com.transcend.monitor.message.DescribeAlarmHistoryMessage.DescribeAlarmHistoryResponse;

@Component
public class DescribeAlarmHistoryWorker
    extends AbstractWorker<DescribeAlarmHistoryRequest, DescribeAlarmHistoryResponse> {

    private static final Logger logger = Appctx.getLogger(DescribeAlarmHistoryWorker.class
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
    public DescribeAlarmHistoryResponse doWork(
            DescribeAlarmHistoryRequest req) throws Exception {
        logger.debug("Performing work for DescribeAlarmHistory.");
        return super.doWork(req, getSession());
    }

    /* (non-Javadoc)
     * @see com.transcend.compute.worker.AbstractWorker#doWork0(com.google.protobuf.Message)
     */
    @Override
    @Transactional
    public DescribeAlarmHistoryResponse
        doWork0(DescribeAlarmHistoryRequest request,
                ServiceRequestContext context) throws Exception {
        final DescribeAlarmHistoryResponse.Builder result =
                DescribeAlarmHistoryResponse.newBuilder();
        AccountBean account = context.getAccountBean();

        final long acctId = account.getId();

        final String alarmName = request.getAlarmName();
        final BigInteger maxRecords = request.hasMaxRecords()?
                new BigInteger(""+request.getMaxRecords()) : null;
        final String histType = request.getHistoryItemType();
        Calendar startDate = Calendar.getInstance();
        if (request.hasStartDate()) {
            Date d = DateHelper.getCalendarFromISO8601String(request.getStartDate(),
                    TimeZone.getTimeZone("GMT")).getTime();
            startDate.setTime(d);
        } else {
            startDate = null;
        }
        Calendar endDate = Calendar.getInstance();
        if (request.hasEndDate()) {
            Date d = DateHelper.getCalendarFromISO8601String(request.getEndDate(),
                    TimeZone.getTimeZone("GMT")).getTime();
            endDate.setTime(d);
        } else {
            endDate = null;
        }

        final AlarmHistoryItem awsAlarmHistoryItem[] =
                describeAlarmHistory(alarmName, startDate, endDate, histType,
                        maxRecords, acctId);
        final Collection<AlarmHistoryItem> collAlarmHistoryItem = Arrays
                .asList(awsAlarmHistoryItem);
        result.addAllAlarmHistoryItem(collAlarmHistoryItem);
        // TODO: add a next token; currently this implementation returns all
        // data, but should limit that to 10 and store the remainder, or
        // at least store cursors or some such method. So, for now, no next
        // token
        // actually exists, therefore the assumption remains default value.
        // result.setNextToken(null);

        return result.buildPartial();
    }

    public AlarmHistoryItem[] describeAlarmHistory(final String alarmName,
            final Calendar startDate, final Calendar endDate,
            final String histItemType, final BigInteger maxRecords,
            final long acctId) {
        logger.info("---------------describeAlarmHistory-------------------");
        List<AlarmHistoryItem> histItems = new ArrayList<AlarmHistoryItem>();

        final Session session = HibernateUtil.getSession();
        final Criteria crit = session
                .createCriteria(AlarmHistoryDetailBean.class);

        if (!StringHelper.isBlank(alarmName)) {
            crit.add(Restrictions.eq("alarmName", alarmName));
        }
        if (startDate != null) {
            crit.add(Restrictions.gt("timestamp", startDate));
        }
        if (endDate != null) {
            crit.add(Restrictions.lt("timestamp", endDate));
        }
        if (!StringHelper.isBlank(histItemType)) {
            crit.add(Restrictions.eq("type", histItemType));
        }
        @SuppressWarnings("unchecked")
        List<AlarmHistoryDetailBean> histBeans = crit.list();
        for (final AlarmHistoryDetailBean detail : histBeans) {
            final AlarmHistoryItem.Builder item = AlarmHistoryItem.newBuilder();
            item.setAlarmName(detail.getAlarmName());
            if (detail.getData() != null) {
                item.setHistoryData(detail.getData());
            }
            item.setHistoryItemType(HistoryItemType.valueOf(detail.getType()));
            if (detail.getSummary() != null) {
                item.setHistorySummary(detail.getSummary());
            }
            String timestamp = DateHelper.getISO8601Date(detail.getTimestamp().getTime());
            item.setTimestamp(timestamp);
            histItems.add(item.build());
        }
        logger.info("------------------------------------------------------");
        if (maxRecords != null && histItems.size() > maxRecords.intValue()) {
            histItems = histItems.subList(0, maxRecords.intValue());
        }
        return histItems.toArray(new AlarmHistoryItem[histItems.size()]);
    }

}
