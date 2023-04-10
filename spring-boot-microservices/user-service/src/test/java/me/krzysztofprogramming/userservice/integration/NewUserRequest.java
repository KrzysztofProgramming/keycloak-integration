package me.krzysztofprogramming.userservice.integration;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
class NewUserRequest {
    private String username;
    private String password;
    private String email;
    private String firstname;
    private String lastname;
    private Boolean isEnabled;
}
