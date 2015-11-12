package com.elrain.bashim.util;

/**
 * Created by denys.husher on 04.11.2015.
 */
public class CounterOfNewItems {
    private int quotesCounter;
    private int comicsCounter;
    private static final CounterOfNewItems ourInstance = new CounterOfNewItems();

    public static CounterOfNewItems getInstance() {
        return ourInstance;
    }

    private CounterOfNewItems() {
    }

    public int getQuotesCounter() {
        return quotesCounter;
    }

    public int getComicsCounter() {
        return comicsCounter;
    }

    public void addQuotes() {
        ++quotesCounter;
    }

    public void addComics() {
        ++comicsCounter;
    }

    public void setCounterTooZero() {
        quotesCounter = 0;
        comicsCounter = 0;
    }
}
