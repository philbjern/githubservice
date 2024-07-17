package com.philbjern.githubservice.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubAPIService {

    public void test(String username) {
        log.info("GithubAPIService test {}", username);
    }

}
