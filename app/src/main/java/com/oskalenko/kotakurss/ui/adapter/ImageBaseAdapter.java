package com.oskalenko.kotakurss.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.oskalenko.kotakurss.R;
import com.oskalenko.kotakurss.interfaces.ItemClickView;
import com.oskalenko.kotakurss.interfaces.ViewImageModel;
import com.oskalenko.kotakurss.interfaces.ViewModel;
import com.oskalenko.kotakurss.manager.ImageManager;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 04.07.2016
 * Time: 21:07
 */

public abstract class ImageBaseAdapter<T1, T2 extends View & ViewModel & ItemClickView & ViewImageModel>
        extends BaseAdapter<T1, T2, BaseViewHolder<T2>> {

    private ImageManager mImageManager;

    protected abstract String getImageUrl(int position);

    public ImageBaseAdapter(ImageManager imageManager) {
       mImageManager = imageManager;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<T2> holder, int position) {

        final String imageUrl = getImageUrl(position);
        final T2 cardView = holder.getView();
        final ImageView imageView = cardView.getImageView();

        if (imageView != null && !TextUtils.isEmpty(imageUrl)) {
            cardView.showProgress();

            mImageManager.loadImage(imageUrl, imageView, getNoPhotoDrawable(), new ImageManager.Callback() {
                @Override
                public void onSuccess() {
                    cardView.hideProgress();
                }

                @Override
                public void onError() {
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    cardView.hideProgress();
                }
            });
        }

        super.onBindViewHolder(holder, position);
    }

    protected int getNoPhotoDrawable() {
        return R.drawable.ic_no_photo;
    }
}
