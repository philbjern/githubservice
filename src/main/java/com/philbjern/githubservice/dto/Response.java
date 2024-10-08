package com.philbjern.githubservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class Response {

    String repositoryName;

    String ownerLogin;

    List<BranchDTO> branches;

}
