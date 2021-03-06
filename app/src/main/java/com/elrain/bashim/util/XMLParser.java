package com.elrain.bashim.util;

import android.support.annotation.NonNull;

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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public final class XmlParser {

    private static final String TAG_ITEM = "item";
    private static final String TAG_GUID = "guid";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_PUB_DATE = "pubDate";
    private static final String TAG_LINK = "link";
    private static final String TAG_AUTHOR = "author";
    private static final String ENCODING = "windows-1251";
    private static final String HTTP = "http";
    private static final String DESCRIPTION_CONTAINS = "<img src=\"";
    private static List<BashItem> mItems;

    /**
     * Launch XML parse process of <code>InputStream</code> object
     *
     * @param in <code>InputStream</code> object with XML
     * @return list of parsed items
     * @throws ParserConfigurationException
     * @throws SAXException                 Any SAX exception, possibly wrapping another exception.
     * @throws IOException
     */
    public static List<BashItem> parseXml(InputStream in) throws ParserConfigurationException, SAXException, IOException {
        mItems = new ArrayList<>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(getInputSource(in), new Parser());
        return mItems;
    }

    @NonNull
    private static InputSource getInputSource(InputStream in) throws UnsupportedEncodingException {
        Reader r = new InputStreamReader(in, ENCODING);
        InputSource is = new InputSource(r);
        is.setEncoding(ENCODING);
        return is;
    }

    private static class Parser extends DefaultHandler {
        private BashItem bashItem;
        private boolean isItemOpen = false;
        private boolean isDescriptionOpen = false;
        private boolean isGuidOpen = false;
        private boolean isTitleOpen = false;
        private boolean isPubDateOpen = false;
        private boolean isLinkOpen = false;
        private boolean isAuthorOpen = false;
        private StringBuilder mStringBuilder;

        public Parser() {
            mStringBuilder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (TAG_ITEM.equals(qName)) {
                bashItem = new BashItem();
                bashItem.setIsFavorite(false);
                isItemOpen = true;
            } else if (isItemOpen && TAG_DESCRIPTION.equals(qName)) isDescriptionOpen = true;
            else if (isItemOpen && TAG_GUID.equals(qName)) isGuidOpen = true;
            else if (isItemOpen && TAG_LINK.equals(qName)) isLinkOpen = true;
            else if (isItemOpen && TAG_PUB_DATE.equals(qName)) isPubDateOpen = true;
            else if (isItemOpen && TAG_TITLE.equals(qName)) isTitleOpen = true;
            else if (isItemOpen && TAG_AUTHOR.equals(qName)) isAuthorOpen = true;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (isItemOpen) {
                if (TAG_GUID.equals(qName)) isGuidOpen = false;
                else if (TAG_LINK.equals(qName)) {
                    bashItem.setLink(mStringBuilder.toString());
                    isLinkOpen = false;
                } else if (TAG_DESCRIPTION.equals(qName)) {
                    String description = mStringBuilder.toString();
                    if (description.contains(DESCRIPTION_CONTAINS))
                        bashItem.setDescription(description.substring(description.indexOf(HTTP), description.length() - 2));
                    else bashItem.setDescription(mStringBuilder.toString());
                    isDescriptionOpen = false;
                } else if (TAG_TITLE.equals(qName)) {
                    bashItem.setTitle(mStringBuilder.toString());
                    isTitleOpen = false;
                } else if (TAG_PUB_DATE.equals(qName)) {
                    bashItem.setPubDate(DateUtil.parseDateFromXml(mStringBuilder.toString()));
                    isPubDateOpen = false;
                } else if (TAG_AUTHOR.equals(qName)) {
                    bashItem.setAuthor(mStringBuilder.toString());
                    isAuthorOpen = false;
                } else {
                    mItems.add(bashItem);
                    isItemOpen = false;
                }
                mStringBuilder = new StringBuilder();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            if (isItemOpen && (isGuidOpen || isTitleOpen || isLinkOpen || isPubDateOpen
                    || isDescriptionOpen || isAuthorOpen))
                for (int index = start; index < start + length; ++index)
                    mStringBuilder.append(ch[index]);
        }
    }


}
