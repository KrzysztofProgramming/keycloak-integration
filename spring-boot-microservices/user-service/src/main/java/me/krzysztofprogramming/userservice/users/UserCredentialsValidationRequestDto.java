package me.krzysztofprogramming.userservice.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
class UserCredentialsValidationRequestDto {
    @NotNull
    private String username;

    @NotNull
    private String password;
}
