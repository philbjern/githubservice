package com.philbjern.githubservice.controller;

import com.philbjern.githubservice.entity.Branch;
import com.philbjern.githubservice.entity.GithubUser;
import com.philbjern.githubservice.dto.ErrorDTO;
import com.philbjern.githubservice.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api")
@Slf4j
@PropertySource("github.properties")
@EnableCaching
public class RepoController {

    private final WebClient webClient;

    public RepoController(WebClient.Builder webClientBuilder,
                          @Value("${github.api.key}") String githubKey) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubKey)
                .build();
    }

    @Cacheable(value = "userRepos", key = "#username")
    @GetMapping(path = "/{username}", produces = "application/json")
    public Mono<ResponseEntity<List<Response>>> getUserReposWithoutForks(@PathVariable String username) {
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleException(IOException e) {
        ErrorDTO error = ErrorDTO.builder()
                .errorCode(404)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}
