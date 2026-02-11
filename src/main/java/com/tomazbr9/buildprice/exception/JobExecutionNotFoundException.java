package com.tomazbr9.buildprice.exception;

public class JobExecutionNotFoundException extends RuntimeException {
    public JobExecutionNotFoundException(String message) {
        super(message);
    }
}
