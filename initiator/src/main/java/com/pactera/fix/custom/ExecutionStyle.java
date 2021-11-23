package com.pactera.fix.custom;

import quickfix.IntField;

/**
 * TRADE TYPE:1-RFQ,2-RFS,3-OneClick
 */
public class ExecutionStyle extends IntField {
    static final long serialVersionUID = 20050617L;
    public static final int FIELD = 20002;
    public static final int RFQ = 1;
    public static final int RFS = 2;
    public static final int ONE_CLICK = 3;

    public ExecutionStyle() {
        super(FIELD);
    }

    public ExecutionStyle(int data) {
        super(FIELD, data);
    }
}