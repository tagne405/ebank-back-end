package com.tyannick.ebanque.Exception;

public class BalanceNotSufficientException extends Exception{
    public BalanceNotSufficientException(String message){
         super(message);
    }
}
