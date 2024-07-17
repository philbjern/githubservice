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

    List<Repository> repos = new ArrayList<>();

    public GithubAPIService() {
        try {
            initFromPropertyFile();
        } catch (IOException e) {
            log.error("Initialization from property file unsuccessfull");
        }
        try {
            if(github == null) {
                initFromEnvironemenVariable();
            }
        }
        catch (IOException e) {
            log.error("Initialization from environment variables unsuccessfull, " +
                    "try to export GITHUB_OAUTH token variable");
        }
    }

    private void initFromEnvironemenVariable() throws IOException {
        github = GitHubBuilder.fromEnvironment().build();
    }

    private void initFromPropertyFile() throws IOException {
        github = GitHubBuilder.fromPropertyFile("src/main/resources/.github").build();
    }

    public List<Repository> getUserRepositoriesData(String username) throws IOException {
        GHUser user = github.getUser(username);
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
        log.info("GithubAPIService test {}, {}", username, repos);
        return repos;
    }

    private Map<String, GHRepository> getNotForkedRepositories(Map<String, GHRepository> repos) {
        Map<String, GHRepository> notForkedRepos = new HashMap<>();
        repos.entrySet().stream().forEach(entry -> {
            String repoName = entry.getKey();
            if (!entry.getValue().isFork()) {
                notForkedRepos.put(repoName, entry.getValue());
            }
        });
        return notForkedRepos;
    }

}
