package com.elrain.bashim.webutil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public final class XmlWorker {

    private static final int TIMEOUT_MILLIS = 30 * 1000;

    /**
     * Creates a new <code>URL</code> instance by parsing specific url. Open a new connection to the
     * resource referred to by this url. Get an <code>InputStream</code> for reading data from the
     * resource pointed by this URLConnection.
     *
     * @param url application context
     * @return source of bytes
     */
    public static InputStream getStream(URL url) {
        try {
            URLConnection urlConn = url.openConnection();
            urlConn.setConnectTimeout(TIMEOUT_MILLIS);
            return urlConn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
