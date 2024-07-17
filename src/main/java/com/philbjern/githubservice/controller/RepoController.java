package com.philbjern.githubservice.controller;

import com.philbjern.githubservice.domain.Repository;
import com.philbjern.githubservice.dto.ErrorDTO;
import com.philbjern.githubservice.service.GithubAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> getUserReposWithoutForks(@PathVariable String username) {
        try {
            List<Repository> repos = githubService.getUserRepositoriesData(username);
            return ResponseEntity.ok(repos);
        } catch (IOException e) {
            ErrorDTO error = new ErrorDTO();
            error.setErrorCode(404);
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }


}
