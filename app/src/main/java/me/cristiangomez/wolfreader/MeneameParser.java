package me.cristiangomez.wolfreader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import me.cristiangomez.wolfreader.model.News;

/**
 * Created by Cristian on 10/21/2014.
 */
public class MeneameParser {
    private static final String ns = null;
    private static XmlPullParserFactory pullParserFactory;
    private static final String LOG_TAG = MeneameParser.class.getSimpleName();
    private static final String NAMESPACE = null;

    public List<News> parse(String string) throws XmlPullParserException, IOException, ParseException {
        if (pullParserFactory==null) {
            pullParserFactory = XmlPullParserFactory.newInstance();
        }
        List<News> newsList = new ArrayList<News>();
        XmlPullParser parser = pullParserFactory.newPullParser();
        InputStream stream = new ByteArrayInputStream(string.getBytes("UTF8"));
        parser.setInput(stream,"UTF-8");
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        int eventType = parser.getEventType();
        boolean finish = false;
        int counter = 0;
        while (eventType!=XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("item")) {
                    newsList.add(parseNews(parser));
                }
            }
            eventType = parser.next();
            if (eventType==XmlPullParser.END_TAG ) {
                if (parser.getName()!=null){
                    if (parser.getName().equals("channel")){
                        finish = true;
                    }
                }
            }
        }

        return newsList;
    }

    private News parseNews(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException {
        News news = new News();
        parser.require(XmlPullParser.START_TAG,NAMESPACE,"item");
        int eventType = parser.getEventType();
        boolean finish = false;
        while (!finish) {
            if (eventType==XmlPullParser.START_TAG) {
                if (parser.getName()!=null) {
                    if (parser.getName().equals("link_id")){
                        news.setLinkId(parseLong(parser));
                    }
                    else if (parser.getName().equals("sub")) {
                        news.setSub(parseString(parser));
                    }
                    else if (parser.getName().equals("user")) {
                        news.setAuthor(parseString(parser));
                    }
                    else if (parser.getName().equals("clicks")) {
                        news.setClicks(parseInt(parser));
                    }
                    else if (parser.getName().equals("votes")) {
                        news.setUpVotes(parseInt(parser));
                    }
                    else if (parser.getName().equals("negatives")) {
                        news.setNegatives(parseInt(parser));
                    }
                    else if (parser.getName().equals("karma")) {
                        news.setKarma(parseInt(parser));
                    }
                    else if(parser.getName().equals("description")) {
                        news.setDescription(parseString(parser));
                    }
                    else if (parser.getName().equals("title")) {
                        news.setTitle(parseString(parser));
                    }
                    else if (parser.getName().equals("url")) {
                        news.setLink(parseString(parser));
                    }
                    else if (parser.getName().equals("thumbnail")) {
                        news.setImageUrl(parseThumbnail(parser));
                    }
                    else if (parser.getName().equals("pubDate")) {
                        news.setPublicactionDate(parseString(parser));
                    }

                }
            }
            parser.next();
            eventType = parser.getEventType();
            if (parser.getName()!=null){
                if (parser.getName().equals("item")) {
                    finish = true;
                    parser.next();
                }
            }
        }
        return news;
    }

    private long parseLong(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.next();
        long number = Long.parseLong(parser.getText());
        return number;
    }

    private String parseString(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.next();
        String text = parser.getText();
        return text;
    }

    private int parseInt(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.next();
        int number = Integer.parseInt(parser.getText());
        return number;
    }

    private String parseThumbnail(XmlPullParser parser) {
        return parser.getAttributeValue(0);
    }


}
