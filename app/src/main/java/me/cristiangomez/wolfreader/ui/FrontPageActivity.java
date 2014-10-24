package me.cristiangomez.wolfreader.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.cristiangomez.wolfreader.MeneameParser;
import me.cristiangomez.wolfreader.R;
import me.cristiangomez.wolfreader.VolleySingleton;
import me.cristiangomez.wolfreader.model.News;


public class FrontPageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        getSupportActionBar().setSubtitle("Portada");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.front_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements Response.Listener<String>, Response.ErrorListener {
        private static final String LOG_TAG = PlaceholderFragment.class.getSimpleName();
        private static List<News> newsList;
        @InjectView(R.id.news_recycler_view)
        RecyclerView mNewsRecycler;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_front_page, container, false);
            ButterKnife.inject(this, rootView);
            VolleySingleton.getInstance(getActivity());
            if (savedInstanceState == null) {
                RequestQueue queue = VolleySingleton.getRequestQueue();
                String url = "https://www.meneame.net/feedburner-rss2.php?rows=20";
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("www.meneame.net")
                        .appendPath("feedburner-rss2.php")
                        .appendQueryParameter("rows", "20");
                Uri uri = builder.build();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                        (Response.Listener) this, this) {
                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String parsed;
                        parsed = new String(response.data, Charset.forName("UTF-8"));
                        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("User-agent", "FeedBurner/1.0 (http://www.FeedBurner.com");
                        return super.getHeaders();
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                stringRequest.setCacheEntry(new Cache.Entry());
                queue.add(stringRequest);
                queue.start();
            }
            if (newsList != null) {
                News[] newsArray = new News[newsList.size()];
                newsList.toArray(newsArray);
                NewsAdapter adapter = new NewsAdapter(newsArray);
                mNewsRecycler.setAdapter(adapter);
                mNewsRecycler.setItemAnimator(new DefaultItemAnimator());
            }
            RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            mNewsRecycler.setLayoutManager(layoutManager);
            return rootView;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Log.e("feedrequest", volleyError.getMessage());

        }

        @Override
        public void onResponse(String s) {
            MeneameParser parser = new MeneameParser();
            try {
                newsList = parser.parse(s);
                if (newsList != null) {
                    News[] newsArray = new News[newsList.size()];
                    newsList.toArray(newsArray);
                    NewsAdapter newsAdapter = new NewsAdapter(newsArray);
                    newsAdapter.notifyDataSetChanged();
                    mNewsRecycler.setAdapter(newsAdapter);
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
