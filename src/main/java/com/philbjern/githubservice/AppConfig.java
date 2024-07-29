package com.philbjern.githubservice;

import com.philbjern.githubservice.service.GithubAPIService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class AppConfig {

    @Bean
    public GithubAPIService githubAPIService() throws IOException {
        GithubAPIService github = new GithubAPIService();
        github.setup();
        return github;
    }

}
