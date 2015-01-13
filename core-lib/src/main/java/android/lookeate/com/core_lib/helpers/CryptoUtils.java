package android.lookeate.com.core_lib.helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtils {
    public static String md5(String in) {
        if (!in.isEmpty()) {
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
        }

        return in;
    }

    public static String generateKontagentKey(String adId) {
        String md5Truncated = md5(adId).substring(0, 15);
        long decimalId = Long.parseLong(md5Truncated, 16);

        return String.valueOf(decimalId);
    }
}
