package com.msi.tough.query.monitor;

import com.msi.tough.query.ErrorResponse;

public class MonitorQueryFaults extends ErrorResponse {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param type
     * @param message
     * @param code
     */
    public MonitorQueryFaults(String type, String message, String code) {
        super(type, message, code);
    }

    public static ErrorResponse invalidParameterCombination() {
        return new ErrorResponse(ErrorResponse.TYPE_SENDER, "InvalidParameterCombination",
                "Parameters that must not be used together were used together.");
    }
}
