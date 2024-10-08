package com.philbjern.githubservice.domain;

import lombok.Data;

@Data
public class GithubAPIUsersResponse {

    String name;

    Boolean fork;

    Owner owner;

    String branches_url;

}
