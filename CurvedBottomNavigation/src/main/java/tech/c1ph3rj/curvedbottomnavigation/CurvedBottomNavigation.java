package tech.c1ph3rj.curvedbottomnavigation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class CurvedBottomNavigation extends FrameLayout {

    private int shadowColor;

    public interface IBottomNavigationListener {
        void currentModel(Model model);
    }

    public static class Model {
        public int id;
        public int icon;
        public String label;
        public String count = CircleNavigationCell.EMPTY_VALUE;

        public Model(int id, int icon) {
            this.id = id;
            this.icon = icon;
        }

        public Model(int id, int icon, String label) {
            this.id = id;
            this.icon = icon;
            this.label = label;
        }
    }

    private final ArrayList<Model> models = new ArrayList<>();
    private final ArrayList<CircleNavigationCell> cells = new ArrayList<>();
    private IBottomNavigationListener onClickedListener = new IBottomNavigationListener() {
        @Override
        public void currentModel(Model model) {
        }
    };
    private IBottomNavigationListener onShowListener = new IBottomNavigationListener() {
        @Override
        public void currentModel(Model model) {
        }
    };
    private IBottomNavigationListener onReselectListener = new IBottomNavigationListener() {
        @Override
        public void currentModel(Model model) {
        }
    };

    private int heightCell = 0;
    private boolean isAnimating = false;
    private int selectedId = -1;
    private final boolean callListenerWhenIsSelected = false;

    private LinearLayout ll_cells;
    private BezierView bezierView;

    private int defaultIconColor = Color.parseColor("#757575");
    private int selectedIconColor = Color.parseColor("#2196f3");
    private int backgroundBottomColor = Color.parseColor("#ffffff");
    private int circleColor = Color.parseColor("#ffffff");
    private int countTextColor = Color.parseColor("#ffffff");
    private int countBackgroundColor = Color.parseColor("#ff0000");
    private Typeface countTypeface = null;
    private boolean hasAnimation = true;
    private int labelTextColor = Color.parseColor("#4F709C");
    private int rippleColor = Color.parseColor("#757575");

    public CurvedBottomNavigation(Context context) {
        super(context);
        initializeViews();
    }

    public CurvedBottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributeFromXml(context, attrs);
        initializeViews();
    }

    public CurvedBottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttributeFromXml(context, attrs);
        initializeViews();
    }

    private void setAttributeFromXml(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomBottomNavigation);

        defaultIconColor = a.getColor(R.styleable.CustomBottomNavigation_mbn_defaultIconColor, defaultIconColor);
        selectedIconColor = a.getColor(R.styleable.CustomBottomNavigation_mbn_selectedIconColor, selectedIconColor);
        backgroundBottomColor = a.getColor(R.styleable.CustomBottomNavigation_mbn_backgroundBottomColor, backgroundBottomColor);
        circleColor = a.getColor(R.styleable.CustomBottomNavigation_mbn_circleColor, circleColor);
        countTextColor = a.getColor(R.styleable.CustomBottomNavigation_mbn_countTextColor, countTextColor);
        countBackgroundColor = a.getColor(R.styleable.CustomBottomNavigation_mbn_countBackgroundColor, countBackgroundColor);
        rippleColor = a.getColor(R.styleable.CustomBottomNavigation_mbn_rippleColor, rippleColor);
        shadowColor = a.getColor(R.styleable.CustomBottomNavigation_mbn_shadowColor, shadowColor);
        hasAnimation = a.getBoolean(R.styleable.CustomBottomNavigation_mbn_hasAnimation, hasAnimation);
        labelTextColor = a.getColor(R.styleable.CustomBottomNavigation_mbn_labelTextColor, labelTextColor);
        String typefacePath = a.getString(R.styleable.CustomBottomNavigation_mbn_countTypeface);
        if (typefacePath != null) {
            countTypeface = Typeface.createFromAsset(context.getAssets(), typefacePath);
        }

        a.recycle();
    }


    private void initializeViews() {
        heightCell = dp(getContext(), 96);

        ll_cells = new LinearLayout(getContext());
        ll_cells.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, heightCell));
        ll_cells.setOrientation(LinearLayout.HORIZONTAL);
        ll_cells.setClipChildren(false);
        ll_cells.setClipToPadding(false);

        bezierView = new BezierView(getContext());
        bezierView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, heightCell));
        bezierView.setColor(backgroundBottomColor);
        shadowColor = -0x454546;
        bezierView.setShadowColor(shadowColor);

        addView(bezierView);
        addView(ll_cells);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (selectedId == -1) {
            bezierView.setBezierX(getLayoutDirection() == View.LAYOUT_DIRECTION_RTL
                    ? getMeasuredWidth() + dp(getContext(), 72) : -dp(getContext(), 72));
        }
        if (selectedId != -1) {
            show(selectedId);
        }
    }

    public void add(final Model model) {
        final CircleNavigationCell cell = new CircleNavigationCell(getContext());
        cell.setLayoutParams(new LinearLayout.LayoutParams(0, heightCell, 1f));
        cell.setIcon(model.icon);
        cell.setCount(model.count);
        cell.setDefaultIconColor(defaultIconColor);
        cell.setSelectedIconColor(selectedIconColor);
        cell.setCircleColor(circleColor);
        cell.setCountTextColor(countTextColor);
        cell.setCountBackgroundColor(countBackgroundColor);
        cell.setCountTypeface(countTypeface);
        cell.setRippleColor(rippleColor);
        cell.setLabelTextColor(labelTextColor);
        if(model.label != null && !model.label.trim().isEmpty()) {
            cell.setLabel(model.label);
        }
        cell.allowDraw(true);
        cell.setOnClickListener(new CircleNavigationCell.OnClickListener() {
            @Override
            public void onClick() {
                if (CurvedBottomNavigation.this.isShowing(model.id)) {
                    onReselectListener.currentModel(model);
                }

                if (cell.isEnabled() && !isAnimating) {
                    CurvedBottomNavigation.this.show(model.id);
                    onClickedListener.currentModel(model);
                } else {
                    if (callListenerWhenIsSelected) {
                        onClickedListener.currentModel(model);
                    }
                }
            }
        });
        cell.disableCell(hasAnimation);
        ll_cells.addView(cell);

        cells.add(cell);
        models.add(model);
    }


    private void anim(final CircleNavigationCell cell, int id) {
        isAnimating = true;

        int pos = getModelPosition(id);
        int nowPos = getModelPosition(selectedId);

        int nPos = Math.max(nowPos, 0);
        int dif = Math.abs(pos - nPos);
        long d = dif * 100L + 240L;

        long animDuration = hasAnimation ? d : 1L;

        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(animDuration);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        final float beforeX = bezierView.bezierX;  // Use the getter method
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                float f = animation.getAnimatedFraction();
                float newX = cell.getX() + ((float) cell.getMeasuredWidth() / 2);
                if (newX > beforeX) {
                    bezierView.setBezierX(f * (newX - beforeX) + beforeX);  // Use the setter method
                } else {
                    bezierView.setBezierX(beforeX - f * (beforeX - newX));  // Use the setter method
                }
                if (f == 1f) {
                    isAnimating = false;
                }
            }
        });
        anim.start();


        if (pos != nowPos) {
            ValueAnimator progressAnim = ValueAnimator.ofFloat(0f, 1f);
            progressAnim.setDuration(animDuration);
            progressAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            progressAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                    bezierView.setProgress(animation.getAnimatedFraction() * 2f);
                }
            });
            progressAnim.start();
        }

        cell.setFromLeft(pos > nowPos);
        for (CircleNavigationCell c : cells) {
            c.setDuration(d);
        }

    }

    public void show(int id) {
        for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            CircleNavigationCell cell = cells.get(i);
            if (model.id == id) {
                anim(cell, id);
                cell.enableCell(true);
                onShowListener.currentModel(model);
            } else {
                cell.disableCell(hasAnimation);
            }
        }
        selectedId = id;
    }

    public boolean isShowing(int id) {
        return selectedId == id;
    }

    public Model getModelById(int id) {
        for (Model model : models) {
            if (model.id == id) {
                return model;
            }
        }
        return null;
    }

    public CircleNavigationCell getCellById(int id) {
        return cells.get(getModelPosition(id));
    }

    public int getModelPosition(int id) {
        for (int i = 0; i < models.size(); i++) {
            Model item = models.get(i);
            if (item.id == id) {
                return i;
            }
        }
        return -1;
    }

    public void setCount(int id, String count) {
        Model model = getModelById(id);
        int pos = getModelPosition(id);
        if (model != null) {
            model.count = count;
            cells.get(pos).setCount(count);
        }
    }

    public void clearCount(int id) {
        Model model = getModelById(id);
        int pos = getModelPosition(id);
        if (model != null) {
            model.count = CircleNavigationCell.EMPTY_VALUE;
            cells.get(pos).setCount(CircleNavigationCell.EMPTY_VALUE);
        }
    }

    public void clearAllCounts() {
        for (Model model : models) {
            clearCount(model.id);
        }
    }

    public void setOnShowListener(IBottomNavigationListener listener) {
        onShowListener = listener;
    }

    public void setOnClickMenuListener(IBottomNavigationListener listener) {
        onClickedListener = listener;
    }

    public void setOnReselectListener(IBottomNavigationListener listener) {
        onReselectListener = listener;
    }

    public int dp(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }
}

