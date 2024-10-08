package com.philbjern.githubservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@PropertySource("github.properties")
@Configuration
public class AppConfig {

//    @Bean
//    public GithubAPIService githubAPIService() throws IOException {
//        GithubAPIService github = new GithubAPIService();
//        github.setup();
//        return github;
//    }

    @Value("github.api.key")
    String myPersonalAccessToken;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
