package me.cristiangomez.wolfreader.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.joda.time.Interval;
import org.joda.time.Period;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.cristiangomez.wolfreader.R;
import me.cristiangomez.wolfreader.VolleySingleton;
import me.cristiangomez.wolfreader.model.News;

/**
 * Created by Cristian on 10/18/2014.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private News[] mNewsDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitleView;
        public TextView mUpVotesView;
        public TextView mAuthorView;
        public TextView mSourceView;
        public View mView;
        @InjectView(R.id.sub) TextView mSubView;
        @InjectView(R.id.click_number) TextView mClikNumberView;
        @InjectView(R.id.since) TextView mSinceView;
        @InjectView(R.id.article_photo) NetworkImageView mImage;
        public ViewHolder (View view) {
            super(view);
            mTitleView = (TextView) view.findViewById(R.id.title);
            mUpVotesView = (TextView) view.findViewById(R.id.up_votes);
            mAuthorView = (TextView) view.findViewById(R.id.author);
            mSourceView = (TextView) view.findViewById(R.id.source);
            ButterKnife.inject(this,view);
        }
    }

    public NewsAdapter(News[] newsDataSet) {
        mNewsDataset = newsDataSet;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        News news = (News) mNewsDataset[position];
        if (news.getImageUrl()!=null && !news.getImageUrl().isEmpty()) {
            viewHolder.mImage.setImageUrl(news.getImageUrl(), VolleySingleton.getInstance(null).getImageLoader());
        }
        viewHolder.mTitleView.setText(news.getTitle());
        try {
            String source = news.getSource();
            if (news.getSource().contains(".")) {
                source = news.getSource().split("\\.")[0];
            }
            source = source.substring(0,1).toUpperCase() + source.substring(1);
            viewHolder.mSourceView.setText(source);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        viewHolder.mAuthorView.setText(news.getAuthor());
        viewHolder.mUpVotesView.setText(String.valueOf(news.getUpVotes()));
        viewHolder.mSubView.setText(news.getSub());
        viewHolder.mUpVotesView.setText(String.valueOf(news.getUpVotes()));
        viewHolder.mClikNumberView.setText(String.valueOf(news.getClicks()));
        Date since = news.getPublicationDate();
        Date now = new Date();
        Interval interval = new Interval(since.getTime(), now.getTime());
        Period period = interval.toPeriod();
        String elapse = String.format("%dd",period.getDays());
        viewHolder.mSinceView.setText(elapse);
    }


    @Override
    public int getItemCount() {
        return mNewsDataset.length;
    }
}