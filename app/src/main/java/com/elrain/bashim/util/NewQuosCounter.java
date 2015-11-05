package com.elrain.bashim.util;

/**
 * Created by denys.husher on 04.11.2015.
 */
public class NewQuosCounter {
    private int counter;
    private static NewQuosCounter ourInstance = new NewQuosCounter();

    public static NewQuosCounter getInstance() {
        return ourInstance;
    }

    private NewQuosCounter() {
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
