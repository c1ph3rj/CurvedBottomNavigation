package tech.c1ph3rj.curvedbottomnavigation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

public class CircleNavigationCell extends RelativeLayout {

    public interface OnClickListener {
        void onClick();
    }

    public static final String EMPTY_VALUE = "empty";
    private ImageView iv;
    private TextView tvCount;
    private TextView labelView;
    private FrameLayout fl;
    private View vCircle;

    private int defaultIconColor = 0;
    private int selectedIconColor = 0;
    private int circleColor = 0;
    private int icon = 0;
    private String count = EMPTY_VALUE;
    private float iconSize = dp(getContext(), 48);
    private int countTextColor = 0;
    private int labelTextColor = 0;
    private String label;
    private int countBackgroundColor = 0;
    private Typeface countTypeface;
    private int rippleColor = 0;
    private boolean isFromLeft;
    private long duration;
    private float progress;
    private boolean isEnabledCell;
    private OnClickListener onClickListener;
    private boolean allowDraw;

    public CircleNavigationCell(Context context) {
        this(context, null);
    }

    public CircleNavigationCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleNavigationCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.navigation_cell, this, true);

        iv = findViewById(R.id.iv);
        tvCount = findViewById(R.id.tv_count);
        fl = findViewById(R.id.fl);
        vCircle = findViewById(R.id.v_circle);
        labelView = findViewById(R.id.labelView);

        allowDraw = true;
        draw();
    }

    private void draw() {
        if (!allowDraw) {
            return;
        }

        setIcon(icon);
        setLabel(label);
        setCount(count);
        setIconSize(iconSize);
        setCountTextColor(countTextColor);
        setCountBackgroundColor(countBackgroundColor);
        setCountTypeface(countTypeface);
        setRippleColor(rippleColor);
        setOnClickListener(onClickListener);
    }

    public void disableCell(boolean isAnimate) {
        if (isEnabledCell) {
            animateProgress(false, isAnimate);
        }
        isEnabledCell = false;
    }

    public void enableCell(boolean isAnimate) {
        if (!isEnabledCell) {
            animateProgress(true, isAnimate);
        }
        isEnabledCell = true;
    }

    private void animateProgress(final boolean enableCell, boolean isAnimate) {
        long d = enableCell ? duration : 250;
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setStartDelay(enableCell ? d / 4 : 0);
        anim.setDuration(isAnimate ? d : 1L);
        anim.setInterpolator(new FastOutSlowInInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                float f = animation.getAnimatedFraction();
                CircleNavigationCell.this.setProgress(enableCell ? f : 1f - f);
            }
        });
        anim.start();
    }

    public void setDefaultIconColor(int defaultIconColor) {
        this.defaultIconColor = defaultIconColor;
        if (allowDraw) {
            ImageViewCompat.setImageTintList(
                    iv,
                    ofColorStateList(!isEnabledCell ? defaultIconColor : selectedIconColor)
            );
        }
    }

    public void setSelectedIconColor(int selectedIconColor) {
        this.selectedIconColor = selectedIconColor;
        if (allowDraw) {
            ImageViewCompat.setImageTintList(
                    iv,
                    ofColorStateList(!isEnabledCell ? defaultIconColor : selectedIconColor)
            );
        }
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        if (allowDraw) {
            isEnabledCell = isEnabledCell;
        }
    }

    public void setLabel(String label) {
        this.label = label;
        if(allowDraw) {
            labelView.setText(label);
        }
    }

    public void setIcon(int icon) {
        this.icon = icon;
        if (allowDraw) {
            iv.setImageResource(icon);
        }
    }

    public void setLabelTextColor(int labelTextColor) {
        this.labelTextColor = labelTextColor;
        if (allowDraw) {
            labelView.setTextColor(labelTextColor);
        }
    }

    public void setCount(String count) {
        this.count = count;
        if (allowDraw) {
            if (count != null && count.equals(EMPTY_VALUE)) {
                tvCount.setText("");
                tvCount.setVisibility(View.INVISIBLE);
            } else {
                if (count != null && count.length() >= 3) {
                    this.count = count.substring(0, 1) + "..";
                }
                tvCount.setText(count);
                tvCount.setVisibility(View.VISIBLE);
                float scale = count != null && count.isEmpty() ? 0.5f : 1f;
                tvCount.setScaleX(scale);
                tvCount.setScaleY(scale);
            }
        }
    }

    public void setIconSize(float iconSize) {
        this.iconSize = iconSize;
        if (allowDraw) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) iv.getLayoutParams();
            layoutParams.width = (int) iconSize;
            layoutParams.height = (int) iconSize;
            iv.setLayoutParams(layoutParams);
            iv.setPivotX(iconSize / 2f);
            iv.setPivotY(iconSize / 2f);
        }
    }

    public void setCountTextColor(int countTextColor) {
        this.countTextColor = countTextColor;
        if (allowDraw) {
            tvCount.setTextColor(countTextColor);
        }
    }

    public void setCountBackgroundColor(int countBackgroundColor) {
        this.countBackgroundColor = countBackgroundColor;
        if (allowDraw) {
            GradientDrawable d = new GradientDrawable();
            d.setColor(countBackgroundColor);
            d.setShape(GradientDrawable.OVAL);
            ViewCompat.setBackground(tvCount, d);
        }
    }

    public void setCountTypeface(@Nullable Typeface countTypeface) {
        this.countTypeface = countTypeface;
        if (allowDraw && countTypeface != null) {
            tvCount.setTypeface(countTypeface);
        }
    }

    public void setRippleColor(int rippleColor) {
        this.rippleColor = rippleColor;
        if (allowDraw) {
            isEnabledCell = isEnabledCell;
        }
    }

    public void setFromLeft(boolean fromLeft) {
        isFromLeft = fromLeft;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        fl.setY((1f - progress) * dp(getContext(), 18) - dp(getContext(), 3));
        ImageViewCompat.setImageTintList(
                iv,
                ofColorStateList(progress == 1f ? selectedIconColor : defaultIconColor)
        );
        float scale = (1f - progress) * -0.2f + 1f;
        iv.setScaleX(scale);
        iv.setScaleY(scale);

        GradientDrawable d = new GradientDrawable();
        d.setColor(circleColor);
        d.setShape(GradientDrawable.OVAL);
        ViewCompat.setBackground(vCircle, d);

        ViewCompat.setElevation(
                vCircle, progress > 0.7f ? (progress * 4f) : 0f
        );

        float m = dp(getContext(), 24);
        vCircle.setX(
                (1f - progress) * (isFromLeft ? -m : m) + ((getMeasuredWidth() - dp(getContext(), 48)) / 2f)
        );
        vCircle.setY((1f - progress) * getMeasuredHeight() + dp(getContext(), 6));
    }

    public void setOnClickListener(@NonNull final OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick();
            }
        });
    }


    public void allowDraw(boolean allowDraw) {
        this.allowDraw = allowDraw;
        draw();
    }

    private static float dp(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return dp * density;
    }

    private static ColorStateList ofColorStateList(int color) {
        return ColorStateList.valueOf(color);
    }

    private void runAfterDelay(long delayMillis, Runnable action) {
        new Handler(Looper.getMainLooper()).postDelayed(action, delayMillis);
    }
}
