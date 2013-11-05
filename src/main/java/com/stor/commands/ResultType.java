package com.stor.commands;

/**
 * An enum used to describe the Result implementation
 * for a given Result class.
 *
 * The ResultType is used to identify the type of Result given
 * without requiring techniques such as reflection to identify
 * a specific implementation class.
 *
 * The ResultType can be used to quickly identify a success or
 * failure without requiring the need to analyze the content of
 * the concrete result.
 */
public enum ResultType {

    SUCCESS("SUCCESS"),
    FAILURE("FAILURE"),
    STATUS("STATUS");

    private final String text;

    ResultType(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }

}
