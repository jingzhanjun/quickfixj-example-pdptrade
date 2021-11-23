package com.pactera.fix.custom;

import quickfix.IntField;

/**
 * The trade execution model of the executable streaming price
 */
public class OneClickAction extends IntField {
    static final long serialVersionUID = 20050617L;
    public static final int FIELD = 20003;
    public static final int FILL_AT_MY_RATE_ONLY = 1;
    public static final int FILL_AT_LATEST = 2;
    public static final int SLIPPAGE = 3;

    public OneClickAction() {
        super(FIELD);
    }

    public OneClickAction(int data) {
        super(FIELD, data);
    }
}