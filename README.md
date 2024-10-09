# Github Repositories Search Service
Github Repositories search service is a server application that enables searching for any github user repositories that are not forks. For this app to work, you need to provide a github authentication token in a `github.properties` file. 

## `github.properties` file
App searches for a properties file with Github API key in path `src/main/resources/github.properties` 

`github.properties` format
```
github.api.url=https://api.github.com/
github.api.key={your_github_api_key}
```

## Application endpoint
After launching, app is available at this address `http://localhost:8081/api/{username}`
Different port can be chosed by modifing `application.properties` and setting different port as `server.port` property.

## Sample output
Here is (reduced for brevity) sample server response for my account.

`$ curl http://localhost:8081/api/philbjern`

```
[
    {
        "repositoryName": "freecodecamp-projects",
        "ownerLogin": "philbjern",
        "branches": [
            {
                "name": "main",
                "lastCommitSHA": "55266d873b6c49a5000fbab12a4337e6181a2e75"
            }
        ]
    },
    {
        "repositoryName": "ttss-client",
        "ownerLogin": "philbjern",
        "branches": [
            {
                "name": "master",
                "lastCommitSHA": "2a258b363a3318e0e41b91be57f58fae3421d7d4"
            }
        ]
    },
    {
        "repositoryName": "dotfiles",
        "ownerLogin": "philbjern",
        "branches": [
            {
                "name": "main",
                "lastCommitSHA": "508e63969bbc588e3adec5886b33fe584d4ce665"
            }
        ]
    },
    {
        "repositoryName": "philbjern",
        "ownerLogin": "philbjern",
        "branches": [
            {
                "name": "main",
                "lastCommitSHA": "e15018abf431fd224da9025b5c47d960c8e556e1"
            }
        ]
    },
    ...
]
```