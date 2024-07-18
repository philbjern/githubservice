package com.philbjern.githubservice.service;

import com.philbjern.githubservice.domain.Branch;
import com.philbjern.githubservice.domain.Repository;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        } catch (IOException e) {
            log.error("Initialization from property file unsuccessfull, {}", e.getMessage());
            github = null;
        }

        try {
            if (github == null) {
                initFromDefaultPropertyFile();
            }
        } catch (IOException e) {
            log.error("Initialization from default property file unsuccessfull, {}", e.getMessage());
            github = null;
        }

        try {
            if(github == null) {
                initFromEnvironmentVariable();
            }
        }
        catch (IOException e) {
            log.error("Initialization from environment variables unsuccessfull, " +
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
        GHUser user;
        try {
            user = github.getUser(username);
        } catch (IOException e) {
            throw new IOException("Provided username does not exist.");
        }

        log.info("Getting user {} repositories", user.getName());
        Map<String, GHRepository> userRepos = user.getRepositories();
        Map<String, GHRepository> notForkedRepos = getNotForkedRepositories(userRepos);

        notForkedRepos.entrySet().stream().forEach(entry -> {
            Repository repo = new Repository();
            repo.setRepositoryName(entry.getKey());
            repo.setOwnerLogin(entry.getValue().getOwnerName());

            try {
                Map<String, GHBranch> branches = entry.getValue().getBranches();
                branches.entrySet().forEach(entry1 -> {
                    Branch branch = new Branch();
                    branch.setName(entry1.getKey());
                    branch.setLastCommitSHA(entry1.getValue().getSHA1());
                    repo.addBranch(branch);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            repos.add(repo);
        });
        log.info("User repositories fetched successfully, user={}, repos={}", username, repos);
        return repos;
    }

    private Map<String, GHRepository> getNotForkedRepositories(Map<String, GHRepository> repos) {
        Map<String, GHRepository> notForkedRepos = new HashMap<>();
        repos.entrySet().forEach(entry -> {
            String repoName = entry.getKey();
            if (!entry.getValue().isFork()) {
                notForkedRepos.put(repoName, entry.getValue());
            }
        });
        return notForkedRepos;
    }

}
