package com.philbjern.githubservice.entity;

import lombok.Data;

@Data
public class Branch {

    String name;

    Commit commit;

}