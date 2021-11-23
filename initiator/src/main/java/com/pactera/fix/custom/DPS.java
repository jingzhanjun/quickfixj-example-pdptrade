package com.pactera.fix.custom;

import quickfix.IntField;

/**
 *
 */
public class DPS extends IntField {
    static final long serialVersionUID = 20050617L;
    public static final int FIELD = 20001;

    public DPS() {
        super(FIELD);
    }

    public DPS(int data) {
        super(FIELD, data);
    }
}