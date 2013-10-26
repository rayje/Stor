package com.stor.commands;

/**
 * An enum used to describe the implementation for a
 * given Command class.
 *
 * The CommandType is used to identify the type of Command given
 * without requiring techniques such as reflection to identify
 * a specific implementation class.
 *
 * The CommandType can be used to quickly identify the type of
 * Command given to a method invocation and generate the
 * appropriate handler that can process the specific Command,
 * such as in the factory pattern.
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
