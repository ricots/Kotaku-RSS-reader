package com.oskalenko.kotakurss.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.oskalenko.kotakurss.R;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 06.07.2016
 * Time: 10:07
 */
public class FeedDescriptionFragment extends BaseFragment {

    public static final String ARG_CONTENT_LINK = "arg_content_link";

    private WebView mDescriptionWebView;

    public static FeedDescriptionFragment newInstance(String link) {
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT_LINK, link);

        FeedDescriptionFragment fragment = new FeedDescriptionFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        String link = arguments.getString(ARG_CONTENT_LINK);

        openLink(link);
    }

    @Override
    protected void bindViews(View view) {
        mDescriptionWebView = (WebView) view.findViewById(R.id.fragment_feed_description_web_view);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_feed_description;
    }

    private void openLink(String link) {
        mDescriptionWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                setLoading(false);
            }
        });

        setLoading(true);
        mDescriptionWebView.loadUrl(link);
    }
}
