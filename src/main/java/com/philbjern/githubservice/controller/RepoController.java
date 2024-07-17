package com.philbjern.githubservice.controller;

import com.philbjern.githubservice.domain.Repository;
import com.philbjern.githubservice.dto.ErrorDTO;
import com.philbjern.githubservice.service.GithubAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class RepoController {

    @Autowired
    public GithubAPIService githubService;

    @GetMapping(path = "/{username}", produces = "application/json")
    public ResponseEntity<?> getUserReposWithoutForks(@PathVariable String username) throws IOException {
        List<Repository> repos = githubService.getUserRepositoriesData(username);
        if (repos != null) {
            return ResponseEntity.ok(repos);
        }
        ErrorDTO error = new ErrorDTO();
        return ResponseEntity.notFound().build();
    }


}
