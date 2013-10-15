package com.stor.commands;

/**
 * User: rayje
 * Date: 10/14/13
 * Time: 9:30 PM
 */
public enum CommandType {

    PUT("PUT"),
    GET("GET"),
    DELETE("DELETE");

    private final String text;

    CommandType(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }
}
