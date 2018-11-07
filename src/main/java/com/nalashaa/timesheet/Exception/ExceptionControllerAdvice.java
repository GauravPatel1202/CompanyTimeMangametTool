
package com.nalashaa.timesheet.Exception;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.nalashaa.timesheet.util.GenericResponseDataBlock;

@ControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger logger = LogManager.getLogger(ExceptionControllerAdvice.class.getName());

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponseDataBlock> exceptionHandler(Exception ex) {
        logger.error("Exception occured details are" , ex);
        GenericResponseDataBlock error = new GenericResponseDataBlock();
        error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setMessage(ex.getMessage());
        return new ResponseEntity<GenericResponseDataBlock>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TimeSheetException.class)
    public ResponseEntity<GenericResponseDataBlock> exceptionHandler(TimeSheetException ex) {
        logger.error("Timesheet exception details are " , ex);
        logger.error(ex.getMessage());
        GenericResponseDataBlock response = new GenericResponseDataBlock();
        response.setMessage(ex.getMessage());
        response.setSuccess(false);
        response.setStatusCode(500);
        return new ResponseEntity<GenericResponseDataBlock>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}