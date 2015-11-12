package com.elrain.bashim.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.object.BashItem;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by denys.husher on 03.11.2015.
 */
public class XMLParser extends DefaultHandler {

    private static final String TAG_ITEM = "item";
    private static final String TAG_GUID = "guid";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_PUB_DATE = "pubDate";
    private static final String TAG_LINK = "link";
    private static final String TAG_AUTHOR = "author";
    private static final String ENCODING = "windows-1251";
    public static final String HTTP = "http";
    private BashItem bashItem;
    private boolean isItemOpen = false;
    private boolean isDescriptionOpen = false;
    private boolean isGuidOpen = false;
    private boolean isTitleOpen = false;
    private boolean isPubDateOpen = false;
    private boolean isLinkOpen = false;
    private boolean isAuthorOpen = false;
    private final Context mContext;
    private StringBuilder mStringBuilder;
    private Constants.Rss mRssType;

    public XMLParser(Context mContext, Constants.Rss rssType) {
        this.mContext = mContext;
        this.mRssType = rssType;
        mStringBuilder = new StringBuilder();
    }

    public void parseXml(InputStream in) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(getInputSource(in), this);
    }

    @NonNull
    private InputSource getInputSource(InputStream in) throws UnsupportedEncodingException {
        Reader r = new InputStreamReader(in, ENCODING);
        InputSource is = new InputSource(r);
        is.setEncoding(ENCODING);
        return is;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (TAG_ITEM.equals(qName)) {
            bashItem = new BashItem();
            isItemOpen = true;
        } else if (isItemOpen && TAG_DESCRIPTION.equals(qName)) {
            isDescriptionOpen = true;
        } else if (isItemOpen && TAG_GUID.equals(qName)) isGuidOpen = true;
        else if (isItemOpen && TAG_LINK.equals(qName)) isLinkOpen = true;
        else if (isItemOpen && TAG_PUB_DATE.equals(qName)) isPubDateOpen = true;
        else if (isItemOpen && TAG_TITLE.equals(qName)) isTitleOpen = true;
        else if (isItemOpen && TAG_AUTHOR.equals(qName)) isAuthorOpen = true;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (isItemOpen) {
            if (TAG_GUID.equals(qName)) {
                bashItem.setGuid(mStringBuilder.toString());
                isGuidOpen = false;
                mStringBuilder = new StringBuilder();
            } else if (TAG_LINK.equals(qName)) {
                bashItem.setLink(mStringBuilder.toString());
                isLinkOpen = false;
                mStringBuilder = new StringBuilder();
            } else if (TAG_DESCRIPTION.equals(qName)) {
                if (Constants.Rss.COMMICS == mRssType) {
                    String description = mStringBuilder.toString();
                    bashItem.setDescription(description.substring(description.indexOf(HTTP), description.length() - 2));
                } else bashItem.setDescription(mStringBuilder.toString());
                isDescriptionOpen = false;
                mStringBuilder = new StringBuilder();
            } else if (TAG_TITLE.equals(qName)) {
                bashItem.setTitle(mStringBuilder.toString());
                isTitleOpen = false;
                mStringBuilder = new StringBuilder();
            } else if (TAG_PUB_DATE.equals(qName)) {
                bashItem.setPubDate(DateUtil.parseDateFromXml(mStringBuilder.toString()));
                isPubDateOpen = false;
                mStringBuilder = new StringBuilder();
            } else if (TAG_AUTHOR.equals(qName)) {
                bashItem.setAuthor(mStringBuilder.toString());
                isAuthorOpen = false;
                mStringBuilder = new StringBuilder();
            } else QuotesTableHelper.inputQuot(mContext, bashItem);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (isItemOpen)
            if (isGuidOpen || isTitleOpen || isLinkOpen || isPubDateOpen || isDescriptionOpen
                    || isAuthorOpen)
                for (int index = start; index < start + length; ++index)
                    mStringBuilder.append(ch[index]);
    }
}
