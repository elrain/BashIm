package com.elrain.bashim.fragment.helper;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.util.Constants;

/**
 * Created by denys.husher on 11.11.2015.
 */
public class PostQuotListener implements AdapterView.OnItemLongClickListener {

    private static final String TYPE_COMICS = "comics";
    private static final String TYPE_QUOTES = "quotes";
    private final Context mContext;
    private static final String SHARE_FORMATTER = "%s <br/> %s";

    public PostQuotListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType(Constants.TEXT_PLAIN);
        String[] textToShare = QuotesTableHelper.getTextToShare(mContext, id);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(String.format(SHARE_FORMATTER, textToShare[0], textToShare[1])).toString());
        mContext.startActivity(sharingIntent);
        if (null != textToShare[2] && !"".equals(textToShare[2])){
            Answers.getInstance().logShare(new ShareEvent().putContentType(TYPE_COMICS));}
        else Answers.getInstance().logShare(new ShareEvent().putContentType(TYPE_QUOTES));
        return true;
    }
}
