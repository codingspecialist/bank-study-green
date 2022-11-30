package shop.mtcoding.bank.domain.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("select u from User u join fetch u.accounts ac where ac.isActive = true and u.id = :userId")
    User findByActiveUserIdv3(@Param("userId") Long userId);
}
