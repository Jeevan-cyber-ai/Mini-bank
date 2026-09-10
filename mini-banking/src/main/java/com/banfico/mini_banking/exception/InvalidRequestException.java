// exception/InvalidRequestException.java
package com.banfico.mini_banking.exception;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}