package me.krzysztofprogramming.userprovider.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;
import java.util.Set;

@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomUserModel {
    @EqualsAndHashCode.Include
    private String id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private Date createdDate;
    private Date lastModifiedDate;
    private Boolean isEnabled;
    private Set<String> userRolesIds;
}
