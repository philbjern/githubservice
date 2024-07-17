package com.philbjern.githubservice.controller;

import com.philbjern.githubservice.service.GithubAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class RepoController {

    @Autowired
    public GithubAPIService githubService;

    @GetMapping(path = "/{username}", produces = "application/json")
    public ResponseEntity<String> getUserReposWithoutForks(@PathVariable String username) {
        githubService.test(username);
        return ResponseEntity.ok(username);
    }


}
