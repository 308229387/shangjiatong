package com.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardUtil {

    public static void hideSoftInput(Activity acitivity) {
        InputMethodManager imm = (InputMethodManager) acitivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(acitivity.getWindow().getDecorView().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showSoftInput(EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) et.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static void showSoftInputDelay(final EditText et, int select) {
        et.postDelayed(new Runnable() {

            @Override
            public void run() {
                showSoftInput(et);
                et.selectAll();
            }
        }, 300);
    }

    public static void hideKeyboard(Activity activity, EditText edit) {
        InputMethodManager im = (InputMethodManager) edit
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void closeBoard(Context mcontext) {
        InputMethodManager imm = (InputMethodManager) mcontext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
        if (imm.isActive())
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showKeyboard(EditText edit) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        edit.requestFocusFromTouch();
        InputMethodManager im = (InputMethodManager) edit
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.showSoftInput(edit, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

}
