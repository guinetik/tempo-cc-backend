package com.guinetik.challenge.remote;

/**
 * Exception class for errors during remote API execution
 */
public class RemoteApiException extends RuntimeException {
    public RemoteApiException(String message) {
        super(message);
    }
}
