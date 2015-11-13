package com.elrain.bashim.util;

/**
 * Created by denys.husher on 04.11.2015.
 */
public class Constants {

    public static final String INTENT_DOWNLOAD = "come.elrain.bashim.download";
    public static final int ID_NOTIFICATION = 2203;
    public static final String KEY_SEARCH_STRING = "searchString";
    public static final String TEXT_PLAIN = "text/plain";
    public static final int ID_LOADER = 2204;
    public static final String KEY_OPEN_MAIN_ACTIVITY = "openMain";
    public static final String KEY_INTENT_IMAGE_URL = "imageUrl";

    public static final String COMMICS_RSS_URL = "http://bash.im/rss/comics.xml";

    /**
     * Have types of RSS lines which could be downloaded and parsed
     * <ul>
     *     <li>Rss#COMMICS - http://bash.im/rss/comics.xml</li>
     *     <li>Rss#QUOTES - http://bash.im/rss/</li>
     * </ul>
     */
    public enum Rss {
        COMMICS, QUOTES
    }
}
