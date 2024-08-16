package com.daniel.gameguru.Utilities;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

public class Utilities {
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(),
                    0
            );
        }
    }

}
