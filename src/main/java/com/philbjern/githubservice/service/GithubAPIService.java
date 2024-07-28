package com.philbjern.githubservice.service;

import com.philbjern.githubservice.domain.Branch;
import com.philbjern.githubservice.domain.Repository;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GithubAPIService {

    private GitHub github;

    private final List<Repository> repos = new ArrayList<>();

    public GithubAPIService() throws IOException {
        setupGithubConnection();
    }

    private void setupGithubConnection() throws IOException {
        try {
            initFromCustomPropertyFile();
            log.info("Github connection setup using custom property file successful");
        } catch (IOException e) {
            log.error("Initialization from property file unsuccessful, {}", e.getMessage());
        }

        try {
            initFromDefaultPropertyFile();
            log.info("Github connection setup using default property file successful");
        } catch (IOException e) {
            log.error("Initialization from default property file unsuccessful, {}", e.getMessage());
        }

        try {
            initFromEnvironmentVariable();
            log.info("Github connection setup using environment variable successful");
        }
        catch (IOException e) {
            log.error("Initialization from environment variables unsuccessful, " +
                    "try to export GITHUB_OAUTH token variable. {}", e.getMessage());
            throw e;
        }
    }

    private void initFromEnvironmentVariable() throws IOException {
        github = GitHubBuilder.fromEnvironment().build();
    }

    private void initFromCustomPropertyFile() throws IOException {
        github = GitHubBuilder.fromPropertyFile("src/main/resources/.github").build();
    }

    private void initFromDefaultPropertyFile() throws IOException {
        github = GitHubBuilder.fromPropertyFile().build();
    }

    public List<Repository> getUserRepositoriesData(String username) throws IOException {
        Optional<GHUser> user = Optional.ofNullable(github.getUser(username));
        if (user.isEmpty()) {
            throw new IOException("Provided username does not exist.");
        }

        log.info("Getting user {} repositories", user.get().getName());
        Map<String, GHRepository> userRepos = user.get().getRepositories();
        Map<String, GHRepository> notForkedRepos = getNotForkedRepositories(userRepos);

        notForkedRepos.entrySet().stream().forEach(entry -> {
            List<Branch> repoBranches = new ArrayList<>();
            try {
                Map<String, GHBranch> branches = entry.getValue().getBranches();
                branches.entrySet().forEach(entry1 -> {
                    Branch branch = new Branch(entry1.getKey(), entry1.getValue().getSHA1());
                    repoBranches.add(branch);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Repository repo = new Repository(entry.getKey(), entry.getValue().getOwnerName(), repoBranches);
        });
        log.info("User repositories fetched successfully, user={}, repos={}", username, repos);
        return repos;
    }

    private Map<String, GHRepository> getNotForkedRepositories(Map<String, GHRepository> repos) {
        return repos.entrySet().stream()
                .filter(entry -> !entry.getValue().isFork())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
