package com.xvn.book.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationRsp {

    private String token;

}
