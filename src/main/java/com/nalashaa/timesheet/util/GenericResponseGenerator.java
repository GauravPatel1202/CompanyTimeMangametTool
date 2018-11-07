package com.nalashaa.timesheet.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GenericResponseGenerator {

	public static ResponseEntity<GenericResponseDataBlock>  getGenericResponse(String message, boolean status,int statusCode, HttpStatus httpStatus ){
		  GenericResponseDataBlock msg = new GenericResponseDataBlock();
	        msg.setMessage(message);
	        msg.setSuccess(status);
	        msg.setStatusCode(statusCode);
	        HttpStatus hstatus=null;
	        switch (httpStatus) {
	        case OK :
				hstatus = HttpStatus.OK;
				break;
			case BAD_REQUEST :
				hstatus = HttpStatus.BAD_REQUEST;
				break;
			case FORBIDDEN :
				hstatus = HttpStatus.FORBIDDEN;
				break;
			case INTERNAL_SERVER_ERROR :
				hstatus = HttpStatus.INTERNAL_SERVER_ERROR;
				break;
			default:
				hstatus = HttpStatus.OK;
				break;
			}
	       return new ResponseEntity<GenericResponseDataBlock>(msg,hstatus);
	}
}
