package com.philbjern.githubservice.controller;

import com.philbjern.githubservice.domain.Repository;
import com.philbjern.githubservice.dto.ErrorDTO;
import com.philbjern.githubservice.service.GithubAPIService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api")
@AllArgsConstructor
public class RepoController {

    private GithubAPIService githubService;

    @GetMapping(path = "/{username}", produces = "application/json")
    public ResponseEntity<List<Repository>> getUserReposWithoutForks(@PathVariable String username) throws IOException {
        List<Repository> repos = githubService.getUserRepositoriesData(username);
        return ResponseEntity.ok(repos);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorDTO> handleIOException(IOException e) {
        ErrorDTO error = ErrorDTO.builder().errorCode(404).message(e.getMessage()).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}
