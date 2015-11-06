package com.elrain.bashim.util;

/**
 * Created by denys.husher on 04.11.2015.
 */
public class NewQuotesCounter {
    private int counter;
    private static final NewQuotesCounter ourInstance = new NewQuotesCounter();

    public static NewQuotesCounter getInstance() {
        return ourInstance;
    }

    private NewQuotesCounter() {
    }

    public int getCounter() {
        return counter;
    }

    public void add() {
        ++counter;
    }

    public void setCounterTooZero() {
        counter = 0;
    }
}
