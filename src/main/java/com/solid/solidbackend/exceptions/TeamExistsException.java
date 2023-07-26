package com.solid.solidbackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TeamExistsException extends RuntimeException{
    public TeamExistsException(String teamName) {
        super(" Team with name `" + teamName + "` already exists! ");
    }
}
