package com.elrain.bashim.util;

/**
 * Created by denys.husher on 04.11.2015.
 */
public class CounterOfNewItems {
    private int quotesCounter;
    private static final CounterOfNewItems ourInstance = new CounterOfNewItems();

    public static CounterOfNewItems getInstance() {
        return ourInstance;
    }

    private CounterOfNewItems() {
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
