package com.creativtrendz.folio.ui;

import android.webkit.JavascriptInterface;
import com.creativtrendz.folio.activities.MainActivity;

public class FolioInterfaces {
    private final MainActivity mContext;


    public FolioInterfaces(MainActivity c) {
        mContext = c;
    }





    @JavascriptInterface
    public void getNums(final String notifications, final String messages, final String requests, final String feed) {
        final int notifications_int = FolioHelpers.isInteger(notifications) ? Integer.parseInt(notifications) : 0;
        final int messages_int = FolioHelpers.isInteger(messages) ? Integer.parseInt(messages) : 0;
        final int requests_int = FolioHelpers.isInteger(requests) ? Integer.parseInt(requests): 0;
        final int feed_int = FolioHelpers.isInteger(feed) ? Integer.parseInt(feed): 0;
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContext.setNotificationNum(notifications_int);
                mContext.setMessagesNum(messages_int);
                mContext.setFriendsNum(requests_int);
                mContext.setNewsNum(feed_int);
            }
        });
    }
}
