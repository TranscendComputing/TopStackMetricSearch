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
import com.transcend.monitor.message.DescribeAlarmsMessage.DescribeAlarmsResponse;

public class DescribeAlarmsResultMarshaller {

    public String marshall(MarshallStruct<DescribeAlarmsResponse> in) {
        XMLNode nodeRoot = new XMLNode(
                MonitorConstants.NODE_DESCRIBEALARMSRESPONSE);
        nodeRoot.addAttr(MonitorConstants.ATTRIBUTE_XMLNS,
                MonitorConstants.NAMESPACE);

        final XMLNode nodeRes = new XMLNode(
                MonitorConstants.NODE_DESCRIBEALARMSRESULT);
        nodeRoot.addNode(nodeRes);

        MarshallingUtils.marshallMetricAlarmList(nodeRes,
                MonitorConstants.NODE_METRICALARMS, in.getMainObject()
                        .getMetricAlarmList());
        MarshallingUtils.marshallString(nodeRes,
                MonitorConstants.NODE_NEXTTOKEN, in.getMainObject()
                        .getNextToken());
        in.addResponseMetadata(nodeRoot, null);

        return nodeRoot.toString();
    }
}
