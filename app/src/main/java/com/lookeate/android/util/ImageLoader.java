package com.lookeate.android.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.view.ContextThemeWrapper;

import com.lookeate.android.AppApplication;
import com.lookeate.android.ui.views.RemoteImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

public class ImageLoader {

    final int memClass =
            ((ActivityManager) AppApplication.getApplication().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();

    // Use 1/8th of the available memory for this memory cache.
    final int cacheSize = 1024 * 1024 * memClass / 8;

    private static ImageLoader instance;
    private static final int THREAD_COUNT = 4;
    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(cacheSize) {
        // Return the size of a bitmap. Used by LruCache to determine the cache
        // size
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    };

    private File cacheDir;
    private final PhotosQueue photosQueue = new PhotosQueue();
    private final ArrayList<PhotosLoader> loaders = new ArrayList<PhotosLoader>();

    public static void clearCache() {
        if (instance != null) {
            instance.cache.evictAll();
        }
    }

    public static void removeCache(String key) {
        if (instance != null) {
            instance.cache.remove(key);
        }
    }

    public static ImageLoader get() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    private ImageLoader() {
        for (int i = 0; i < THREAD_COUNT; i++) {
            PhotosLoader loader = new PhotosLoader();
            loader.setName("Photo_Loader_" + i);
            loader.setPriority(Thread.MIN_PRIORITY);
            loader.start();
            loaders.add(loader);
        }
    }

    public Bitmap get(final String url, PointF size) {
        if (url != null) {
            String key = ImageLoader.getCacheKey(url, size);
            return cache.get(key);
        }
        return null;
    }

    public static String getFileName(String url) {
        if (url.contains("/")) {
            if (url != null && url.length() > 0) {
                String file = url.substring(url.lastIndexOf("/"), url.length());
                file = file.replace("?", "_").replace("=", "_").replace(":", "_");
                return ImageUtils.hash(file) + ".png";
            }
        } else {
            return url + ".png";
        }
        return null;
    }

    public static String getCacheKey(String url, PointF iSize) {
        if (url.contains("/")) {
            if (url != null && url.length() > 0) {
                String file = url.substring(url.lastIndexOf("/"), url.length());
                file = file.replace("?", "_").replace("=", "_").replace(":", "_");
                file = file + "_" + iSize.x + "_" + iSize.y;
                return ImageUtils.hash(file);
            }
        } else {
            String file = url + "_" + iSize.x + "_" + iSize.y;
            return ImageUtils.hash(file);
        }

        return null;
    }

    public void displayImage(Uri uri, RemoteImageView imageView, String sPath, String sFileName, PointF iSize, int exifRotation) {
        if (sPath == null) {
            return;
        }
        cacheDir = new File(sPath);
        String sFile;
        if (sFileName != null) {
            sFile = sFileName;
        } else {
            if (uri.getPath() == null) {
                imageView.showStubImage();
                return;
            } else {
                sFile = getFileName(uri.getPath());
            }
        }
        String key = getCacheKey(uri.toString(), iSize);
        if (sFile == null) {
            imageView.showStubImage();
        } else {
            Bitmap bitmap = null;
            bitmap = cache.get(key);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                queuePhoto(uri, imageView, sFile, iSize, exifRotation);
            }
        }
    }

    public void displayImage(String url, RemoteImageView imageView, String sPath, String sFileName, PointF iSize, int exifRotation) {
        if (sPath == null) {
            return;
        }
        cacheDir = new File(sPath);
        String sFile;
        if (sFileName != null) {
            sFile = sFileName;
        } else {
            sFile = getFileName(url);
        }
        String key = getCacheKey(url, iSize);
        if (sFile == null) {
            imageView.showStubImage();
        } else {
            Bitmap bitmap = null;
            bitmap = cache.get(key);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                queuePhoto(url, imageView, sFile, iSize, exifRotation);
            }
        }
    }

    private void queuePhoto(Uri uri, RemoteImageView imageView, String sFileName, PointF iSize, int exifRotation) {
        PhotoToLoad photoToLoad = new PhotoToLoad(uri, imageView, sFileName, iSize, exifRotation);
        synchronized (photosQueue.photosToLoad) {
            photosQueue.photosToLoad.push(photoToLoad);
            photosQueue.photosToLoad.notifyAll();
        }
    }

    private void queuePhoto(String url, RemoteImageView imageView, String sFileName, PointF iSize, int exifRotation) {
        PhotoToLoad photoToLoad = new PhotoToLoad(url, imageView, sFileName, iSize, exifRotation);
        synchronized (photosQueue.photosToLoad) {
            photosQueue.photosToLoad.push(photoToLoad);
            photosQueue.photosToLoad.notifyAll();
        }
    }

    class PhotoToLoad implements Runnable {
        public String url;
        public Uri uri;
        public RemoteImageView imageView;
        public String sFileName;
        public Bitmap bitmap;
        public PointF size = new PointF(64, 64);
        public int exifRotation;

        public PhotoToLoad(String u, RemoteImageView i, String sFile, PointF iSize, int rotation) {
            url = u;
            imageView = i;
            sFileName = sFile;
            size = iSize;
            exifRotation = rotation;
        }

        public PhotoToLoad(Uri imageUri, RemoteImageView i, String sFile, PointF iSize, int rotation) {
            uri = imageUri;
            url = imageUri.toString();
            imageView = i;
            sFileName = sFile;
            size = iSize;
            exifRotation = rotation;
        }

        @Override
        public void run() {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.showStubImage();
            }
        }
    }

    class PhotosQueue {
        private final Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();
    }

    class PhotosLoader extends Thread {
        @Override
        public void run() {
            long expires;
            String key = Constants.EMPTY_STRING;
            try {
                while (true) {
                    if (photosQueue.photosToLoad.size() == 0) {
                        synchronized (photosQueue.photosToLoad) {
                            photosQueue.photosToLoad.wait();
                        }
                    }
                    if (photosQueue.photosToLoad.size() != 0) {
                        PhotoToLoad photoToLoad = null;
                        synchronized (photosQueue.photosToLoad) {
                            if (!photosQueue.photosToLoad.isEmpty()) {
                                photoToLoad = photosQueue.photosToLoad.pop();
                            }
                        }
                        if (photoToLoad != null) {
                            expires = Long.MAX_VALUE;
                            key = ImageLoader.getCacheKey(photoToLoad.url, photoToLoad.size);
                            Bitmap bmp = null;

                            if (photoToLoad.uri == null) {
                                bmp = ImageUtils.saveBitmap(photoToLoad.url, photoToLoad.sFileName, expires, photoToLoad.size);
                            } else {
                                bmp = ImageUtils.decodeFile(new File(photoToLoad.url), photoToLoad.size, photoToLoad.exifRotation);
                            }

                            if (bmp != null) {
                                cache.put(key, bmp);
                                if (photoToLoad.bitmap != null) {
                                    photoToLoad.bitmap.recycle();
                                }
                                photoToLoad.bitmap = bmp;

                            } else {
                                File myFile = new File(cacheDir, photoToLoad.sFileName);
                                myFile.delete();
                            }

                            if (photoToLoad.url.equalsIgnoreCase((String) photoToLoad.imageView.getTag())) {
                                ContextThemeWrapper b = (ContextThemeWrapper) photoToLoad.imageView.getContext();
                                Handler h = new Handler(b.getMainLooper());
                                h.post(photoToLoad);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
