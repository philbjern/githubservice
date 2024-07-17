package com.philbjern.githubservice.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorDTO {

    private Integer errorCode;
    private String message;

}
