package com.philbjern.githubservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDTO {

    private Integer errorCode;

    private String message;

}
