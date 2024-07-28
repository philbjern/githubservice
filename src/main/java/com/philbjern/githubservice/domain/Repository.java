package com.philbjern.githubservice.domain;

import java.util.List;


public record Repository (String repositoryName, String ownerLogin, List<Branch> branches) {};
