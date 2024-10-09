package com.philbjern.githubservice.controller;

import com.philbjern.githubservice.domain.Branch;
import com.philbjern.githubservice.domain.GithubAPIUsersResponse;
import com.philbjern.githubservice.dto.ErrorDTO;
import com.philbjern.githubservice.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api")
@Slf4j
@PropertySource("github.properties")
public class RepoController {

    private RestTemplate restTemplate;

    public RepoController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${github.api.key}")
    String githubPersonalAccessToken;

    @GetMapping(path = "/{username}", produces = "application/json")
    public ResponseEntity<List<Response>> getUserReposWithoutForks(@PathVariable String username) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubPersonalAccessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = "https://api.github.com/users/" + username + "/repos";
        log.info("Url: {}", url);

        ResponseEntity<List<GithubAPIUsersResponse>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
                new ParameterizedTypeReference<List<GithubAPIUsersResponse>>() {});

        log.info("Github API Response: {}", response.getBody().get(0).getName());

        List<Response> respList = new ArrayList<>();

        for(GithubAPIUsersResponse repo : response.getBody()) {
            Response resp = new Response();
            resp.setRepositoryName(repo.getName());
            resp.setOwnerLogin(repo.getOwner().getLogin());

            String repoUrlForBranches = repo.getBranches_url();
            String urlBranches = repoUrlForBranches.substring(0, repoUrlForBranches.length() - "{/branch}".length());

            ResponseEntity<List<Branch>> branchesList = restTemplate.exchange(urlBranches, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<List<Branch>>() {});

            List<Branch> branches = new ArrayList<>();
            for (Branch branch : branchesList.getBody()) {
                log.info("Branch name: {}, last commit SHA: {}", branch.getName(), branch.getCommit().getSha());
                branches.add(branch);
            }

            resp.setBranches(branches);
            respList.add(resp);
        }

        return ResponseEntity.ok(respList);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorDTO> handleIOException(IOException e) {
        ErrorDTO error = ErrorDTO.builder().errorCode(404).message(e.getMessage()).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}
