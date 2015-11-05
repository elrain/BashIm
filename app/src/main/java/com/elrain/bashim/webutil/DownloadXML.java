package com.elrain.bashim.webutil;

import android.content.Context;

import com.elrain.bashim.BuildConfig;
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
    public static void downloadFile(Context context) {
        try {
            URL url = new URL(BuildConfig.RSS_URL);
            URLConnection urlConn = url.openConnection();
            InputStream is = urlConn.getInputStream();
            XMLParser xmlParser = new XMLParser(context);
            xmlParser.parseXml(is);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}
