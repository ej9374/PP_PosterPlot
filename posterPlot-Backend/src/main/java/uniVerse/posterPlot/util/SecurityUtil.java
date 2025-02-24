package uniVerse.posterPlot.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uniVerse.posterPlot.entity.UserEntity;

public class SecurityUtil {

    public static UserEntity getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("인증 정보가 없습니다. 로그인이 필요합니다.");
        }

        if ("anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("익명 사용자는 접근할 수 없습니다. 로그인이 필요합니다.");
        }

        if (!(authentication.getPrincipal() instanceof UserEntity)) {
            throw new RuntimeException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        return (UserEntity) authentication.getPrincipal();
    }
}
