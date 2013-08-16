/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.transcend.monitor.helper;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.util.DateUtils;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.QueryBuilder;
import com.msi.tough.core.StringHelper;
import com.msi.tough.model.monitor.AlarmBean;
import com.msi.tough.model.monitor.AlarmHistoryDetailBean;
import com.msi.tough.model.monitor.DimensionBean;
import com.transcend.monitor.message.MetricAlarmMessage.AlarmState;
import com.transcend.monitor.message.MetricAlarmMessage.ComparisonOperator;
import com.transcend.monitor.message.MetricAlarmMessage.Dimension;
import com.transcend.monitor.message.MetricAlarmMessage.MetricAlarm;
import com.transcend.monitor.message.MetricAlarmMessage.Statistic;

/**
 * @author jgardner
 *
 */
@Component
public class AlarmModelHelper {

    private static DateUtils dateUtils = new DateUtils();

    private static SessionFactory sessionFactory = null;

    @Transactional
    public static List<AlarmBean> describeAlarms(
            final List<String> alarmNames, final String actionPrefix,
            final String alarmNamePrefix, final BigInteger maxRecords,
            final String stateValue, final long acctId) {

        Session session = sessionFactory.getCurrentSession();

        QueryBuilder qb = new QueryBuilder("from AlarmBean");
        qb.equals("userId", acctId);

        if (alarmNames != null && alarmNames.size() > 0) {
            qb.in("alarmName", alarmNames);
        }
        if (!StringHelper.isBlank(alarmNamePrefix)) {
            qb.like("alarmName", alarmNamePrefix + "%");
        }
        if (!StringHelper.isBlank(stateValue)) {
            qb.equals("state", stateValue);
        }

        if (actionPrefix != null && !actionPrefix.isEmpty()) {
            // TODO: implement actionPrefix
            // This may be tricky with a criteria obj. Need to research
            // the best way to do this given a built up criteria obj.
            // possibly make 2 queries? go for list of distinct ids for
            // each action type and add an in criteria for id?
            // session.createQuery("");
        }
        // get our alarm bean objects by querying the database.

        @SuppressWarnings("unchecked")
        List<AlarmBean> alarms = qb.toQuery(session).list();
        if (maxRecords != null && alarms.size() > maxRecords.intValue()) {
            alarms = alarms.subList(0, maxRecords.intValue());
        }
        return alarms;
    }

    @Transactional
    public static AlarmHistoryDetailBean configurationUpdate(AlarmBean alarm,
            String before, String after) {
        Session session = sessionFactory.getCurrentSession();
        final AlarmHistoryDetailBean update = new AlarmHistoryDetailBean();
        update.setAlarmName(alarm.getAlarmName());
        update.setTimestamp(Calendar.getInstance());
        update.setType("ConfigurationUpdate");
        if (alarm.getId() == null) {
            update.setSummary("Alarm "+alarm.getAlarmName()+" created.");
        }
        session.save(update);
        alarm.save(session);
        return update;
    }


    public static MetricAlarm toMetricAlarm(final AlarmBean alarm) {
        final MetricAlarm.Builder b = MetricAlarm.newBuilder();
        b.setActionsEnabled(alarm.getEnabled());
        final CommaObject cal = new CommaObject(alarm.getActionNames());
        b.addAllAlarmAction(cal.toList());
        // b.setAlarmArn(alarmArn);
        String timestamp = dateUtils.formatIso8601Date(alarm.getLastUpdate());
        b.setAlarmConfigurationUpdatedTimestamp(timestamp);
        b.setAlarmDescription(alarm.getDescription());
        b.setAlarmName(alarm.getAlarmName());
        b.setComparisonOperator(ComparisonOperator.valueOf(alarm.getComparator()));
        for (final DimensionBean db : alarm.getDimensions()) {
            final Dimension.Builder dim = Dimension.newBuilder();
            dim.setName(db.getKey());
            dim.setValue(db.getValue());
            b.addDimension(dim);
        }
        b.setEvaluationPeriods(alarm.getEvaluationPeriods().intValue());
        final CommaObject cin = new CommaObject(
                alarm.getInsufficientDataActions());
        b.addAllInsufficientDataAction(cin.toList());
        b.setMetricName(alarm.getMetricName());
        b.setNamespace(alarm.getNamespace());
        final CommaObject cok = new CommaObject(alarm.getOkActions());
        b.addAllOKAction(cok.toList());
        b.setPeriod(alarm.getPeriod().intValue());
        b.setStateReason(alarm.getStateReason());
        b.setStateReasonData(alarm.getStateReasonData());
        // b.setStateUpdatedTimestamp(stateUpdatedTimestamp);
        b.setStateValue(AlarmState.valueOf(alarm.getState()));
        b.setStatistic(Statistic.valueOf(alarm.getStatistic()));
        b.setThreshold(alarm.getThreshold());
        b.setUnit(alarm.getUnit());
        return b.build();
    }

    @Autowired(required=true)
    public void setSessionFactory(SessionFactory sessionFactory) {
        AlarmModelHelper.sessionFactory = sessionFactory;
    }

}
