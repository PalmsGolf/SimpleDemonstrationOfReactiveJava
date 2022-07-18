package com.mike.projectreactortest.exceptions;

import java.io.IOException;

public class DataBaseException extends IOException {

    public DataBaseException(String errorMessage) {
        super(errorMessage);
    }
}
