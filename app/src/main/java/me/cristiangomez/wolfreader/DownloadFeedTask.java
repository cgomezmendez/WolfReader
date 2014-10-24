package me.cristiangomez.wolfreader;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import me.cristiangomez.wolfreader.model.News;

/**
 * Created by Cristian on 10/24/2014.
 */
public class DownloadFeedTask extends AsyncTask<String, Void, List<News>> {
    private List<OnDownloadListener> mListeners;

    public DownloadFeedTask() {
        mListeners = new ArrayList<OnDownloadListener>();
    }

    public void addOnDownloadListener(OnDownloadListener listener) {
        mListeners.add(listener);
    }

    public void notifyListeners(List<News> newsList) {
        for (int i = 0; i < mListeners.size(); i++) {
            mListeners.get(i).onDownloadFinished(newsList);
        }
    }

    public void removeListener(OnDownloadListener listener) {
        mListeners.remove(listener);
    }

    @Override
    protected List<News> doInBackground(String... urls) {
        List<News> newsList = null;
        try {
            newsList = loadXmlFromNetwork(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return newsList;
    }

    private List<News> loadXmlFromNetwork(String url) throws IOException, ParseException, XmlPullParserException {
        InputStream inputStream = null;
        List<News> newsList = null;
        try {
            inputStream = downloadUrl(url);
            MeneameParser parser = new MeneameParser();
            newsList = parser.parse(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return newsList;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(20000);
        conn.setConnectTimeout(25000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }

    @Override
    protected void onPostExecute(List<News> newsList) {
        super.onPostExecute(newsList);
        notifyListeners(newsList);
    }

    public static interface OnDownloadListener {
        public void onDownloadFinished(List<News> newsList);
    }
}
