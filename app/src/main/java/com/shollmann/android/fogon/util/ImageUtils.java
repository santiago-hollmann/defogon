package com.shollmann.android.fogon.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;

import com.shollmann.android.fogon.AppApplication;
import com.shollmann.android.wood.helpers.LogInternal;
import com.shollmann.android.wood.network.NetworkUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageUtils {

    public static boolean isExternalAvailable() {
        return (AppApplication.getApplication().getFilesDir() != null);
    }

    public static String getPathImages() {
        return isExternalAvailable() ? AppApplication.getApplication().getFilesDir().getPath() + "/" : null;
    }

    public static String hash(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return in;

    }

    public static Bitmap saveBitmap(String address, String sFileName, long expires, PointF iSize) {
        File fFile = new File(getPathImages(), sFileName);
        Bitmap b = null;
        if (fFile.exists()) {
            b = decodeFile(fFile, iSize, 0);
            long modifiedSince = System.currentTimeMillis() - fFile.lastModified();
            if (modifiedSince > expires) {
                fFile.delete();
                b = null;
            }
        }
        if (b == null && NetworkUtilities.isOnline()) {
            InputStream is = null;
            OutputStream os = null;
            try {
                is = new URL(address).openStream();
                os = new FileOutputStream(fFile);
                copyStream(is, os);
                b = decodeFile(fFile, iSize, 0);
            } catch (Exception ex) {
                // ex.printStackTrace();
                // LogInternal.error(ex.getMessage());
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        // LogInternal.error(e.getMessage());
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        // LogInternal.error(e.getMessage());
                    }
                }
            }
        }
        return b;
    }

    public static void copyStream(InputStream in, OutputStream out) {
        // Read bytes and write to destination until eof
        byte[] buf = new byte[1024];
        int len = 0;
        try {
            while ((len = in.read(buf, 0, 1024)) >= 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            LogInternal.log("ERROR: Could not copy stream. " + e.getMessage());
        }
    }

    public static Bitmap decodeFile(File f, PointF iSize, int rotation) {
        try {

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream = new FileInputStream(f);
            BitmapFactory.decodeStream(stream, null, o);
            stream.close();
            int width_tmp = o.outWidth;
            int height_tmp = o.outHeight;

            if (width_tmp == 1) {
                f.delete();
                return null;
            }
            int scale = 1;
            width_tmp /= 2;
            height_tmp /= 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((height_tmp / scale) > iSize.y && (width_tmp / scale) > iSize.x) {
                scale *= 2;
            }

            //This resolution reduction tweak was created to allow super-low-end phones to display many thumbnails on-screen
            //It started to get visually noticeable on big-screen/low-res devices like the Galaxy Tab 3. So it got conditioned.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                scale *= 2;
            }

            o.inJustDecodeBounds = false;
            o.inPurgeable = true;
            o.inDither = true;
            o.inPreferredConfig = Bitmap.Config.RGB_565;
            o.inSampleSize = scale;

            if (rotation > 0) {
                Matrix matrix = new Matrix();

                matrix.setRotate(rotation);
                Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f), null, o);

                return Bitmap.createBitmap(bmp, 0, 0, Math.round(width_tmp / scale), Math.round(height_tmp / scale), matrix, true);
            } else {
                stream = new FileInputStream(f);
                Bitmap bmp = BitmapFactory.decodeStream(stream, null, o);
                stream.close();
                return bmp;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
