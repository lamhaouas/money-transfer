package com.techelevator.tenmo.exceptions;

public class InvalidTransferException extends Exception{
    public InvalidTransferException(){
        super("Transfer is invalid");
    }
}
