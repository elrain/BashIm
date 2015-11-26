package com.elrain.bashim.util;

/**
 * Created by denys.husher on 04.11.2015.
 */
public final class CounterOfNewItems {
    private static final CounterOfNewItems ourInstance = new CounterOfNewItems();
    private int quotesCounter;

    private CounterOfNewItems() {
    }

    public static CounterOfNewItems getInstance() {
        return ourInstance;
    }

    public int getQuotesCounter() {
        return quotesCounter;
    }

    public void addQuotes() {
        ++quotesCounter;
    }

    public void setCounterTooZero() {
        quotesCounter = 0;
    }
}
