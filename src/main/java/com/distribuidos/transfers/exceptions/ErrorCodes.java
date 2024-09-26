package com.distribuidos.transfers.exceptions;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorCodes {

    private static final String PREFIX = "TR-00";

    public static final String DOCUMENTS_UPSTREAM_ERROR = PREFIX + "01";
    public static final String AUTHENTICATION_UPSTREAM_ERROR = PREFIX + "02";


}
