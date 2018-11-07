package com.nalashaa.timesheet.Exception;


public class TimeSheetException extends RuntimeException{

    public TimeSheetException(){
        super();
    }
    
    public TimeSheetException(String message){
        super(message);
    }
}
