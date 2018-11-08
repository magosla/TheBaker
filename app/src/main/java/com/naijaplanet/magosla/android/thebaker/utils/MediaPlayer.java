package com.naijaplanet.magosla.android.thebaker.utils;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.naijaplanet.magosla.android.thebaker.R;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MediaPlayer implements Player.EventListener {
    static final int CACHE_SIZE = 1024 * 1024;
    private final Context mContext;
    private final PlayerView mPlayerView;
    private final Uri mMediaUri;
    private SimpleExoPlayer mExoPlayer;

    private Callback mListener;


    @SuppressWarnings("unused")
    private DefaultTrackSelector mTrackSelector;
    @SuppressWarnings("unused")
    private DefaultTrackSelector.Parameters mTrackSelectorParameters;
    @SuppressWarnings("unused")
    private PlaybackStateCompat.Builder mStateBuilder;

    public MediaPlayer(Context context, @NonNull PlayerView playerView, @NonNull Uri mediaUri, @Nullable Callback listener) {
        mContext = context;
        mPlayerView = playerView;
        mMediaUri = mediaUri;
        if (listener != null) {
            mListener = listener;
        }
        setup();
    }

    private void setup() {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, new DefaultRenderersFactory(mContext),
                    trackSelector, loadControl);

            // Set the ExoPlayer.EventListener to mContext activity.
            mExoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(mContext, mContext.getString(R.string.app_name));

            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,userAgent);



            int cacheFlags = CacheDataSource.FLAG_BLOCK_ON_CACHE;
            CacheDataSourceFactory cacheDataSource = new CacheDataSourceFactory(VideoCache.getInstance(mContext),
                    dataSourceFactory,cacheFlags,CACHE_SIZE);


            // This is the MediaSource representing the media to be played.
            MediaSource videoSource = new ExtractorMediaSource.Factory(cacheDataSource)
                    .createMediaSource(mMediaUri);



            // Prepare the player with the source.
            mExoPlayer.prepare(videoSource);

            mPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.setPlayWhenReady(true);

            mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
          //  mExoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_ENDED:
                Log.i("EventListenerState", "Playback ended!");
                mExoPlayer.setPlayWhenReady(false);
                mListener.playbackEnded();
                break;
            case Player.STATE_READY:
                Log.i("EventListenerState", "Playback State Ready!");
                mPlayerView.setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING);
                mListener.playbackReady();
                mExoPlayer.setPlayWhenReady(true);
                break;
            case Player.STATE_BUFFERING:
                Log.i("EventListenerState", "Playback buffering");
                mExoPlayer.setPlayWhenReady(false);
                mListener.playbackBuffering();

                break;
            case Player.STATE_IDLE:

                mListener.playbackIdle();
                break;

        }
    }

    public void release() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    public interface Callback {
        default void playbackBuffering() {
        }

        default void playbackReady() {
        }

        default void playbackEnded() {
        }

        default void playbackIdle() {
        }
    }

}

 class VideoCache {
    private static SimpleCache sDownloadCache;

    public static SimpleCache getInstance(Context context) {
        if (sDownloadCache == null) {
           // sDownloadCache = new SimpleCache(new File(context.getCacheDir(), "exoCache"), new NoOpCacheEvictor());
            // Specify cache folder, my cache folder named media which is inside getCacheDir.
            File file = new File(context.getCacheDir(), "media");
            sDownloadCache = new SimpleCache(file, new LeastRecentlyUsedCacheEvictor(MediaPlayer.CACHE_SIZE));


        }
        return sDownloadCache;
    }
}
