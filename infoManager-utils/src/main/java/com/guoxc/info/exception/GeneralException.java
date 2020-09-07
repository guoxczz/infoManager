package com.guoxc.info.exception;

public class GeneralException extends Exception {

    private String errorCode;
    private String provinceCode;

    public GeneralException(String errorCode) {
        this.errorCode = errorCode;
    }


    public GeneralException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public GeneralException(String errorCode, Throwable cause) {
        this(errorCode, errorCode, cause);
    }

    public GeneralException(String errorCode, String message, String provinceCode) {
        super(message);
        this.errorCode = errorCode;
        this.provinceCode = provinceCode;
    }

    public GeneralException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public GeneralException(String errorCode, String message, String provinceCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.provinceCode = provinceCode;
    }

//    public String getProvinceCode() {
//        if (provinceCode == null || "".equalsIgnoreCase(provinceCode)) {
//            return ErrorConstants.DEFAULT_PROVINCECODE;
//        } else {
//            return provinceCode;
//        }
//    }

    public String getErrorCode() {
        if (errorCode == null || "".equalsIgnoreCase(errorCode)) {
            return  "-1" ;
        } else {
            return errorCode;
        }
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message == null || "".equalsIgnoreCase(message)) {
                message ="empty Exception ";
        }
        return message;
    }

}
