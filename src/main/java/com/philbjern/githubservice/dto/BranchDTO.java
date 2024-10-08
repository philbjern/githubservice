package com.philbjern.githubservice.dto;

import lombok.Data;

@Data
public class BranchDTO {

    String name;

    String lastCommitSHA;

}
