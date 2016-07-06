package com.oskalenko.kotakurss.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.oskalenko.kotakurss.R;
import com.oskalenko.kotakurss.common.ActivityUtils;
import com.oskalenko.kotakurss.common.Utils;
import com.oskalenko.kotakurss.data.Injection;
import com.oskalenko.kotakurss.data.LoaderProvider;
import com.oskalenko.kotakurss.exception.NetworkConnectionException;
import com.oskalenko.kotakurss.exception.ServerErrorException;
import com.oskalenko.kotakurss.ui.dialog.InformationDialog;
import com.oskalenko.kotakurss.ui.dialog.InformationDialog.DialogResult;
import com.oskalenko.kotakurss.ui.dialog.InformationDialog.OnDialogResult;
import com.oskalenko.kotakurss.ui.fragment.FeedDescriptionFragment;
import com.oskalenko.kotakurss.ui.fragment.FeedsFragment;
import com.oskalenko.kotakurss.ui.presenter.FeedsPresenter;
import com.oskalenko.kotakurss.ui.presenter.view.FeedsContract;

import java.net.UnknownHostException;
import java.util.List;

import static com.oskalenko.kotakurss.ui.dialog.InformationDialog.DialogResult.OK;
import static com.oskalenko.kotakurss.ui.dialog.InformationDialog.DialogType.ONE_BUTTON_MODE;
import static com.oskalenko.kotakurss.ui.dialog.InformationDialog.DialogType.TWO_BUTTON_MODE;

public class HomeActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener {

    private static final String TAG = HomeActivity.class.getName();
    public static final int REQUEST_ENABLE_CONNECTION = 100;

    private FeedsPresenter mFeedsPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        startFeedsScreen();
        if (!Utils.isNetworkAvailable(getApplicationContext())) {
            showError(new NetworkConnectionException());
        }
    }

    @Override
    public void onBackStackChanged() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(
                getSupportFragmentManager().getBackStackEntryCount() >= 1);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_home;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setToolBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void startFeedsScreen() {
        FeedsFragment feedsFragment = FeedsFragment.newInstance();
        ActivityUtils.addFragmentToActivity(
                getSupportFragmentManager(), feedsFragment, R.id.main_container, false);

        initPresenter(feedsFragment);
    }

    public void startFeedDescriptionScreen(String link) {
        FeedDescriptionFragment feedDescriptionFragment = FeedDescriptionFragment.newInstance(link);
        ActivityUtils.addFragmentToActivity(
                getSupportFragmentManager(), feedDescriptionFragment, R.id.main_container, true);
    }

    private void initPresenter(FeedsContract.View view) {
        mFeedsPresenter = new FeedsPresenter(
                Injection.provideLoaderProvider(getApplicationContext()),
                getSupportLoaderManager(),
                Injection.provideTasksRepository(getApplicationContext()),
                view, Injection.providePrefManager(getApplicationContext()));
    }

    @Override
    public void showError(Throwable throwable) {
        if (InformationDialog.isShown()) {
            return;
        }

        InformationDialog dialog = setupErrorDialog(throwable);

        if (dialog != null) {
            InformationDialog.setShown(true);
            dialog.show(getSupportFragmentManager(), null);
        }
    }

    private InformationDialog setupErrorDialog(Throwable throwable) {
        InformationDialog.DialogType dialogMode = ONE_BUTTON_MODE;
        String dialogTitle = getString(R.string.information_dialog_exception_title);
        String errorMessage = null;
        OnDialogResult dialogResult = null;

        if (throwable instanceof NetworkConnectionException) {

            dialogMode = TWO_BUTTON_MODE;
            errorMessage = getString(R.string.wifi_network_exception_message);
            dialogResult = createNetworkExceptionHandler();

        } else if (throwable instanceof ServerErrorException ||
                throwable instanceof java.net.SocketTimeoutException) {

            errorMessage = getString(R.string.server_error_exception_message);

        } else if (throwable instanceof RuntimeException) {
            Log.e(TAG, throwable.getMessage());
            return null;
        }

        InformationDialog errorDialog = InformationDialog.newInstance(dialogTitle, errorMessage, dialogMode);
        errorDialog.setOnDialogResult(dialogResult);

        return errorDialog;
    }

    private OnDialogResult createNetworkExceptionHandler() {
        return new OnDialogResult() {
            @Override
            public void onDialogResult(DialogResult dialogResult, Object... result) {
                if (dialogResult == OK) {
                    startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), REQUEST_ENABLE_CONNECTION);
                }
            }
        };
    }
}
