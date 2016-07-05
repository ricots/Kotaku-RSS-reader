package com.oskalenko.kotakurss.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.oskalenko.kotakurss.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 04.07.2016
 * Time: 20:55
 */

public abstract class BaseActivity extends AppCompatActivity {

    private View mProgressBar;
    private InputMethodManager mInputManager;

    @LayoutRes
    protected abstract int getLayoutRes();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInputManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (getLayoutRes() != 0) {
            setContentView(getLayoutRes());
            bindViews();
        }
    }

    public void showLoading() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(VISIBLE);
        }
    }

    public void hideLoading() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(GONE);
        }
    }

    public void showError(Throwable throwable) {
    }

    protected void bindViews() {
        mProgressBar = findViewById(R.id.loadingView);
    }

    protected void hideKeyboard() {
        final View view = getCurrentFocus();
        if (view != null) {
            mInputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
