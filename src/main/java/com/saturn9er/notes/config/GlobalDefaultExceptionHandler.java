package com.saturn9er.notes.config;

import com.saturn9er.notes.exception.ResourceForbiddenException;
import com.saturn9er.notes.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The type Global default exception handler.
 */
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    /**
     * Resource forbidden exception handler string.
     *
     * @param ex the exception
     * @return the string
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ResourceForbiddenException.class)
    @MessageExceptionHandler(ResourceForbiddenException.class)
    public String resourceForbiddenExceptionHandler(Exception ex) {
        return "/error/403";
    }

    /**
     * Resource not found exception handler string.
     *
     * @param ex the exception
     * @return the string
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    @MessageExceptionHandler(ResourceNotFoundException.class)
    public String resourceNotFoundExceptionHandler(Exception ex) {
        return "/error/404";
    }

    /**
     * Access exception handler string.
     *
     * @param ex the exception
     * @return the string
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    @MessageExceptionHandler(Exception.class)
    public String accessExceptionHandler(Exception ex) {
        return "/error/index";
    }
}
