package com.oskalenko.kotakurss.ui.view;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.oskalenko.kotakurss.R;
import com.oskalenko.kotakurss.interfaces.ItemClickView;
import com.oskalenko.kotakurss.interfaces.OnClickViewListener;
import com.oskalenko.kotakurss.interfaces.ViewImageModel;
import com.oskalenko.kotakurss.interfaces.ViewModel;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 04.07.2016
 * Time: 20:56
 */

public abstract class BaseItemView<T> extends RelativeLayout implements ViewModel<T>, ViewImageModel, ItemClickView {

    @Nullable
    private ImageView mImageView;
    @Nullable
    private View mProgressBar;

    protected int mPosition;
    protected OnClickViewListener mClickListener;

    public BaseItemView(Context context) {
        super(context);

        if (getLayout() != 0) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(getLayout(), this);

            bindViews(view);
        }
    }

    private void bindViews(View view) {
        if (view != null) {
            mImageView = ((ImageView) view.findViewById(R.id.image_view));
            mProgressBar = view.findViewById(R.id.progress_bar);
        }
    }

    @LayoutRes
    protected abstract int getLayout();

    @Override
    public void setPosition(int position) {
        mPosition = position;
    }

    @Override
    public void setClickListener(OnClickViewListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public ImageView getImageView() {
        return mImageView;
    }

    @Override
    public void hideProgress() {
        setProgressVisibility(GONE);
    }

    @Override
    public void showProgress() {
        setProgressVisibility(VISIBLE);
    }

    private void setProgressVisibility(int visibility) {
        if (mProgressBar != null) {
            if (mImageView != null && mImageView.getVisibility() == View.VISIBLE) {
                mProgressBar.setVisibility(visibility);
            }
        }
    }
}
