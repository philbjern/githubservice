package com.philbjern.githubservice.dto;

import com.philbjern.githubservice.domain.Branch;
import lombok.Data;

import java.util.List;

@Data
public class Response {

    String repositoryName;

    String ownerLogin;

    List<Branch> branches;

}
