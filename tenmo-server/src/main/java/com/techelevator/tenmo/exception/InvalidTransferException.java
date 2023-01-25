package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.http.HttpResponse;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class InvalidTransferException extends Exception {

    public InvalidTransferException() {
        super();
    }
}
