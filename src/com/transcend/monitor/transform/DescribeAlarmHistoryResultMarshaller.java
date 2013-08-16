/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.transcend.monitor.transform;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.monitor.common.MonitorConstants;
import com.msi.tough.query.MarshallStruct;
import com.transcend.monitor.message.DescribeAlarmHistoryMessage.DescribeAlarmHistoryResponse;

public class DescribeAlarmHistoryResultMarshaller {

    public String marshall(MarshallStruct<DescribeAlarmHistoryResponse> in) {

        XMLNode nodeRoot = new XMLNode(
                MonitorConstants.NODE_DESCRIBEALARMHISTORYRESPONSE);
        nodeRoot.addAttr(MonitorConstants.ATTRIBUTE_XMLNS,
                MonitorConstants.NAMESPACE);

        final XMLNode nodeRes = new XMLNode(
                MonitorConstants.NODE_DESCRIBEALARMHISTORYRESULT);
        nodeRoot.addNode(nodeRes);

        MarshallingUtils.marshallAlarmHistoryItemList(nodeRes,
                MonitorConstants.NODE_ALARMHISTORYITEMS, in.getMainObject()
                        .getAlarmHistoryItemList());
        MarshallingUtils.marshallString(nodeRoot,
                MonitorConstants.NODE_NEXTTOKEN, in.getMainObject()
                        .getNextToken());
        in.addResponseMetadata(nodeRoot, null);

        return nodeRoot.toString();
    }
}
