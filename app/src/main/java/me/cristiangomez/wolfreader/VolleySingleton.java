package me.cristiangomez.wolfreader;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Cristian on 10/21/2014.
 */
public class VolleySingleton {
    private static VolleySingleton instance;
    private static Context mContext;
    private static RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private VolleySingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache(){
            private final LruCache<String, Bitmap>
            cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url,bitmap);
            }
        });
    }
    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance==null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue==null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(),new HurlStack());

        }
        return mRequestQueue;
    }

    public <T> void addToRequest(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

}
