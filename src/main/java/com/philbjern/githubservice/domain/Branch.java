package com.philbjern.githubservice.domain;

import lombok.Data;

import java.util.List;

@Data
public class Branch {

    String name;

    Commit commit;

}