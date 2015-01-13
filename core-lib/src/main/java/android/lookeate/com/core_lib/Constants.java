package android.lookeate.com.core_lib;

public class Constants {

    public static final String EMPTY_STRING = "";
    public static final String SPACE_STRING = " ";
    public static final String COMMA_STRING = ", ";

    public static class Errors {
        public static final int NO_NETWORK = -77;
        public static final int UNEXPECTED = -33;
        public static final int TIME_OUT = -22;
        public static final int SOMETHING_WENT_WRONG = 94;
    }

    public static class StatusCode {
        public static final int OK = 200;
    }

    public static interface Endpoints {
        public static final int PRODUCTION = 0;
        public static final int TESTING = 1;
        public static final int MOCK = 2;
    }

    public static class ExtraKeys {
        public final static String SCHEME = "";
        public final static String FULL_SCHEME = SCHEME + "://data/";
        public final static String DATA = "data";
        public final static String DATA_2 = "data2";
        public final static String DATA_3 = "data3";
    }

    public static class Actions {
        public final static String MESSAGE = "message";
        public final static String NETWORK = "network";
        public final static String CONNECTIONS = "connections";
    }
}
