/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.transcend.monitor.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.google.protobuf.Message;
import com.msi.tough.core.Appctx;
import com.msi.tough.monitor.common.MarshallingUtils;
import com.msi.tough.monitor.common.MonitorConstants;
import com.msi.tough.query.ProtobufUtil;
import com.transcend.monitor.message.MetricAlarmMessage.Dimension;

/**
 * @author jgardner
 *
 */
public abstract class BaseMonitorUnmarshaller<T extends Message> {

    private final static Logger logger = Appctx
            .getLogger(BaseMonitorUnmarshaller.class.getName());

    /**
     * General signature for umarshall.
     *
     * @param in
     * @return built object
     * @throws Exception
     */
    public abstract T unmarshall(Map<String, String[]> in) throws Exception;

    /**
     * Convenience method to unmarshall common fields.
     *
     * @param instance
     * @param in
     * @throws Exception
     */
    public T unmarshall(T instance, Map<String, String[]> in) {
        String namespace = MarshallingUtils.unmarshallString(in,
                MonitorConstants.NODE_NAMESPACE,
                logger);
        if ("AWS/EC2".equals(namespace)) {
            // Since we are AWS compatible, accept AWS namespace, sub our own.
            namespace = "MSI/EC2";
        }
        instance = ProtobufUtil.setRequiredField(instance, "namespace", namespace);
        return instance;
    }

    /**
     * @param mapIn
     * @return
     */
    Collection<Dimension> unmarshallDimensions(
        Map<String, String[]> in)
    {
        List<Dimension> dims = new ArrayList<Dimension>();
        int i = 0;
        while (true)
        {
            i++;
            String prefix = "Dimensions.member." + i + ".";
            final String n[] = in.get(prefix + MonitorConstants.NODE_NAME);
            if (n == null)
            {
                break;
            }
            final String v[] = in.get(prefix + MonitorConstants.NODE_VALUE);
            Dimension.Builder d = Dimension.newBuilder();
            d.setName(n[0]);
            d.setValue((v != null ? v[0] : ""));
            dims.add(d.build());
        }
        return dims;
    }
}
