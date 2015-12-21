package com.elrain.bashim.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

/**
 * Created by denys.husher on 17.12.2015.
 */
public abstract class RecyclerCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private Cursor mCursor;
    private final Context mContext;
    private boolean mDataValid;
    private int mIdColumnIndex;
    private DataSetObserver mObserver;

    RecyclerCursorAdapter(Context context, @Nullable Cursor cursor) {
        this.mCursor = cursor;
        this.mContext = context;
        mDataValid = null != mCursor;
        if (mDataValid) {
            mIdColumnIndex = mCursor.getColumnIndex("_id");
            mObserver = new MyDataSetObserver();
            mCursor.registerDataSetObserver(mObserver);
        }
    }

    Context getContext() {
        return mContext;
    }

    protected abstract void onBindViewHolder(VH holder, Cursor cursor);

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null)
            if (mCursor.moveToPosition(position)) return mCursor.getLong(mIdColumnIndex);
            else return 0;
        else return 0;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(holder, mCursor);
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) return mCursor.getCount();
        else return 0;
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     *
     * @param cursor The new cursor to be used
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     *
     * @param newCursor The new cursor to be used.
     * @return Returns the previously set Cursor, or null if there wasa not one.
     * If the given new Cursor is the same instance is the previously set
     * Cursor, null is also returned.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
            if (mObserver != null) oldCursor.unregisterDataSetObserver(mObserver);
        }
        mCursor = newCursor;
        if (newCursor != null) {
            if (mObserver != null) newCursor.registerDataSetObserver(mObserver);
            mIdColumnIndex = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mIdColumnIndex = -1;
            mDataValid = false;
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
        }
    }
}
