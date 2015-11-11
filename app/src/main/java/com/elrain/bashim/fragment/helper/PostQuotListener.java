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

    private Context mContext;
    private String quotFormat = "%s <br/> %s";

    public PostQuotListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType(Constants.TEXT_PLAIN);
        String[] textToShare = QuotesTableHelper.getTextToShare(mContext, id);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(String.format(quotFormat, textToShare[0], textToShare[1])).toString());
        mContext.startActivity(sharingIntent);
        Answers.getInstance().logShare(new ShareEvent());
        return true;
    }
}
