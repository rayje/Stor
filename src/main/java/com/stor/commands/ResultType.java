package com.stor.commands;

/**
 * User: rayje
 * Date: 10/14/13
 * Time: 9:46 PM
 */
public enum ResultType {

    SUCCESS("SUCCESS"),
    FAILURE("FAILURE");

    private final String text;

    ResultType(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }

}
