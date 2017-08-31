package com.tenor.android.demo.downloader;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;


public abstract class GifDownloader<CTX extends Context> {

    @NonNull
    private final WeakReference<CTX> mWeakRef;
    @NonNull
    private final String mApplicationId;
    @Nullable
    private final File mOutput;

    public WeakReference<CTX> getWeakRef() {
        return mWeakRef;
    }

    /**
     * Constructor that wraps the context up with WeakReference to avoid memory leak due to async downloading process
     * use application context if you want the downloading process to be persisted
     *
     * @param ctx the subclass of {@link Context}
     * @param url the gif url of your interest
     */
    public GifDownloader(@NonNull CTX ctx,
                         @NonNull final String applicationId,
                         @NonNull String url) {
        this(new WeakReference<>(ctx), applicationId, url);
    }

    public GifDownloader(@NonNull WeakReference<CTX> weakRef,
                         @NonNull final String applicationId,
                         @NonNull String url) {
        mWeakRef = weakRef;
        mApplicationId = applicationId;
        mOutput = getGifDestination();

        if (weakRef.get() == null) {
            // reference got GCed, so the download process is not longer needed
            return;
        }

        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url cannot be empty");
        }


        final SimpleTarget<byte[]> target = new SimpleTarget<byte[]>() {
            @Override
            public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                try {
                    FileOutputStream outputStream = new FileOutputStream(mOutput);
                    outputStream.write(resource);
                    outputStream.close();
                } catch (IOException e) {
                    onLoadFailed(e, null);
                    failure();
                }
                success(getUri());
            }
        };

        DrawableTypeRequest<String> request = Glide.with(weakRef.get()).load(url);
        request.diskCacheStrategy(DiskCacheStrategy.SOURCE);
        request.asGif()
                .toBytes()
                .into(target);
    }

    /**
     * Get/Change destination of storing GIF, you may also point this to your cache location as well
     */
    @Nullable
    private File getGifDestination() {

        // create the file destination to hold GIF
        final File file = new File(getGifStorageDir(), generateUniqueGifFileName());

        // check if the creation process was success or not
        if (file.exists()) {
            if (!file.delete()) {
                /*
                 * SecurityException maybe thrown in here, if that is the case
                 *
                 * it mean the system cannot alter the file in this location,
                 * which is likely to be caused by missing storage permission
                 */
                return null;
            }
        }
        return file;
    }

    /**
     * Get/Change the temporary gif file name in here
     */
    @NonNull
    private String generateUniqueGifFileName() {
        // If you want the file to be hidden, you can also add "." prefix to the file name
        return "gif_name_" + System.nanoTime() + ".gif";
    }

    /**
     * Get/Change the folder name of storing gif in here
     */
    @Nullable
    private static File getGifStorageDir() {
        File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "gif_folder_name");
        if (!file.exists() && !file.mkdirs()) {
            // create folder fail, likely missing storage permission, handle permission request here
            return null;
        }
        return file;
    }

    /**
     * API 24+ Compatible method for getting {@link Uri}
     * <p>
     * Take a look at the configuration of {@link R.xml#provider_paths} under `res/xml/provide_paths.xml`,
     * and how it is being declared on the {@code AndroidManifest} under {@code <provider />} tag
     */
    private Uri getUri() {
        if (Build.VERSION.SDK_INT < 24) {
            return Uri.fromFile(mOutput);
        } else {
            return FileProvider.getUriForFile(mWeakRef.get(), mApplicationId + ".provider", mOutput);
        }
    }

    public abstract void success(@Nullable Uri uri);

    public abstract void failure();
}
