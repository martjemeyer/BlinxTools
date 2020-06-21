package com.dummycoding.mycrypto.models;

public class OutputModel {
    String output;
    int newCursorPosition;

    public OutputModel(String output, int newCursorPosition) {
        this.output = output;
        this.newCursorPosition = newCursorPosition;
    }

    public String getOutput() {
        return output;
    }

    public int getNewCursorPosition() {
        return newCursorPosition;
    }
}
