package me.krzysztofprogramming.userprovider.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class UserCredentialsDto {
    private String username;
    private String password;
}
