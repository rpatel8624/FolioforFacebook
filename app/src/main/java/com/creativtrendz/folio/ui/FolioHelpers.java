package com.creativtrendz.folio.ui;

import android.webkit.WebView;

public class FolioHelpers {


    private static final int BADGE_UPDATE_INTERVAL = 5000;

    
    public static void updateNums(WebView view) {
        view.loadUrl("javascript:(function()%7Bandroid.getNums(document.querySelector(%22%23notifications_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23messages_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23requests_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML)%7D).innerHTML%2Cdocument.querySelector(%22%23feed_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML)%7D)()");
    }

    public static void updateNumsService(WebView view) {
        view.loadUrl("javascript:(function()%7Bfunction%20n_s()%7Bandroid.getNums(document.querySelector(%22%23notifications_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23messages_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23requests_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23feed_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML)%2CsetTimeout(n_s%2C" + BADGE_UPDATE_INTERVAL + ")%7Dtry%7Bn_s()%7Dcatch(_)%7B%7D%7D)()");
    }




    public static boolean isInteger(String str) {
        return (str.matches("^-?\\d+$"));
    }
}
    


