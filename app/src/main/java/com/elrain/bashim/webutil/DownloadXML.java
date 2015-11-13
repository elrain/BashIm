package com.elrain.bashim.webutil;

import android.content.Context;

import com.elrain.bashim.BuildConfig;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.XMLParser;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by denys.husher on 03.11.2015.
 */
public class DownloadXML {
    /**
     * Creates a new <code>URL</code> instance by parsing specific url. Open a new connection to the
     * resource referred to by this url. Get an <code>InputStream</code> for reading data from the
     * resource pointed by this URLConnection and start parsing this stream.
     * @param context application context
     * @param rssType type what kind of rss is need to be parsed.
     * @see com.elrain.bashim.util.Constants.Rss
     */
    public static void getStreamAndParse(Context context, Constants.Rss rssType) {
        try {
            URL url;
            if (rssType == Constants.Rss.QUOTES) url = new URL(BuildConfig.RSS_URL);
            else url = new URL(Constants.COMMICS_RSS_URL);
            URLConnection urlConn = url.openConnection();
            InputStream is = urlConn.getInputStream();
            XMLParser xmlParser = new XMLParser(context, rssType);
            xmlParser.parseXml(is);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}
