package com.elrain.bashim.webutil;

import android.content.Context;

import com.elrain.bashim.BuildConfig;
import com.elrain.bashim.util.Constants;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by denys.husher on 03.11.2015.
 */
public final class XmlWorker {

    private static final int TIMEOUT_MILLIS = 30 * 1000;

    /**
     * Creates a new <code>URL</code> instance by parsing specific url. Open a new connection to the
     * resource referred to by this url. Get an <code>InputStream</code> for reading data from the
     * resource pointed by this URLConnection and start parsing this stream.
     *
     * @param context application context
     */
    public static void getStreamAndParse(Context context) {
        try {
            URL[] url = new URL[]{new URL(BuildConfig.RSS_URL), new URL(Constants.COMMICS_RSS_URL)};
            for (URL link : url) {
                URLConnection urlConn = link.openConnection();
                urlConn.setConnectTimeout(TIMEOUT_MILLIS);
                InputStream is = urlConn.getInputStream();
                XmlParser xmlParser = new XmlParser(context);
                xmlParser.parseXml(is);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}
