package com.example.web.dto;

import com.example.spingjwtauthexample.model.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    private String username;

    private String email;

    private Set<RoleType> roles;

    private String password;
}
