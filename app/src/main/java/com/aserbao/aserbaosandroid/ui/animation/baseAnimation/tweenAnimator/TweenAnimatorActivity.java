package com.aserbao.aserbaosandroid.ui.animation.baseAnimation.tweenAnimator;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aserbao.aserbaosandroid.R;
import com.aserbao.aserbaosandroid.aa.BaserRecyclerViewManager;
import com.aserbao.aserbaosandroid.comon.base.BaseRecyclerViewActivity;
import com.aserbao.aserbaosandroid.comon.base.beans.BaseRecyclerBean;
import com.aserbao.aserbaosandroid.comon.base.interfaces.IBaseRecyclerItemClickListener;
import com.aserbao.aserbaosandroid.comon.commonData.ASourceUtil;

public class TweenAnimatorActivity extends BaseRecyclerViewActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initGetData() {
        mBaseRecyclerBeen.add(new BaseRecyclerBean("选择插值器",101));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("Translate",0));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("Scale",1));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("Rotate",2));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("Alpha",3));

        mBaseRecyclerEmptyContainer.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.emoji_00);
        imageView.setLayoutParams(layoutParams);
        mBaseRecyclerEmptyContainer.addView(imageView);

        mCanvasPointView.setVisibility(View.VISIBLE);
        mBaseRecyclerTv.setText("TweenAnimation");
    }


    Interpolator interpolator;
    @Override
    public void itemClickBack(View view, int position) {
        switch (position){
            case 101:
                BaserRecyclerViewManager aPopupManager = new BaserRecyclerViewManager(this, this);
                ASourceUtil.getAllInterpoator(aPopupManager.mBaseRecyclerBeen);
                aPopupManager.showBottomRecyclerViewPop(new IBaseRecyclerItemClickListener() {
                    @Override
                    public void itemClickBack(View view, int position) {
                        interpolator = ASourceUtil.getCuurSelectedInterpolator(position);
                        if (aPopupManager != null) {
                            aPopupManager.dismiss();
                        }
                    }
                });
                break;
            case 0:
                userTranslateAnimation();
                break;
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
        }
    }


    public void userTranslateAnimation(){
        float x = mBaseRecyclerTv.getX();
        float y = mBaseRecyclerTv.getY();
        Point point = new Point((int)x,(int)y);
        mCanvasPointView.addPoint(point);

        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0,0,200);
        translateAnimation.setInterpolator(new LinearInterpolator());
        translateAnimation.setRepeatCount(-1);
        translateAnimation.setRepeatMode(Animation.REVERSE);
        translateAnimation.setDuration(500);
        mBaseRecyclerTv.setAnimation(translateAnimation);
    }
}
