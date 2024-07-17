package com.philbjern.githubservice.domain;

import lombok.Data;
import org.kohsuke.github.GHBranch;

import java.util.ArrayList;
import java.util.List;

@Data
public class Repository {
    private String repositoryName;

    private String ownerLogin;

    private List<Branch> branches = new ArrayList<>();

    public void addBranch(Branch branch) {
        this.branches.add(branch);
    }
}
