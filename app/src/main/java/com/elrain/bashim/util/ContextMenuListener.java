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
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;
import com.elrain.bashim.R;
import com.elrain.bashim.activity.ImagePagerActivity;
import com.elrain.bashim.activity.ImageScaleActivity;

/**
 * Created by denys.husher on 08.12.2015.
 */
public class ContextMenuListener implements View.OnCreateContextMenuListener, View.OnClickListener {

    private static final String TYPE_COMICS = "comics";
    private static final String TYPE_QUOTES = "quotes";
    private static final String CLIP_LABEL = "label";

    private final Context mContext;
    private final boolean isBestOrRandom;
    private String mText;
    private String mLink;
    private String mAuthor;
    private boolean isGalleryNeeded;
    private long mId;

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
        miCopy.setOnMenuItemClickListener(item -> {
            ClipboardManager manager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip;
            if (null == mText) clip = ClipData.newPlainText(CLIP_LABEL, mLink);
            else clip = ClipData.newPlainText(CLIP_LABEL, String.format(
                        Constants.SHARE_FORMATTER_CLIPBOARD, mText, mLink));
            manager.setPrimaryClip(clip);
            Toast.makeText(mContext, mContext.getString(R.string.toast_text_copied),
                    Toast.LENGTH_SHORT).show();
            return true;
        });
        MenuItem miShare = menu.add(0, v.getId(), 0, mContext.getString(R.string.menu_context_share));
        miShare.setOnMenuItemClickListener(item -> {
            if (isBestOrRandom) shareBestOrRandom();
            else shareOther();
            return false;
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

    public void setTextAndAuthor(String text, String author) {
        if(null == author) mText = text;
        else mLink = text;
        mAuthor = author;
    }

    public void addClickListener(View v, long id, boolean isGalleryNeeded) {
        this.isGalleryNeeded = isGalleryNeeded;
        mId = id;
        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isGalleryNeeded) {
            Intent intent = new Intent(mContext, ImagePagerActivity.class);
            intent.putExtra(Constants.KEY_INTENT_IMAGE_ID, mId);
            mContext.startActivity(intent);
        } else {
            Intent intent = new Intent(mContext, ImageScaleActivity.class);
            intent.putExtra(Constants.KEY_INTENT_IMAGE_URL, mText);
            mContext.startActivity(intent);
        }
    }
}
