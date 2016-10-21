package com.creativtrendz.folio.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

/** Created by Jorell on 5/5/2016.*/
public class ImageGrabber {
    private final String TAG = getClass().getSimpleName();
    private OnImageLoaderListener mImageLoaderListener;
    private HashSet mUrlsInProgress = new HashSet();

    public ImageGrabber(@NonNull OnImageLoaderListener listener) {
        this.mImageLoaderListener = listener;
    }

    public static void writeToDisk(@NonNull final File imageFile, @NonNull final Bitmap image, @NonNull final OnBitmapSaveListener listener, @NonNull final Bitmap.CompressFormat format, boolean shouldOverwrite) {
        if (imageFile.isDirectory()) {
            listener.onBitmapSaveError(new ImageError("the specified path points to a directory, should be a file").setErrorCode(4));
            return;
        }
        if (imageFile.exists()) {
            if (!shouldOverwrite) {
                listener.onBitmapSaveError(new ImageError("file already exists, write operation cancelled").setErrorCode(2));
                return;
            } else if (!imageFile.delete()) {
                listener.onBitmapSaveError(new ImageError("could not delete existing file, most likely the write permission was denied").setErrorCode(3));
                return;
            }
        }
        File parent = imageFile.getParentFile();
        if (parent.exists() || parent.mkdirs()) {
            try {
                if (imageFile.createNewFile()) {
                    new AsyncTask<Void, Void, Void>() {
                        private ImageError error;

                        protected Void doInBackground(Void... params) {
                            Throwable e;
                            Throwable th;
                            FileOutputStream fos = null;
                            try {
                                FileOutputStream fos2 = new FileOutputStream(imageFile);
                                try {
                                    image.compress(format, 100, fos2);
                                    try {
                                        fos2.flush();
                                        fos2.close();
                                        fos = fos2;
                                    } catch (IOException e2) {
                                        e2.printStackTrace();
                                        fos = fos2;
                                    }
                                } catch (Throwable th3) {
                                    th = th3;
                                    fos = fos2;
                                    fos.flush();
                                    fos.close();
                                    throw th;
                                }
                            } catch (Throwable e4) {
                                e = e4;
                                error = new ImageError(e).setErrorCode(-1);
                                cancel(true);
                                if (fos != null) {
                                    try {
                                        fos.flush();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                    try {
                                        fos.close();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                return null;
                            }
                            return null;
                        }

                        protected void onCancelled() {
                            listener.onBitmapSaveError(this.error);
                        }

                        protected void onPostExecute(Void result) {
                            listener.onBitmapSaved();
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    return;
                } else {
                    listener.onBitmapSaveError(new ImageError("could not create file").setErrorCode(3));
                    return;
                }
            } catch (Throwable e) {
                listener.onBitmapSaveError(new ImageError(e).setErrorCode(-1));
                return;
            }
        }
        listener.onBitmapSaveError(new ImageError("could not create parent directory").setErrorCode(3));
    }

    public static Bitmap readFromDisk(@NonNull File imageFile) {
        if (!imageFile.exists() || imageFile.isDirectory()) {
            return null;
        }
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    }

    public static void readFromDiskAsync(@NonNull File imageFile, @NonNull final OnImageReadListener listener) {
        new AsyncTask<String, Void, Bitmap>() {
            protected Bitmap doInBackground(String... params) {
                return BitmapFactory.decodeFile(params[0]);
            }

            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    listener.onImageRead(bitmap);
                } else {
                    listener.onReadFailed();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageFile.getAbsolutePath());
    }

    public void download(@NonNull final String imageUrl, final boolean displayProgress) {
        if (mUrlsInProgress.contains(imageUrl)) {
            Log.w(TAG, "Downloader Code from FaceSlim");
        } else {
            new AsyncTask<Void, Integer, Bitmap>() {
                private ImageError error;

                protected void onPreExecute() {
                    mUrlsInProgress.add(imageUrl);
                    Log.d(TAG, "starting download");
                }

                protected void onCancelled() {
                    mUrlsInProgress.remove(imageUrl);
                    mImageLoaderListener.onError(error);
                }

                protected void onProgressUpdate(Integer... values) {
                    mImageLoaderListener.onProgressChange(values[0]);
                }

                protected Bitmap doInBackground(Void... params) {
                    Throwable e;
                    Throwable th;
                    Bitmap bitmap = null;
                    HttpURLConnection connection = null;
                    InputStream inputStream = null;
                    ByteArrayOutputStream out = null;
                    try {
                        connection = (HttpURLConnection) new URL(imageUrl).openConnection();
                        if (displayProgress) {
                            connection.connect();
                            if (connection.getContentLength() <= 0) {
                                error = new ImageError("Invalid content length. The URL is probably not pointing to a file").setErrorCode(0);
                                cancel(true);
                            }
                            InputStream is = new BufferedInputStream(connection.getInputStream(), 8192);
                            try {
                                ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                                try {
                                    byte[] bytes = new byte[8192];
                                    long read = 0;
                                    while (true) {
                                        int count = is.read(bytes);
                                        if (count == -1) {
                                            break;
                                        }
                                        read += (long) count;
                                        out2.write(bytes, 0, count);
                                        publishProgress((int) ((100 * read) / ((long) 3000)));
                                    }
                                    bitmap = BitmapFactory.decodeByteArray(out2.toByteArray(), 0, out2.size());
                                    out = out2;
                                    inputStream = is;
                                } catch (Throwable th2) {
                                    th = th2;
                                    out = out2;
                                    inputStream = is;
                                    if (connection != null) {
                                        connection.disconnect();
                                    }
                                    if (out != null) {
                                        out.flush();
                                        out.close();
                                    }
                                    if (inputStream != null) {
                                        inputStream.close();
                                    }
                                    throw th;
                                }
                            } catch (Throwable th3) {
                                th = th3;
                                inputStream = is;
                                if (connection != null) {
                                    connection.disconnect();
                                }
                                if (out != null) {
                                    out.flush();
                                    out.close();
                                }
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                                throw th;
                            }
                        }
                        inputStream = connection.getInputStream();
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        if (connection != null) {
                            try {
                                connection.disconnect();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                        if (out != null) {
                            out.flush();
                            out.close();
                        }
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (Throwable th4) {
                        e = th4;
                        if (isCancelled()) {
                            error = new ImageError(e).setErrorCode(-1);
                            cancel(true);
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                        if (out != null) {
                            try {
                                out.flush();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            try {
                                out.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        return bitmap;
                    }
                    return bitmap;
                }

                protected void onPostExecute(Bitmap result) {
                    if (result == null) {
                        Log.e(TAG, "factory returned a null result");
                        mImageLoaderListener.onError(new ImageError("downloaded file could not be decoded as bitmap").setErrorCode(1));
                    } else {
                        Log.d(TAG, "download complete, " + result.getByteCount() + " bytes transferred");
                        mImageLoaderListener.onComplete(result);
                    }
                    mUrlsInProgress.remove(imageUrl);
                    System.gc();
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public interface OnImageLoaderListener {
        void onComplete(Bitmap bitmap);

        void onError(ImageError imageError);

        void onProgressChange(int i);
    }

    interface OnBitmapSaveListener {
        void onBitmapSaveError(ImageError imageError);

        void onBitmapSaved();
    }

    interface OnImageReadListener {
        void onImageRead(Bitmap bitmap);

        void onReadFailed();
    }

    public static final class ImageError extends Throwable {
        public static final int ERROR_DECODE_FAILED = 1;
        public static final int ERROR_FILE_EXISTS = 2;
        public static final int ERROR_GENERAL_EXCEPTION = -1;
        public static final int ERROR_INVALID_FILE = 0;
        public static final int ERROR_IS_DIRECTORY = 4;
        public static final int ERROR_PERMISSION_DENIED = 3;
        private int errorCode;

        ImageError(@NonNull String message) {
            super(message);
        }

        ImageError(@NonNull Throwable error) {
            super(error.getMessage(), error.getCause());
            setStackTrace(error.getStackTrace());
        }

        public int getErrorCode() {
            return errorCode;
        }

        public ImageError setErrorCode(int code) {
            errorCode = code;
            return this;
        }
    }
}

