package com.pactera.fix.custom;

import quickfix.DoubleField;

/**
 * The acceptable amount that the executed rate can deviate from the rate submitted by the price taker.
 * For example: “0.0005”
 */
public class OneClickTolerance extends DoubleField {
    private static final long serialVersionUID = 8615422599800202355L;
    public static final int FIELD = 20004;

    public OneClickTolerance() {
        super(FIELD);
    }

    public OneClickTolerance(double data) {
        super(FIELD, data);
    }
}