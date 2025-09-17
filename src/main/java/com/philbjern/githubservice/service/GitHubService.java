package com.philbjern.githubservice.service;

import com.philbjern.githubservice.dto.Response;
import com.philbjern.githubservice.entity.Branch;
import com.philbjern.githubservice.entity.GithubUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class GitHubService {

    private final WebClient webClient;

    public GitHubService(WebClient.Builder webClientBuilder,
                                        @Value("${github.api.key}") String githubKey,
                                        @Value("${github.api.url}") String githubApiUrl) {
        this.webClient = webClientBuilder
                .baseUrl(githubApiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubKey)
                .build();

        log.info("Github API base url: {}", githubApiUrl);
    }

    public Mono<ResponseEntity<List<Response>>> getReposWithoutForks(String username) {
        return webClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .bodyToFlux(GithubUser.class)
                .filter(repo -> !repo.isFork())
                .take(5)
                .flatMap(repo -> {
                    String urlBranches = repo.getBranchesUrl()
                            .replace("{/branch}", "");

                    return webClient.get()
                            .uri(urlBranches)
                            .retrieve()
                            .bodyToFlux(Branch.class)
                            .take(10)
                            .collectList()
                            .map(branches -> {
                                Response resp = new Response();
                                resp.setRepositoryName(repo.getName());
                                resp.setOwnerLogin(repo.getOwner().getLogin());
                                resp.setBranches(branches);
                                return resp;
                            });
                })
                .collectList()
                .map(ResponseEntity::ok);
    }

}
