package uniVerse.posterPlot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;

@Configuration
public class SwaggerConfig {

    // JWT 인증 스키마 생성
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP) // HTTP 기반 인증
                .bearerFormat("JWT") // Bearer 포맷 사용
                .scheme("bearer"); // Bearer 스키마
    }

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("v1.0") //버전
                .title("PosterPlot API") //이름
                .description("AI 기반 줄거리 생성과 유저 작성 시나리오를 바탕으로, 유사 영화 추천 및 시나리오 열람 기능을 제공하는 웹 플랫폼 프로젝트 API"); //설명
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("JWT")) // JWT 인증 추가
                .components(new Components()
                        .addSecuritySchemes("JWT", createAPIKeyScheme()))// JWT
                .info(info);
    }
}