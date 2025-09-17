package com.philbjern.githubservice.controller;

import com.philbjern.githubservice.dto.ErrorDTO;
import com.philbjern.githubservice.dto.Response;
import com.philbjern.githubservice.service.GitHubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api")
@Slf4j
@EnableCaching
@RequiredArgsConstructor
public class RepoController {

    private final GitHubService githubService;

    @Cacheable(value = "userRepos", key = "#username")
    @GetMapping(path = "/{username}", produces = "application/json")
    public Mono<ResponseEntity<List<Response>>> getUserReposWithoutForks(@PathVariable String username) {
        return githubService.getReposWithoutForks(username);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleException(Exception e) {
        ErrorDTO error = ErrorDTO.builder()
                .errorCode(404)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}
