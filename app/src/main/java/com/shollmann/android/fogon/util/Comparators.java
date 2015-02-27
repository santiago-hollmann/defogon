package com.shollmann.android.fogon.util;

import java.util.Comparator;

public class Comparators {

    // This is just a sample
    public static Comparator<?> comparatorItem = new Comparator<Class>() {
        @Override
        public int compare(Class arg0, Class arg1) {
            // Make some comparation between class' attributes
            // and return 1, 0 or -1 acorrding to it.
            return 0;
        }
    };

}
