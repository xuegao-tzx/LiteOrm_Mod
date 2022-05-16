/*
 * Copyright (C) 2021 Huawei Device Co., Ltd.
 */

package com.litesuits.orm.log;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

/**
 * The type Log.
 */
public class Log {
    /**
     * The constant MY_TAG.
     */
    private static final String MY_TAG = "TZXCL_MY_TAG_" + Log.class.getPackage().getName();
    /**
     * The constant LABEL.
     */
    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00234, MY_TAG);

    /**
     * E int.
     *
     * @param tag the tag
     * @param log the log
     * @param e   the e
     * @return the int
     */
    public static int e(String tag, String log, Throwable e) {
        return HiLog.error(LABEL, "Tag:%{public}s - Log:%{public}s - StackTrace:%{public}s", tag, log, HiLog.getStackTrace(e));
    }

    /**
     * E int.
     *
     * @param tag the tag
     * @param e   the e
     * @return the int
     */
    public static int e(String tag, Throwable e) {
        return HiLog.error(LABEL, "Tag:%{public}s - StackTrace:%{public}s", tag, HiLog.getStackTrace(e));
    }

    /**
     * E int.
     *
     * @param tag the tag
     * @param log the log
     * @return the int
     */
    public static int e(String tag, String log) {
        return HiLog.error(LABEL, "Tag:%{public}s - Log:%{public}s", tag, log);
    }

    /**
     * W int.
     *
     * @param tag the tag
     * @param e   the e
     * @return the int
     */
    public static int w(String tag, Throwable e) {
        return HiLog.warn(LABEL, "Tag:%{public}s - StackTrace:%{public}s", tag, HiLog.getStackTrace(e));
    }

    /**
     * W int.
     *
     * @param tag the tag
     * @param msg the msg
     * @param e   the e
     * @return the int
     */
    static int w(String tag, String msg, Throwable e) {
        return HiLog.warn(LABEL, "Tag:%{public}s - %{public}s - StackTrace:%{public}s", tag, msg, HiLog.getStackTrace(e));
    }

    /**
     * W int.
     *
     * @param tag the tag
     * @param log the log
     * @return the int
     */
    public static int w(String tag, String log) {
        return HiLog.warn(LABEL, "Tag:%{public}s - Log:%{public}s", tag, log);
    }

    /**
     * D int.
     *
     * @param tag the tag
     * @param log the log
     * @return the int
     */
    public static int d(String tag, String log) {
        return HiLog.debug(LABEL, "Tag:%{public}s - Log:%{public}s", tag, log);
    }

    /**
     * D int.
     *
     * @param tag the tag
     * @param log the log
     * @param e   the e
     * @return the int
     */
    public static int d(String tag, String log, Throwable e) {
        return HiLog.debug(LABEL, "Tag:%{public}s - Log:%{public}s - StackTrace:%{public}s", tag, log, HiLog.getStackTrace(e));
    }

    /**
     * int.
     *
     * @param tag the tag
     * @param log the log
     * @return the int
     */
    public static int i(String tag, String log) {
        return HiLog.info(LABEL, "Tag:%{public}s - Log:%{public}s", tag, log);
    }

    /**
     * int.
     *
     * @param tag the tag
     * @param log the log
     * @param e   the e
     * @return the int
     */
    public static int i(String tag, String log, Throwable e) {
        return HiLog.info(LABEL, "Tag:%{public}s - Log:%{public}s - StackTrace:%{public}s", tag, log, HiLog.getStackTrace(e));
    }

    /**
     * Println.
     *
     * @param tag the tag
     * @param log the log
     */
    public static void println(String tag, String log) {
        String str;
        if (tag.contains("%s")) str = String.format(tag, log);
        else str = String.format("Tag:%s - Log:%s", tag, log);
        debug(str);
    }

    /**
     * Println.
     *
     * @param string the string
     * @param args   the args
     */
    public static void println(String string, Object... args) {
        String str = String.format(string, args);
        debug(str);
    }

    /**
     * Debug.
     *
     * @param log the log
     */
    private static void debug(String log) {
        System.out.println(MY_TAG + "---" + log);
//        XLog.warn(LABEL, log);
    }

}
