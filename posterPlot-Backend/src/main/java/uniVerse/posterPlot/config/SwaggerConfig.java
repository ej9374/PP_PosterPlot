package uniVerse.posterPlot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("v1.0") //버전
                .title("PosterPlot API") //이름
                .description("AI 기반 줄거리 생성과 유저 작성 시나리오를 바탕으로, 유사 영화 추천 및 시나리오 열람 기능을 제공하는 웹 플랫폼 프로젝트 API"); //설명
        return new OpenAPI()
                .info(info);
    }

}