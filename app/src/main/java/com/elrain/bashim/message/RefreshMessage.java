package com.elrain.bashim.message;

import android.app.Fragment;

/**
 * Created by denys.husher on 26.11.2015.
 */
public final class RefreshMessage {

    public final State mState;
    public final Fragment mFrom;

    public RefreshMessage(State state, Fragment from) {
        this.mState = state;
        this.mFrom = from;
    }

    public enum State {
        STARTED, FINISHED
    }
}
