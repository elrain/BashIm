package com.elrain.bashim.message;

import android.app.Fragment;

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
