package shop.mtcoding.bank.dto;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.user.User;

public class UserRespDto {

    @Setter
    @Getter
    public static class JoinRespDto {
        private Long id;
        private String username;

        public JoinRespDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
        }

    }
}
