package com.philbjern.githubservice.entity;

import java.util.List;


public record Repository (String repositoryName, String ownerLogin, List<Branch> branches) {};
