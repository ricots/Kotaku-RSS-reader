package com.oskalenko.kotakurss.ui.presenter.view;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.oskalenko.kotakurss.data.model.Feed;
import com.oskalenko.kotakurss.ui.presenter.BasePresenter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 05.07.2016
 * Time: 10:26
 * This specifies the contract between the view and the presenter.
 */
public interface FeedsContract {

    interface View extends BaseView<Presenter> {

        void showFeeds(List<Feed> feeds);
    }

    interface Presenter extends BasePresenter {

        void loadFeeds(boolean pullToRefresh);

        void openFeedDetails(@NonNull Feed feed);
    }
}
