package com.elrain.bashim.webutil;

import android.test.suitebuilder.annotation.SmallTest;

import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.util.XmlParser;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by denys.husher on 29.12.2015.
 */
public class XmlWorkerTest extends TestCase {

    private MockWebServer mockWebServer;
    private MockResponse mockResponse;
    private static final String XML_ONE_QUOTE = "<rss xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:content=\"http://purl.org/rss/1.0/modules/content/\" version=\"2.0\">\n" +
            "<channel>\n<title>Bash.im</title>\n<link>http://bash.im/</link>\n<atom:link href=\"http://bash.im/rss/\" rel=\"self\" type=\"application/rss+xml\"/>\n" +
            "<description>tyut</description>\n<language>ru</language>\n<item>\n<guid isPermaLink=\"false\">\n66ce9244e9e1b4a06d8ccb01bf2dfc526174edbd371a3cca26e9b002fd99dc56\n" +
            "</guid>\n<link>http://bash.im/quote/437320</link>\n<title>quote #437320</title>\n<pubDate>Tue, 29 Dec 2015 12:13:01 +0400</pubDate>\n<description>\n" +
            "<![CDATA[\nMingan: text\n]]>\n</description>\n</item></channel>\n</rss>";
    private static final String XML_QUOTES = "<rss xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:content=\"http://purl.org/rss/1.0/modules/content/\" version=\"2.0\">\n" +
            "<channel>\n<title>Bash.im</title>\n<link>http://bash.im/</link>\n<atom:link href=\"http://bash.im/rss/\" rel=\"self\" type=\"application/rss+xml\"/>\n" +
            "<description>tyut</description>\n<language>ru</language>\n<item>\n<guid isPermaLink=\"false\">\n66ce9244e9e1b4a06d8ccb01bf2dfc526174edbd371a3cca26e9b002fd99dc56\n" +
            "</guid>\n<link>http://bash.im/quote/437321</link>\n<title>quote #437321</title>\n<pubDate>Tue, 29 Dec 2015 12:13:01 +0400</pubDate>\n<description>\n"+
            "<![CDATA[\nMingan: text1\n]]>\n</description>\n</item>\n<item>\n<guid isPermaLink=\"false\">\n66ce9244e9e1b4a06d8ccb01bf2dfc526174edbd371a3cca26e9b002fd99dc56\n" +
            "</guid>\n<link>http://bash.im/quote/437322</link>\n<title>quote #437322</title>\n<pubDate>Tue, 29 Dec 2015 12:13:01 +0400</pubDate>\n<description>\n" +
            "<![CDATA[\nMingan: text2\n]]>\n</description>\n</item>\n<item>\n<guid isPermaLink=\"false\">\n66ce9244e9e1b4a06d8ccb01bf2dfc526174edbd371a3cca26e9b002fd99dc56\n" +
            "</guid>\n<link>http://bash.im/quote/437323</link>\n<title>quote #437323</title>\n<pubDate>Tue, 29 Dec 2015 12:13:01 +0400</pubDate>\n<description>\n" +
            "<![CDATA[\nMingan: text3\n]]>\n</description>\n</item>\n</channel>\n</rss>";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mockWebServer = new MockWebServer();
        mockWebServer.play();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mockWebServer.shutdown();
    }

    @SmallTest
    public void testGetInputStream() throws Exception {
        mockResponse = new MockResponse().setBody(XML_ONE_QUOTE);
        mockWebServer.enqueue(mockResponse);
        InputStream is = XmlWorker.getStream(mockWebServer.getUrl("/"));
        List<BashItem> items = XmlParser.parseXml(is);
        assertEquals(1, items.size());
        for (BashItem bi : items)
            assertEquals("quote #437320", bi.getTitle());
    }

    @SmallTest
    public void testGetInputStreamSeveralQuotes() throws Exception{
        mockResponse = new MockResponse().setBody(XML_QUOTES);
        mockWebServer.enqueue(mockResponse);
        InputStream is = XmlWorker.getStream(mockWebServer.getUrl("/"));
        List<BashItem> items = XmlParser.parseXml(is);
        assertEquals(3, items.size());
    }

    @SmallTest
    public void testParser() throws IOException, SAXException, ParserConfigurationException {
        InputStream is = new ByteArrayInputStream(XML_ONE_QUOTE.getBytes());
        List<BashItem> items = XmlParser.parseXml(is);
        assertEquals(1, items.size());
    }

}
