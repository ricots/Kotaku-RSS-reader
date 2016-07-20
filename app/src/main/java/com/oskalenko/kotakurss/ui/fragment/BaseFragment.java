package com.oskalenko.kotakurss.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oskalenko.kotakurss.ui.activity.BaseActivity;
import com.oskalenko.kotakurss.ui.presenter.BasePresenter;
import com.oskalenko.kotakurss.ui.presenter.view.FeedsContract;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 04.07.2016
 * Time: 21:12
 */
public abstract class BaseFragment extends Fragment {

    protected abstract void bindViews(View view);

    @LayoutRes
    protected abstract int getLayoutRes();

    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        if (getLayoutRes() == 0) {
            throw new IllegalArgumentException(
                    "getLayoutRes() returned 0, which is not valid resource is.");
        } else {
            return inflater.inflate(getLayoutRes(), container, false);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getPresenter() != null) {
            getPresenter().start();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getPresenter().result(requestCode, resultCode);
    }

    public void showError(Throwable throwable) {
        getBaseActivity().showError(throwable);
    }

    public void setLoading(boolean active) {
        if (active) {
            getBaseActivity().showLoading();
        } else {
            getBaseActivity().hideLoading();
        }
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
