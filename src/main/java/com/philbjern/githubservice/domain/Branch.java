package com.philbjern.githubservice.domain;

import lombok.Data;

@Data
public class Branch {

    private String name;

    private String lastCommitSHA;

}
