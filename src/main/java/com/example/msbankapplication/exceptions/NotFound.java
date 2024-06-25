package com.example.msbankapplication.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class NotFound extends RuntimeException{
    private String errorMessage;
    private String logMessage;
    public NotFound(String errorMessage,String logMessage){
        super(errorMessage);
        this.errorMessage=errorMessage;
        this.logMessage=logMessage;
    }
}
