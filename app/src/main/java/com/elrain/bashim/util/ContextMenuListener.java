package com.elrain.bashim.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;
import com.elrain.bashim.R;
import com.elrain.bashim.dal.QuotesTableHelper;

/**
 * Created by denys.husher on 08.12.2015.
 */
public class ContextMenuListener implements View.OnCreateContextMenuListener,
        AdapterView.OnItemLongClickListener {

    private static final String TYPE_COMICS = "comics";
    private static final String TYPE_QUOTES = "quotes";
    private static final String CLIP_LABEL = "label";

    private final Context mContext;
    private final boolean isBestOrRandom;
    private String mText;
    private String mLink;
    private String mAuthor;

    public ContextMenuListener(Context context, boolean isBestOrRandom) {
        this.mContext = context;
        this.isBestOrRandom = isBestOrRandom;
    }

    public void setLink(String link) {
        mLink = link;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(mContext.getString(R.string.menu_context_title));
        MenuItem miCopy = menu.add(0, v.getId(), 0, mContext.getString(R.string.menu_context_copy));
        miCopy.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ClipboardManager manager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip;
                if (null == mText)
                    clip = ClipData.newPlainText(CLIP_LABEL, mLink);
                else
                    clip = ClipData.newPlainText(CLIP_LABEL, String.format(
                            Constants.SHARE_FORMATTER_CLIPBOARD, mText, mLink));
                manager.setPrimaryClip(clip);
                Toast.makeText(mContext, mContext.getString(R.string.toast_text_copied),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        MenuItem miShare = menu.add(0, v.getId(), 0, mContext.getString(R.string.menu_context_share));
        miShare.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (isBestOrRandom) shareBestOrRandom();
                else shareOther();
                return false;
            }
        });
    }

    private void shareOther() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType(Constants.TEXT_PLAIN);
        if (null != mText)
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                    String.format(Constants.SHARE_FORMATTER_CLIPBOARD, mText, mLink));
        else
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mLink);
        mContext.startActivity(sharingIntent);
        if (null != mAuthor && !"".equals(mAuthor)) {
            Answers.getInstance().logShare(new ShareEvent().putContentType(TYPE_COMICS));
        } else Answers.getInstance().logShare(new ShareEvent().putContentType(TYPE_QUOTES));
    }

    private void shareBestOrRandom() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType(Constants.TEXT_PLAIN);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, getText());
        mContext.startActivity(sharingIntent);
    }

    @NonNull
    private String getText() {
        return Html.fromHtml(String.format(Constants.SHARE_FORMATTER, mText, mLink)).toString();
    }

    public void setText(String text) {
        mText = text;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String[] dataToShare = QuotesTableHelper.getTextToShare(mContext, id);
        if (null == dataToShare) return true;
        mText = Html.fromHtml(dataToShare[0]).toString();
        mLink = dataToShare[1];
        mAuthor = dataToShare[2];
        return false;
    }
}
