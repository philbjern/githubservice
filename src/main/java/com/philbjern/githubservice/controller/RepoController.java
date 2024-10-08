package com.philbjern.githubservice.controller;

import com.philbjern.githubservice.domain.Branch;
import com.philbjern.githubservice.domain.GithubAPIUsersResponse;
import com.philbjern.githubservice.dto.ErrorDTO;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api")
@AllArgsConstructor
@Slf4j
public class RepoController {

    private RestTemplate restTemplate;

    @GetMapping(path = "/{username}", produces = "application/json")
    public ResponseEntity<String> getUserReposWithoutForks(@PathVariable String username) throws IOException {
        String url = "https://api.github.com/users/" + username + "/repos";
        log.info("Url: {}", url);

        ResponseEntity<List<GithubAPIUsersResponse>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<GithubAPIUsersResponse>>() {});

        log.info("Github API Response: {}", response.getBody().get(0).getName());

        for(GithubAPIUsersResponse repo : response.getBody()) {

            String repoUrlForBranches = repo.getBranches_url();
            String urlBranches = repoUrlForBranches.substring(0, repoUrlForBranches.length() - "{/branch}".length());

            ResponseEntity<List<Branch>> branchesList = restTemplate.exchange(urlBranches, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Branch>>() {});

            for (Branch branch : branchesList.getBody()) {
                log.info("Branch name: {}, last commit SHA: {}", branch.getName(), branch.getCommit().getSha());
            }

        }

        return ResponseEntity.ok("ok");
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorDTO> handleIOException(IOException e) {
        ErrorDTO error = ErrorDTO.builder().errorCode(404).message(e.getMessage()).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}
