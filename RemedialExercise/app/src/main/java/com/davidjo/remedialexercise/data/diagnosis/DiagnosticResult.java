package com.davidjo.remedialexercise.data.diagnosis;

public class DiagnosticResult {

    public final boolean positive;
    public final String message;

    public DiagnosticResult(boolean positive, String message) {
        this.positive = positive;
        this.message = message;
    }

}
