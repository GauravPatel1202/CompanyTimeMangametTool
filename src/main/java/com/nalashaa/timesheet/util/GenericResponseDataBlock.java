
package com.nalashaa.timesheet.util;

public class GenericResponseDataBlock {

    private Boolean success;
    private Integer statusCode;
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder grdbSB = new StringBuilder("");
        grdbSB.append("isSuccess : ").append(getSuccess()).append("statusCode : ").append(getStatusCode()).append("message : ").append(getMessage());
        return grdbSB.toString();
    }
}