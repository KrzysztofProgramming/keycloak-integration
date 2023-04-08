package me.krzysztofprogramming.userservice.users.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = UserEntity.TABLE_NAME, indexes = {
        @Index(name = "IX_" + UserEntity.TABLE_NAME + "_username", columnList = "username"),
        @Index(name = "IX_" + UserEntity.TABLE_NAME + "_email", columnList = "email")
}, uniqueConstraints = {
        @UniqueConstraint(name = "UK_" + UserEntity.TABLE_NAME + "_username", columnNames = "username"),
        @UniqueConstraint(name = "UK_" + UserEntity.TABLE_NAME + "_email", columnNames = "email")
})
public class UserEntity {

    public static final String TABLE_NAME = "user_table";
    @Id
    @EqualsAndHashCode.Include
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY, value = "password")
    private String hashedPassword;
    @Column(nullable = false)
    private String email;
    private String firstname;
    private String lastname;
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false, updatable = false)
    private Date createdDate;
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private Date lastModifiedDate;
    @Builder.Default
    private Boolean isEnabled = true;

//    @ManyToMany(cascade = CascadeType.PERSIST)
//    @JoinTable(
//            name="users_roles",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "role_name")
//    )
//    private Set<RoleEntity> userRoles;

}