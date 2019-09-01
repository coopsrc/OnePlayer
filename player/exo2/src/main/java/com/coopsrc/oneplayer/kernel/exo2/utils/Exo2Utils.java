package com.coopsrc.oneplayer.kernel.exo2.utils;


import android.content.Context;
import android.text.TextUtils;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-06-28 18:05
 */
public class Exo2Utils {
    private static String userAgent;

    private static DatabaseProvider databaseProvider;
    private static Cache downloadCache;
    private static File downloadDirectory;
    private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";

    public RenderersFactory buildRenderersFactory(Context context, boolean preferExtensionRenderer) {
        @DefaultRenderersFactory.ExtensionRendererMode
        int extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
        return new DefaultRenderersFactory(context).setExtensionRendererMode(extensionRendererMode);
    }


    public static DataSource.Factory buildDataSourceFactory(Context context) {
        DefaultDataSourceFactory upstreamFactory = new DefaultDataSourceFactory(context, buildHttpDataSourceFactory(context));
        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache(context));
    }

    private static synchronized Cache getDownloadCache(Context context) {
        if (downloadCache == null) {
            File downloadContentDirectory = new File(getDownloadDirectory(context), DOWNLOAD_CONTENT_DIRECTORY);
            downloadCache =
                    new SimpleCache(downloadContentDirectory, new NoOpCacheEvictor(), getDatabaseProvider(context));
        }
        return downloadCache;
    }

    private static DatabaseProvider getDatabaseProvider(Context context) {
        if (databaseProvider == null) {
            databaseProvider = new ExoDatabaseProvider(context);
        }
        return databaseProvider;
    }

    private static File getDownloadDirectory(Context context) {
        if (downloadDirectory == null) {
            downloadDirectory = context.getApplicationContext().getExternalFilesDir(null);
            if (downloadDirectory == null) {
                downloadDirectory = context.getApplicationContext().getFilesDir();
            }
        }
        return downloadDirectory;
    }

    private static HttpDataSource.Factory buildHttpDataSourceFactory(Context context) {
        if (TextUtils.isEmpty(userAgent)) {
            userAgent = Util.getUserAgent(context, "OneExoPlayer2");
        }
        return new DefaultHttpDataSourceFactory(userAgent);
    }

    private static CacheDataSourceFactory buildReadOnlyCacheDataSource(DataSource.Factory upstreamFactory, Cache cache) {
        return new CacheDataSourceFactory(
                cache,
                upstreamFactory,
                new FileDataSourceFactory(),
                /* cacheWriteDataSinkFactory= */ null,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                /* eventListener= */ null);
    }

}
