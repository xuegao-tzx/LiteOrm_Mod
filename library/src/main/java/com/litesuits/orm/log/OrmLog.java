package com.litesuits.orm.log;

/**
 * the logger
 *
 * @author MaTianyu 2014-1-1下午4:05:39
 */
public final class OrmLog {

    /**
     * The constant isPrint.
     */
    public static boolean isPrint;
    /**
     * The constant defaultTag.
     */
    private static String defaultTag = "OrmLog";

    /**
     * Instantiates a new Orm log.
     */
    private OrmLog() {
    }

    /**
     * Sets tag.
     *
     * @param tag the tag
     */
    public static void setTag(String tag) {
        OrmLog.defaultTag = tag;
    }

    /**
     * int.
     *
     * @param o the o
     * @return the int
     */
    public static int i(Object o) {
        return isPrint && o != null ? Log.i(defaultTag, o.toString()) : -1;
    }

    /**
     * int.
     *
     * @param m the m
     * @return the int
     */
    public static int i(String m) {
        return isPrint && m != null ? Log.i(defaultTag, m) : -1;
    }

    /*********************** Log  @param tag the tag
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int v(String tag, String msg) {
        return isPrint && msg != null ? Log.i(tag, msg) : -1;
    }

    /**
     * D int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int d(String tag, String msg) {
        return isPrint && msg != null ? Log.d(tag, msg) : -1;
    }

    /**
     * int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int i(String tag, String msg) {
        return isPrint && msg != null ? Log.i(tag, msg) : -1;
    }

    /**
     * W int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int w(String tag, String msg) {
        return isPrint && msg != null ? Log.w(tag, msg) : -1;
    }

    /**
     * E int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int e(String tag, String msg) {
        return isPrint && msg != null ? Log.e(tag, msg) : -1;
    }

    /*********************** Log with object list  @param tag the tag
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int v(String tag, Object... msg) {
        return isPrint ? Log.i(tag, getLogMessage(msg)) : -1;
    }

    /**
     * D int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int d(String tag, Object... msg) {
        return isPrint ? Log.d(tag, getLogMessage(msg)) : -1;
    }

    /**
     * int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int i(String tag, Object... msg) {
        return isPrint ? Log.i(tag, getLogMessage(msg)) : -1;
    }

    /**
     * W int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int w(String tag, Object... msg) {
        return isPrint ? Log.w(tag, getLogMessage(msg)) : -1;
    }

    /**
     * E int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int e(String tag, Object... msg) {
        return isPrint ? Log.e(tag, getLogMessage(msg)) : -1;
    }

    /**
     * Gets log message.
     *
     * @param msg the msg
     * @return the log message
     */
    private static String getLogMessage(Object... msg) {
        if (msg != null && msg.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (Object s : msg) if (s != null) sb.append(s);
            return sb.toString();
        }
        return "";
    }

    /*********************** Log with Throwable  @param tag the tag
     * @param tag the tag
     * @param msg the msg
     * @param tr the tr
     * @return the int
     */
    public static int v(String tag, String msg, Throwable tr) {
        return isPrint && msg != null ? Log.i(tag, msg, tr) : -1;
    }

    /**
     * D int.
     *
     * @param tag the tag
     * @param msg the msg
     * @param tr  the tr
     * @return the int
     */
    public static int d(String tag, String msg, Throwable tr) {
        return isPrint && msg != null ? Log.d(tag, msg, tr) : -1;
    }

    /**
     * int.
     *
     * @param tag the tag
     * @param msg the msg
     * @param tr  the tr
     * @return the int
     */
    public static int i(String tag, String msg, Throwable tr) {
        return isPrint && msg != null ? Log.i(tag, msg, tr) : -1;
    }

    /**
     * W int.
     *
     * @param tag the tag
     * @param msg the msg
     * @param tr  the tr
     * @return the int
     */
    public static int w(String tag, String msg, Throwable tr) {
        return isPrint && msg != null ? Log.w(tag, msg, tr) : -1;
    }

    /**
     * E int.
     *
     * @param tag the tag
     * @param msg the msg
     * @param tr  the tr
     * @return the int
     */
    public static int e(String tag, String msg, Throwable tr) {
        return isPrint && msg != null ? Log.e(tag, msg, tr) : -1;
    }

    /*********************** TAG use Object Tag  @param tag the tag
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int v(Object tag, String msg) {
        return isPrint ? Log.i(tag.getClass().getSimpleName(), msg) : -1;
    }

    /**
     * D int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int d(Object tag, String msg) {
        return isPrint ? Log.d(tag.getClass().getSimpleName(), msg) : -1;
    }

    /**
     * int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int i(Object tag, String msg) {
        return isPrint ? Log.i(tag.getClass().getSimpleName(), msg) : -1;
    }

    /**
     * W int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int w(Object tag, String msg) {
        return isPrint ? Log.w(tag.getClass().getSimpleName(), msg) : -1;
    }

    /**
     * E int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int e(Object tag, String msg) {
        return isPrint ? Log.e(tag.getClass().getSimpleName(), msg) : -1;
    }
}
