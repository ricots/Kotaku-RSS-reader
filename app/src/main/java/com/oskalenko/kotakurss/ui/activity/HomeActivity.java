package com.oskalenko.kotakurss.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.oskalenko.kotakurss.R;
import com.oskalenko.kotakurss.common.ActivityUtils;
import com.oskalenko.kotakurss.data.FeedsRepository;
import com.oskalenko.kotakurss.data.Injection;
import com.oskalenko.kotakurss.data.LoaderProvider;
import com.oskalenko.kotakurss.ui.fragment.FeedsFragment;
import com.oskalenko.kotakurss.ui.presenter.FeedsPresenter;
import com.oskalenko.kotakurss.ui.presenter.view.FeedsContract;

public class HomeActivity extends BaseActivity {

    private static final String TAG = HomeActivity.class.getName();

    private Toolbar mToolbar;
    private FeedsPresenter mFeedsPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initToolBar();
        startFeedsScreen();
    }

    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mToolbar.setTitle(R.string.toolbarTitle);
        setSupportActionBar(mToolbar);
    }

    private void startFeedsScreen() {
        // Create the fragment
        FeedsFragment feedsFragment = FeedsFragment.newInstance();
        ActivityUtils.addFragmentToActivity(
                getSupportFragmentManager(), feedsFragment, R.id.main_container);

        initPresenter(feedsFragment);
    }

    private void initPresenter(FeedsContract.View view) {
        LoaderProvider loaderProvider = new LoaderProvider(this);
        mFeedsPresenter = new FeedsPresenter(
                loaderProvider,
                getSupportLoaderManager(),
                Injection.provideTasksRepository(getApplicationContext()),
                view);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_home;
    }
}
