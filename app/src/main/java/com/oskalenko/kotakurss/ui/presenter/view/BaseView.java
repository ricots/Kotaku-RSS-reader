package com.oskalenko.kotakurss.ui.presenter.view;

public interface BaseView<T> {

    void setPresenter(T presenter);

    void setLoading(boolean active);

    void showError(Throwable throwable);
}
