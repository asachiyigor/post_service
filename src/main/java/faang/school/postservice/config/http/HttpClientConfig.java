package faang.school.postservice.config.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class HttpClientConfig {

    @Bean
    HttpClient httpClient() {
        return HttpClient.newHttpClient();
    };
}
