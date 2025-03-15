package com.xvn.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationReq {

    @NotBlank(message = "Firstname is mandatory")
    @NotEmpty(message = "Firstname is mandatory")
    private String firstname;

    @NotBlank(message = "Lastname is mandatory")
    @NotEmpty(message = "Lastname is mandatory")
    private String lastname;

    @Email(message = "Email is not formatted")
    @NotBlank(message = "Email is mandatory")
    @NotEmpty(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @NotEmpty(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be between 8 characters long minimum")
    private String password;

}
