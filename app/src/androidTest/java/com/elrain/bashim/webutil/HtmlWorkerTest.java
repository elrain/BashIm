package com.elrain.bashim.webutil;

import android.test.InstrumentationTestCase;

import com.elrain.bashim.object.BashItem;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by denys.husher on 13.01.2016.
 */
public class HtmlWorkerTest extends InstrumentationTestCase {
    private CountDownLatch mCountDownLatch;
    private MockWebServer mMockWebServer;
    private static final String HTML_QUOTES = "<html id=\"godtier\">\n<head>\n\t<title>Подборка случайных цитат — Цитатник Рунета</title>\n\n\t\n\t<meta charset=\"windows-1251\" />\n" +
            "\n\t<link href=\"http://s.bash.im/reset.css\" type=\"text/css\" rel=\"stylesheet\" />\n\t<meta name=\"format-detection\" content=\"telephone=no\">\n" +
            "\t\t\t\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=yes\" />\n\t<link href=\"http://s.bash.im/bor25.css?20140303\" type=\"text/css\" rel=\"stylesheet\" />\n" +
            "\t\t\n\t\t<link rel=\"alternate\" title=\"Цитатник Рунета - bash.im\" href=\"http://bash.im/rss/\" type=\"application/rss+xml\">\n\t</head>\n<body>\n\n\n" +
            "<div id=\"xsite-nav\">\n\t<a href=\"/\" class=\"current\"><img src=\"http://s.bash.im/img/icon-bor.gif\" width=\"16\" height=\"16\" />bash.im</a>\n" +
            "\t<a href=\"http://ithappens.me/\"><img src=\"http://s.bash.im/img/icon-ith.gif\" width=\"16\" height=\"16\" />ithappens.me</a>\n" +
            "\t<a href=\"http://zadolba.li/\"><img src=\"http://s.bash.im/img/icon-zdb.gif\" width=\"16\" height=\"16\" />zadolba.li</a>\n" +
            "</div><img src=\"http://o.hai.lolwut.it/\" width=\"0\" height=\"0\" style=\"visibility: hidden; position: absolute; top: 0; left: 0;\" />\n\n\n<div id=\"body\">\n\n" +
            "\t<div id=\"header\">\n" +
            "\t\t<a href=\"/\"><img src=\"http://s.bash.im/logo.gif\" data-src2x=\"http://s.bash.im/logo@2x.gif\" width=\"300\" height=\"40\" alt=\"bash.im\" /></a>\t\t<h1>bash.im&nbsp;— Цитатник&nbsp;Рунета</h1>\n" +
            "\t</div>\n\n\t\n\t<div id=\"menubar\" class=\"inside\">\n\t<div class=\"submenu\">\n\t<span class=\"title\">Цитаты:</span>\n\t<div class=\"options\">\n" +
            "\t\t<span class=\"nowrap\"><a href=\"/\" id=\"menu-index\">новые</a>\n\t\t<a href=\"/random\" class=\"current\">случайные</a>\n\t\t<a href=\"/best\">лучшие</a>\n" +
            "\t\t<a href=\"/byrating\">по рейтингу</a></span>\n\t\t<span class=\"nowrap\"><a href=\"/abyss\">Бездна</a>\n\t\t<a href=\"/abysstop\">топ Бездны</a>\n" +
            "\t\t<a href=\"/abyssbest\" id=\"menu-abyssbest\">лучшее Бездны</a></span>\n\t\t<a href=\"/add\" class=\"add last\">добавить</a>\n\t\t\t</div>\n\t</div>\n\t\n\t\n" +
            "\t<div class=\"submenu\">\n\t<span class=\"title\">Прочее:</span>\n\t<div class=\"options nowrap\">\n\t\t<a href=\"/comics\" id=\"menu-comics\">комиксы</a>\n" +
            "\t\t<a href=\"/faq\">о сайте</a>\n\t\t<a href=\"/webmaster\">вебмастеру</a>\t\t<a href=\"/rss\">RSS</a>\n\t\t<a href=\"http://twitter.com/b_o_r\" class=\"last\">Twitter</a>\n" +
            "\t\t\t</div>\n</div>\n\t\n\t\n</div>\n\t\t\n\n\t\t<div class=\"quote\">\n\t<div class=\"actions\">\n" +
            "\t\t\t\t\t\t\t\t<a href=\"/quote/235713/rulez\" class=\"up\" rel=\"nofollow\" onclick=\"v('235713',0,0); return false;\">+</a>\t\t<span class=\"rating-o\"><span id=\"v235713\" class=\"rating\">2709</span></span>\t\t<a href=\"/quote/235713/sux\" class=\"down\" rel=\"nofollow\" onclick=\"v('235713',1,0); return false;\">&ndash;</a>\n" +
            "\t\t<a href=\"/quote/235713/bayan\" class=\"old\" id=\"vb235713\" rel=\"nofollow\" onclick=\"v('235713',2,0); return false;\">[:||||:]</a>\t\t<span class=\"share\" id=\"s235713\"><span class=\"ph\">Поделиться</span><script>new Ya.share({ elementStyle: y1, popupStyle: y2, element: 's235713', link: 'http://bash.im/quote/235713', title: 'Цитата #235713' });</script></span>\t\t\t\t\t\t\t\t\t\t<span class=\"date\">2007-05-22 18:28</span> <a href=\"/quote/235713\" class=\"id\">#235713</a>\t\t\t\t\t</div>\n" +
            "\t<div class=\"text\">ххх: только что мой котяра нашипел на отца и поцарапал...<br />ххх: отец его закрыл в ванной, подперев стульями дверь<br />ххх: подумалось: забанили за оскорбление админа...</div>\n" +
            "\t</div>\t \n\t \n\t<div class=\"quote\">\n\t<div class=\"actions\">\n\t\t\t\t\t\t\t\t<a href=\"/quote/104726/rulez\" class=\"up\" rel=\"nofollow\" onclick=\"v('104726',0,0); return false;\">+</a>\t\t<span class=\"rating-o\"><span id=\"v104726\" class=\"rating\">17250</span></span>\t\t<a href=\"/quote/104726/sux\" class=\"down\" rel=\"nofollow\" onclick=\"v('104726',1,0); return false;\">&ndash;</a>\n" +
            "\t\t<a href=\"/quote/104726/bayan\" class=\"old\" id=\"vb104726\" rel=\"nofollow\" onclick=\"v('104726',2,0); return false;\">[:||||:]</a>\t\t<span class=\"share\" id=\"s104726\"><span class=\"ph\">Поделиться</span><script>new Ya.share({ elementStyle: y1, popupStyle: y2, element: 's104726', link: 'http://bash.im/quote/104726', title: 'Цитата #104726' });</script></span>\t\t\t\t\t\t\t\t\t\t<span class=\"date\">2007-02-09 01:10</span> <a href=\"/quote/104726\" class=\"id\">#104726</a>\t\t\t\t\t</div>\n" +
            "\t<div class=\"text\">DreamMaker: Да..конечно лекция по физике у нашего препода довольно увлекательное и серьезное мероприятие.....Но когда перед тобой на парте красуется надпись : &quot;ЙА КРИВЕТКО!&quot;......</div>\n" +
            "\t</div>\t \n\t \n\t<div class=\"quote\">\n\t<div class=\"actions\">\n" +
            "\t\t\t\t\t\t\t\t<a href=\"/quote/421045/rulez\" class=\"up\" rel=\"nofollow\" onclick=\"v('421045',0,0); return false;\">+</a>\t\t<span class=\"rating-o\"><span id=\"v421045\" class=\"rating\">2517</span></span>\t\t<a href=\"/quote/421045/sux\" class=\"down\" rel=\"nofollow\" onclick=\"v('421045',1,0); return false;\">&ndash;</a>\n" +
            "\t\t<a href=\"/quote/421045/bayan\" class=\"old\" id=\"vb421045\" rel=\"nofollow\" onclick=\"v('421045',2,0); return false;\">[:||||:]</a>\t\t<span class=\"share\" id=\"s421045\"><span class=\"ph\">Поделиться</span><script>new Ya.share({ elementStyle: y1, popupStyle: y2, element: 's421045', link: 'http://bash.im/quote/421045', title: 'Цитата #421045' });</script></span>\t\t\t\t\t\t\t\t\t\t<span class=\"date\">2013-02-04 11:45</span> <a href=\"/quote/421045\" class=\"id\">#421045</a>\t\t\t\t\t</div>\n" +
            "\t<div class=\"text\">Смотрим фотографии выставочных мейнкунов (кошачья порода размера кинг-сайз). Хозяева, демонстрируя котиков, держат их на двух руках и имеют при этом весьма напряженные лица с кривыми улыбками. Подходит товарищ.<br>товарищ: А чегой-то они их не держат в выставочной стойке на одной руке?<br>я: Да им еще домкрат от Белаза подкатить не успели.</div>\n" +
            "\t</div>\n</body>\n</html>\t";
    private HtmlWorker.OnHtmlParsed listener;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mMockWebServer = new MockWebServer();
        mMockWebServer.play();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mMockWebServer.shutdown();
    }

    public void testGetQuotes() throws InterruptedException {
        mCountDownLatch = new CountDownLatch(1);
        MockResponse mockResponse = new MockResponse().setBody(HTML_QUOTES);
        mMockWebServer.enqueue(mockResponse);
        listener = new HtmlWorker.OnHtmlParsed() {
            @Override
            public void returnResult(List<BashItem> quotes) {
                assertEquals(3, quotes.size());
                mCountDownLatch.countDown();
            }
        };
        HtmlWorker.getQuotes(listener, mMockWebServer.getUrl("/").toString());
        mCountDownLatch.await();
    }
}
