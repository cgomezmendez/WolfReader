package me.cristiangomez.wolfreader.ui;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
    public static class PlaceholderFragment extends Fragment implements DownloadFeedTask.OnDownloadListener {
        private static final String LOG_TAG = PlaceholderFragment.class.getSimpleName();
        private static List<News> newsList;
        @InjectView(R.id.news_recycler_view)
        RecyclerView mNewsRecycler;
        private NewsAdapter mNewsAdapter;
        private ProgressDialog mProgressDialog;
        private DownloadFeedTask mDownloaderTask;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_front_page, container, false);
            ButterKnife.inject(this, rootView);
            RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            mNewsRecycler.setLayoutManager(layoutManager);
            if (savedInstanceState == null) {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("www.meneame.net")
                        .appendPath("feedburner-rss2.php")
                        .appendQueryParameter("rows", "20");
                Uri uri = builder.build();
                mDownloaderTask = new DownloadFeedTask();
                mDownloaderTask.addOnDownloadListener(this);
                mProgressDialog = ProgressDialog.show(this.getActivity(), "Descargando Noticias", "Descargando noticias, por favor espere...", true, false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mDownloaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uri.toString());
                } else {
                    mDownloaderTask.execute(uri.toString());
                }
            }

            return rootView;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            newsList = null;
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
            mNewsAdapter = new NewsAdapter(newsList);
            mNewsRecycler.swapAdapter(mNewsAdapter, true);
        }
    }
}
