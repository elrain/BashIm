package com.elrain.bashim.webutil;

import android.os.AsyncTask;
import android.text.Html;

import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.util.DateUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by denys.husher on 23.11.2015.
 */
public final class HtmlWorker {

    private static final String HREF = "href";
    private static final String HTTP_BASH_IM = "http://bash.im";
    private static final String DIV_QUOTE = "div.quote";
    private static final String DIV_ACTIONS = "div.actions";
    private static final String SPAN_DATE = "span.date";
    private static final String A_ID = "a.id";
    private static final String DIV_TEXT = "div.text";
    private static final String QUOTE = "Цитата ";
    private static OnHtmlParsed mListener;

    public static void getRandomQuotes(OnHtmlParsed listener, String url) {
        mListener = listener;
        new GetAndParseHtml().execute(url);
    }

    public interface OnHtmlParsed {
        void returnResult(ArrayList<BashItem> quotes);
    }

    private static class GetAndParseHtml extends AsyncTask<String, Void, ArrayList<BashItem>> {
        @Override
        protected ArrayList<BashItem> doInBackground(String... params) {
            ArrayList<BashItem> quotes = new ArrayList<>();
            Document document;
            try {
                document = Jsoup.connect(params[0]).get();
                Elements quotesElements = document.select(DIV_QUOTE);
                for (Element quote : quotesElements) {
                    Element newQuote = document.select(DIV_QUOTE).get(quotesElements.indexOf(quote));
                    Elements action = Jsoup.parse(String.valueOf(newQuote)).select(DIV_ACTIONS);
                    String date = action.select(SPAN_DATE).text();
                    if ("".equals(date))
                        continue;
                    BashItem item = new BashItem();
                    item.setDescription(Html.fromHtml(Jsoup.parse(String.valueOf(newQuote)).select(DIV_TEXT).html()).toString());
                    item.setTitle(QUOTE + action.select(A_ID).text());
                    item.setLink(HTTP_BASH_IM + action.select(A_ID).attr(HREF));
                    item.setPubDate(DateUtil.parseDateFromXml(date));
                    quotes.add(item);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return quotes;
        }

        @Override
        protected void onPostExecute(ArrayList<BashItem> bashItems) {
            super.onPostExecute(bashItems);
            mListener.returnResult(bashItems);
        }
    }
}
