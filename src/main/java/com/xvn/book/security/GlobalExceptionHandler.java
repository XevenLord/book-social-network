package com.xvn.book.security;

import com.xvn.common.core.enums.SysCodeEnum;
import com.xvn.common.core.exception.ExptRsp;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

@RestControllerAdvice
@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @PostConstruct
    public void init() {
        log.info("GlobalExceptionHandler loaded");
    }


    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExptRsp> handleException(LockedException expt) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ExptRsp.builder()
                        .errCode(SysCodeEnum.AUTH_ACC_LOCKED.getCode())
                        .errMsg(SysCodeEnum.AUTH_ACC_LOCKED.getMsg())
                        .err(expt.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExptRsp> handleException(DisabledException expt) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ExptRsp.builder()
                        .errCode(SysCodeEnum.AUTH_ACC_DISABLED.getCode())
                        .errMsg(SysCodeEnum.AUTH_ACC_DISABLED.getMsg())
                        .err(expt.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExptRsp> handleException(BadCredentialsException expt) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ExptRsp.builder()
                        .errCode(SysCodeEnum.AUTH_BAD_CREDS.getCode())
                        .errMsg(SysCodeEnum.AUTH_BAD_CREDS.getMsg())
                        .err(expt.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExptRsp> handleException(MessagingException expt) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExptRsp.builder()
                        .err(expt.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExptRsp> handleException(MethodArgumentNotValidException expt) {
        log.info("Checking enter this exception handling");
        Set<String> errs = new HashSet<>();
        expt.getBindingResult().getAllErrors()
                .forEach(err -> {
                    var errMsg = err.getDefaultMessage();
                    errs.add(errMsg);
                });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExptRsp.builder()
                        .vldErrs(errs)
                        .err(expt.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExptRsp> handleException(Exception expt) {
        // log the exception
        expt.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExptRsp.builder()
                        .errMsg("Internal error, contact the admin")
                        .err(expt.getMessage())
                        .build()
                );
    }

}
