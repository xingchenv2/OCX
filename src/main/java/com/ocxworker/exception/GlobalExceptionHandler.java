package com.ocxworker.exception;

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

    @ExceptionHandler({OciException.class})
    public ResponseData<?> handleOciException(OciException e) {
        log.error("Business error: {}", e.getMessage());
        return ResponseData.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseData<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(f -> f.getField() + ": " + f.getDefaultMessage())
            .findFirst()
            .orElse("Validation failed");
        return ResponseData.error(message);
    }

    @ExceptionHandler({NoResourceFoundException.class})
    public ResponseData<?> handleNoResourceFound(NoResourceFoundException e) {
        return ResponseData.error(404, "资源不存在");
    }

    @ExceptionHandler({ClientAbortException.class})
    public void handleClientAbortException(ClientAbortException e) {
        log.debug("Client aborted request: {}", e.getMessage());
    }

    @ExceptionHandler({BmcException.class})
    public ResponseData<?> handleBmcException(BmcException e) {
        String opc = e.getOpcRequestId();
        log.error(
            "OCI API error: status={} opcRequestId={} serviceCode={} message={}",
            new Object[]{e.getStatusCode(), opc != null ? opc : "-", e.getServiceCode(), e.getMessage()}
        );
        StringBuilder sb = new StringBuilder("OCI 错误 [").append(e.getStatusCode()).append("]");
        if (StringUtils.hasText(e.getMessage())) {
            sb.append(": ").append(e.getMessage());
        }

        if (StringUtils.hasText(opc)) {
            sb.append(" (opc-request-id: ").append(opc).append(")");
        }

        return ResponseData.error(sb.toString());
    }

    @ExceptionHandler({Exception.class})
    public ResponseData<?> handleException(Exception e) {
        String type = e.getClass().getName();
        String detail = e.getMessage() != null ? e.getMessage() : "(无消息)";
        log.error("Unexpected error: {} | {}", new Object[]{type, detail, e});
        return ResponseData.error("服务器内部错误，请查看日志");
    }
}
