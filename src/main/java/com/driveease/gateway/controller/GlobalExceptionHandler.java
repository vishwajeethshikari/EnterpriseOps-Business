
package com.driveease.gateway.controller;

import com.driveease.gateway.exception.ApiError;
import com.driveease.gateway.exception.ResourceNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(
            ResourceNotFoundException exception) {
        return new ApiError(exception.getMessage());
    }

@ExceptionHandler(IllegalArgumentException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public ApiError handleBadRequest(
        IllegalArgumentException exception) {
    return new ApiError(exception.getMessage());
}

}
