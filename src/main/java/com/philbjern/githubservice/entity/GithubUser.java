package com.philbjern.githubservice.entity;

import lombok.Data;

@Data
public class GithubUser {

    String name;

    boolean fork;

    Owner owner;

    String branchesUrl;

}
