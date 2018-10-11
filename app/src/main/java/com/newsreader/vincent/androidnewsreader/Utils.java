package com.newsreader.vincent.androidnewsreader;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

public class Utils {

    //hides the keyboard shown on screen
    public static void hideSoftKeyboard(Activity activity) {
        if(activity != null && activity.getCurrentFocus() != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
