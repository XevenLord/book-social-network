package com.xvn.book.auth.controller;

import com.xvn.book.auth.service.AuthSrv;
import com.xvn.common.core.dto.auth.req.AuthReqDto;
import com.xvn.common.core.dto.auth.rsp.AuthRspDto;
import com.xvn.common.core.dto.auth.req.RegReqDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthCtr {

    private final AuthSrv service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(@RequestBody @Valid RegReqDto request) throws MessagingException {
        service.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthRspDto> authenticate(@RequestBody @Valid AuthReqDto req) {
        return ResponseEntity.ok(service.authenticate(req));
    }

    @GetMapping("/activate-account")
    public void confirm(@RequestParam String token) throws MessagingException {
        service.activateAccount(token);
    }

}
