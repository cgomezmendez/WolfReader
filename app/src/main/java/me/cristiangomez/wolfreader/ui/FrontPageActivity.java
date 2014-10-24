package me.cristiangomez.wolfreader.ui;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.cristiangomez.wolfreader.DownloadFeedTask;
import me.cristiangomez.wolfreader.R;
import me.cristiangomez.wolfreader.VolleySingleton;
import me.cristiangomez.wolfreader.model.News;


public class FrontPageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VolleySingleton.getInstance(getApplicationContext());
        setContentView(R.layout.activity_front_page);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FrontPageFragment())
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
    public static class FrontPageFragment extends Fragment implements DownloadFeedTask.OnDownloadListener, SwipeRefreshLayout.OnRefreshListener {
        private static final String LOG_TAG = FrontPageFragment.class.getSimpleName();
        private static List<News> mNewsList;
        @InjectView(R.id.news_recycler_view)
        RecyclerView mNewsRecycler;
        @InjectView(R.id.swipe_container)
        SwipeRefreshLayout swipeRefreshLayout;
        private NewsAdapter mNewsAdapter;
        private ProgressDialog mProgressDialog;
        private DownloadFeedTask mDownloaderTask;

        public FrontPageFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_front_page, container, false);
            ButterKnife.inject(this, rootView);
            if (mNewsList != null) {
                mNewsRecycler.setAdapter(new NewsAdapter(mNewsList));
            }
            RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            mNewsRecycler.setLayoutManager(layoutManager);
            if (savedInstanceState == null) {
                mNewsRecycler.setLayoutManager(layoutManager);
                mNewsRecycler.setAdapter(new NewsAdapter(new ArrayList<News>()));
                mNewsRecycler.setHasFixedSize(true);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                }, 500);
                poblateList();
            }
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
            return rootView;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onDetach() {
            super.onDetach();
            if (mDownloaderTask != null) {
                mDownloaderTask.removeListener(this);
            }
        }

        @Override
        public void onDownloadFinished(List<News> newsList) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            mNewsList = newsList;
            mNewsAdapter = new NewsAdapter(mNewsList);
            RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            mNewsRecycler.setLayoutManager(layoutManager);
            mNewsRecycler.setAdapter(mNewsAdapter);
            mNewsRecycler.setHasFixedSize(true);
            mNewsRecycler.getAdapter().notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            mDownloaderTask = new DownloadFeedTask();
        }

        @Override
        public void onRefresh() {
            poblateList();
        }

        public void poblateList() {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("www.meneame.net")
                    .appendPath("feedburner-rss2.php")
                    .appendQueryParameter("rows", "20");
            Uri uri = builder.build();
            mDownloaderTask = new DownloadFeedTask();
            mDownloaderTask.addOnDownloadListener(this);
            //mProgressDialog = ProgressDialog.show(this.getActivity(), "Descargando Noticias", "Descargando noticias, por favor espere...", true, false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mDownloaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uri.toString());
            } else {
                mDownloaderTask.execute(uri.toString());
            }
        }
    }
}
