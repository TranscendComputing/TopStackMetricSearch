/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2013
 * All Rights Reserved.
 */
package com.transcend.monitor.transform;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.monitor.common.MonitorConstants;
import com.msi.tough.query.MarshallStruct;
import com.transcend.monitor.message.DescribeAlarmsForMetricMessage.DescribeAlarmsForMetricResponse;

public class DescribeAlarmsForMetricResultMarshaller {

    public String marshall(MarshallStruct<DescribeAlarmsForMetricResponse> in) {

        XMLNode nodeRoot = new XMLNode(
                MonitorConstants.NODE_DESCRIBEALARMSFORMETRICRESPONSE);
        nodeRoot.addAttr(MonitorConstants.ATTRIBUTE_XMLNS,
                MonitorConstants.NAMESPACE);

        final XMLNode nodeRes = new XMLNode(
                MonitorConstants.NODE_DESCRIBEALARMSFORMETRICRESULT);
        nodeRoot.addNode(nodeRes);

        MarshallingUtils.marshallMetricAlarmList(nodeRes,
                MonitorConstants.NODE_METRICALARMS, in.getMainObject()
                        .getMetricAlarmList());

        return nodeRoot.toString();
    }

}
