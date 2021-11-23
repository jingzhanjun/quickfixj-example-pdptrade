package com.pactera.fix.custom;

import quickfix.DoubleField;

/**
 * Value from the streaming price in 35=X or 35=W
 */
public class StreamingQuote extends DoubleField {
    private static final long serialVersionUID = 5163783278672332296L;
    public static final int FIELD = 20005;

    public StreamingQuote() {
        super(FIELD);
    }

    public StreamingQuote(double data) {
        super(FIELD, data);
    }
}