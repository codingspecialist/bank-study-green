package shop.mtcoding.bank.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.mtcoding.bank.config.enums.UserEnum;
import shop.mtcoding.bank.domain.AudingTime;

@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Table(name = "users")
@Entity
public class User extends AudingTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String username;
    @Column(nullable = false, length = 60)
    private String password;
    @Column(nullable = false, length = 50)
    private String email;
    @Column(nullable = false, length = 20)
    private String fullName;

    @Enumerated(EnumType.STRING) // enum 쓸때 어노테이션
    @Column(nullable = false)
    private UserEnum role; // ADMIN, CUSTOMER

    @Builder
    public User(Long id, String username, String password, String email, String fullName, UserEnum role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

}
