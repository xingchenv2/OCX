/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.exception.GlobalExceptionHandler
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.model.vo.ResponseData
 *  com.oracle.bmc.model.BmcException
 *  lombok.Generated
 *  org.apache.catalina.connector.ClientAbortException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.StringUtils
 *  org.springframework.web.bind.MethodArgumentNotValidException
 *  org.springframework.web.bind.annotation.ExceptionHandler
 *  org.springframework.web.bind.annotation.RestControllerAdvice
 *  org.springframework.web.servlet.resource.NoResourceFoundException
 */
package com.ocxworker.exception;

import com.ocxworker.exception.OciException;
import com.ocxworker.model.vo.ResponseData;
import com.oracle.bmc.model.BmcException;
import lombok.Generated;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value={OciException.class})
    public ResponseData<?> handleOciException(OciException e) {
        log.error("Business error: {}", (Object)e.getMessage());
        return ResponseData.error((int)e.getCode(), (String)e.getMessage());
    }

    @ExceptionHandler(value={MethodArgumentNotValidException.class})
    public ResponseData<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream().map(f -> f.getField() + ": " + f.getDefaultMessage()).findFirst().orElse("Validation failed");
        return ResponseData.error((String)message);
    }

    @ExceptionHandler(value={NoResourceFoundException.class})
    public ResponseData<?> handleNoResourceFound(NoResourceFoundException e) {
        return ResponseData.error((int)404, (String)"\u8d44\u6e90\u4e0d\u5b58\u5728");
    }

    @ExceptionHandler(value={ClientAbortException.class})
    public void handleClientAbortException(ClientAbortException e) {
        log.debug("Client aborted request: {}", (Object)e.getMessage());
    }

    @ExceptionHandler(value={BmcException.class})
    public ResponseData<?> handleBmcException(BmcException e) {
        String opc = e.getOpcRequestId();
        log.error("OCI API error: status={} opcRequestId={} serviceCode={} message={}", new Object[]{e.getStatusCode(), opc != null ? opc : "-", e.getServiceCode(), e.getMessage()});
        StringBuilder sb = new StringBuilder("OCI \u9519\u8bef [").append(e.getStatusCode()).append("]");
        if (StringUtils.hasText((String)e.getMessage())) {
            sb.append(": ").append(e.getMessage());
        }
        if (StringUtils.hasText((String)opc)) {
            sb.append(" (opc-request-id: ").append(opc).append(")");
        }
        return ResponseData.error((String)sb.toString());
    }

    @ExceptionHandler(value={Exception.class})
    public ResponseData<?> handleException(Exception e) {
        String type = e.getClass().getName();
        String detail = e.getMessage() != null ? e.getMessage() : "(\u65e0\u6d88\u606f)";
        log.error("Unexpected error: {} | {}", new Object[]{type, detail, e});
        return ResponseData.error((String)"\u670d\u52a1\u5668\u5185\u90e8\u9519\u8bef\uff0c\u8bf7\u67e5\u770b\u65e5\u5fd7");
    }
}

