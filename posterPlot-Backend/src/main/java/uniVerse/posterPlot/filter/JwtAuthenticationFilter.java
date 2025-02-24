package uniVerse.posterPlot.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import uniVerse.posterPlot.repository.UserRepository;
import uniVerse.posterPlot.util.JwtProvider;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        log.info("Authorization 헤더 값: {}", request.getHeader("Authorization"));

        if (token != null) {
            log.info("추출된 JWT 토큰: {}", token);
            String id = jwtProvider.validate(token); // 유효한 토큰이라면 ID 반환

            if (id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("JWT 검증 성공, 사용자 ID: {}", id);
                userRepository.findById(id).ifPresent(user -> {
                    // 사용자의 권한 정보 추가
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Spring Security에 인증 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("인증 성공: id={}", id);
                });
            } else {
                log.warn("JWT 검증 실패 또는 이미 인증된 사용자입니다.");
            }
        } else {
            log.warn("JWT 토큰이 요청에 포함되지 않았습니다.");
        }

        // 다음 필터 진행
        filterChain.doFilter(request, response);
    }


    private String extractToken(HttpServletRequest request) {
        // 1. Authorization 헤더 확인
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null){
            log.warn("Authorization 헤더가 존재하지 않습니다.");
            return null;
        }
        log.info("Authorization 헤더: {}", bearerToken);  // 추가 로그
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. URL 파라미터 확인
        String paramToken = request.getParameter("token");
        if (StringUtils.hasText(paramToken)) {
            log.debug("URL 파라미터 토큰: {}", paramToken);
            return paramToken;
        }
        return null;
    }
}
