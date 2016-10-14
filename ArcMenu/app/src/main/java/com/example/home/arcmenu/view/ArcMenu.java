package com.example.home.arcmenu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.example.home.arcmenu.R;


/**
 * 菜单
 * Created by xuguohong on 16/10/13.
 */
public class ArcMenu extends ViewGroup implements View.OnClickListener {

    private MenuItemClickListener mMenuItemClickListener;

    private final int LEFT_TOP = 0;
    private final int LEFT_CENTER = 1;
    private final int LEFT_BUTTOM = 2;
    private final int RIGHT_TOP = 3;
    private final int RIGHT_CENTER = 4;
    private final int RIGHT_BUTTOM = 5;


    private boolean isMenuClose = false;
    private boolean isStartAnimation = false;
    private Position position = Position.LEFT_CENTER;

    private int radius = 100;

    private View mMainMenu;

    public enum Position {
        LEFT_TOP, LEFT_CENTER, LEFT_BUTTOM, RIGHT_TOP, RIGHT_CENTER, RIGHT_BUTTOM
    }


    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcMenu);

        int pos = typedArray.getInt(R.styleable.ArcMenu_position, 0);

        switch (pos) {
            case LEFT_TOP:
                position = Position.LEFT_TOP;
                break;

            case LEFT_CENTER:
                position = Position.LEFT_CENTER;
                break;

            case LEFT_BUTTOM:
                position = Position.LEFT_BUTTOM;
                break;

            case RIGHT_TOP:
                position = Position.RIGHT_TOP;
                break;

            case RIGHT_CENTER:
                position = Position.RIGHT_CENTER;
                break;

            case RIGHT_BUTTOM:
                position = Position.RIGHT_BUTTOM;
                break;
        }

        radius = typedArray.getDimensionPixelOffset(R.styleable.ArcMenu_radius, 100);

        typedArray.recycle();
    }


    private void layoutMainMenu(View mainMenuView) {

        mMainMenu = mainMenuView;
        mMainMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMenuClose){
                    if(!isStartAnimation){
                        openAnimation();
                    }
                }else{
                    if(!isStartAnimation)
                    {
                        closeAnimation();
                    }
                }
            }
        });

        int widht = getMeasuredWidth();
        int height = getMeasuredHeight();
        int menuWidth = mainMenuView.getMeasuredWidth();
        int menuHeight = mainMenuView.getMeasuredHeight();

        int l = 0;
        int t = 0;

        switch (position) {
            case LEFT_TOP:
                break;
            case LEFT_CENTER:
                t = height / 2 - menuHeight / 2;
                break;
            case LEFT_BUTTOM:
                t = height - menuHeight;
                break;
            case RIGHT_TOP:
                l = widht - menuWidth;
                break;
            case RIGHT_CENTER:
                l = widht - menuWidth;
                t = height / 2 - menuHeight / 2;
                break;
            case RIGHT_BUTTOM:
                l = widht - menuWidth;
                t = height - menuHeight;
                break;
        }
        int b = t + menuHeight;
        int r = l + menuWidth;
        mainMenuView.layout(l, t, r, b);
    }


    private void layoutSubMenu() {
        int subMenuCount = getChildCount();

        if (subMenuCount >= 1) {
            View mainMenuView = getChildAt(0);
            int left = mainMenuView.getLeft();
            int top = mainMenuView.getTop();
            int mainWidth = mainMenuView.getMeasuredWidth();
            int mainHeight = mainMenuView.getMeasuredHeight();

            //角度间隔
            float a = 90 / (subMenuCount - 2);
            if (position == Position.RIGHT_CENTER || position == Position.LEFT_CENTER) {
                a = 180 / (subMenuCount - 2);
            }


            for (int i = 1; i < subMenuCount; i++) {

                int circleX = (int) (left + mainWidth / 2 + radius * Math.cos(Math.toRadians(a * (i - 1))));
                int circleY = (int) (top + mainHeight / 2 + radius * Math.sin(Math.toRadians(a * (i - 1))));


                switch (position) {
                    case LEFT_TOP:
                        break;
                    case LEFT_CENTER:
                        circleX = (int) (left + mainWidth / 2 + radius * Math.abs(Math.cos(Math.toRadians(90 - a * (i - 1)))));
                        circleY = (int) (top + mainHeight / 2 - radius * Math.sin(Math.toRadians(90 - a * (i - 1))));
                        break;
                    case LEFT_BUTTOM:
                        circleY = (int) (top + mainHeight / 2 - radius * Math.sin(Math.toRadians(a * (i - 1))));
                        break;
                    case RIGHT_TOP:
                        circleX = (int) (left + mainWidth / 2 - radius * Math.cos(Math.toRadians(a * (i - 1))));
                        break;
                    case RIGHT_CENTER:
                        circleX = (int) (left + mainWidth / 2 - radius * Math.abs(Math.cos(Math.toRadians(90 - a * (i - 1)))));
                        circleY = (int) (top + mainHeight / 2 - radius * Math.sin(Math.toRadians(90 - a * (i - 1))));

                        break;
                    case RIGHT_BUTTOM:
                        circleX = (int) (left + mainWidth / 2 - radius * Math.cos(Math.toRadians(a * (i - 1))));
                        circleY = (int) (top + mainHeight / 2 - radius * Math.sin(Math.toRadians(a * (i - 1))));
                        break;
                }
                View subMenuView = getChildAt(i);
                int subWidth = subMenuView.getMeasuredWidth();
                int subHeight = subMenuView.getMeasuredHeight();
                subMenuView.layout(circleX - subWidth / 2, circleY - subHeight / 2, circleX + subWidth / 2, circleY + subHeight / 2);
            }
        }
    }


    public void setOnMuenItemClickListener(MenuItemClickListener menuItemClickListener)
    {
        mMenuItemClickListener = menuItemClickListener;
        int count = getChildCount();

        for (int i = 1;i < count ; i++){
            View subView = getChildAt(i);
            subView.setOnClickListener(this);
            subView.setTag(i - 1);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        Log.e("ArcMenu", "count:" + count);
        for (int i = 0; i < count; i++) {
            //计算child的宽高
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int count = getChildCount();
        if (count > 0) {
            layoutMainMenu(getChildAt(0));
        }
        layoutSubMenu();
    }

    private void openAnimation(){
        int count = getChildCount();

        AnimationSet mainAnimationSet = new AnimationSet(true);
        RotateAnimation mainRotateAnimation = new RotateAnimation(0,45,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        mainRotateAnimation.setDuration(450);
        mainAnimationSet.addAnimation(mainRotateAnimation);
        mainAnimationSet.setFillAfter(true);
        mMainMenu.startAnimation(mainRotateAnimation);

        for (int i = 1;i < count;i++)
        {
            View targetView = getChildAt(i);
            AnimationSet animationSet = new AnimationSet(true);
            RotateAnimation rotateAnimation = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(150);
            rotateAnimation.setRepeatCount(3);
            animationSet.addAnimation(rotateAnimation);

            TranslateAnimation translateAnimation = new TranslateAnimation(mMainMenu.getX() - targetView.getX(),0,mMainMenu.getY() - targetView.getY(),0);
            translateAnimation.setDuration(450);
            translateAnimation.setFillAfter(true);
            animationSet.addAnimation(translateAnimation);

            AlphaAnimation alphaAnimation = new AlphaAnimation(0f,1f);
            alphaAnimation.setDuration(450);
            animationSet.addAnimation(alphaAnimation);

            animationSet.setFillAfter(true);
            targetView.startAnimation(animationSet);
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isStartAnimation = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isStartAnimation = false;
                    isMenuClose = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    private void closeAnimation()
    {
        int count = getChildCount();

        AnimationSet mainAnimationSet = new AnimationSet(true);
        RotateAnimation mainRotateAnimation = new RotateAnimation(45,0,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        mainRotateAnimation.setDuration(450);
        mainAnimationSet.addAnimation(mainRotateAnimation);
        mainAnimationSet.setFillAfter(true);
        mMainMenu.startAnimation(mainRotateAnimation);

        for (int i = 1;i < count;i++)
        {
            View targetView = getChildAt(i);
            AnimationSet animationSet = new AnimationSet(true);
            RotateAnimation rotateAnimation = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(150);
            rotateAnimation.setRepeatCount(3);
            animationSet.addAnimation(rotateAnimation);

            TranslateAnimation translateAnimation = new TranslateAnimation(0,mMainMenu.getX() - targetView.getX(),0,mMainMenu.getY() - targetView.getY());
            translateAnimation.setDuration(450);
            translateAnimation.setFillAfter(true);
            animationSet.addAnimation(translateAnimation);

            AlphaAnimation alphaAnimation = new AlphaAnimation(1f,0f);
            alphaAnimation.setDuration(450);
            animationSet.addAnimation(alphaAnimation);

            animationSet.setFillAfter(true);
            targetView.startAnimation(animationSet);
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isStartAnimation = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isStartAnimation = false;
                    isMenuClose = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        Log.e("onClick","onclick");
        if(mMenuItemClickListener != null){
            mMenuItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    public  interface  MenuItemClickListener
    {
        void onItemClick(View view,int position);
    }
}
