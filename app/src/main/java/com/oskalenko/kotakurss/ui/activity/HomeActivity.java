package com.oskalenko.kotakurss.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.oskalenko.kotakurss.R;
import com.oskalenko.kotakurss.common.ActivityUtils;
import com.oskalenko.kotakurss.data.Injection;
import com.oskalenko.kotakurss.data.LoaderProvider;
import com.oskalenko.kotakurss.ui.fragment.FeedDescriptionFragment;
import com.oskalenko.kotakurss.ui.fragment.FeedsFragment;
import com.oskalenko.kotakurss.ui.presenter.FeedsPresenter;
import com.oskalenko.kotakurss.ui.presenter.view.FeedsContract;

public class HomeActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener {

    private static final String TAG = HomeActivity.class.getName();

    private FeedsPresenter mFeedsPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        startFeedsScreen();
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
        LoaderProvider loaderProvider = new LoaderProvider(this);
        mFeedsPresenter = new FeedsPresenter(
                loaderProvider,
                getSupportLoaderManager(),
                Injection.provideTasksRepository(getApplicationContext()),
                view);
    }
}
