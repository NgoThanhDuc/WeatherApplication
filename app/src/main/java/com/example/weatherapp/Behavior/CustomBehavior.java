package com.example.weatherapp.Behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

import com.example.weatherapp.R;

import edmt.dev.advancednestedscrollview.MaxHeightRecyclerView;
import edmt.dev.advancednestedscrollview.MyViewGroupUtils;

public class CustomBehavior extends CoordinatorLayout.Behavior<NestedScrollView> {

    public CustomBehavior(Context context, AttributeSet atts) {
        super(context, atts);
    }

    //hàm này tượng trưng kéo đến màn hình nào thì dừng(hiện tại là lấy layout có id.toolbar_container, đụng thì dừng, cũng là chiều cai tối đa)
    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull NestedScrollView child, @NonNull View dependency) {
        return dependency.getId() == R.id.toolbar_container;
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull NestedScrollView child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);

        //độ cảo của NestedScrollView - độ cao của 2 cái text = độ cao lớn nhất của recyclerView
        int rvMaxHeightHourly = child.getHeight() - child.findViewById(R.id.relativeLayoutRV).getHeight();
        int rvMaxHeight = child.getHeight() - child.findViewById(R.id.img_arrowHourly).getHeight();

        MaxHeightRecyclerView rv = child.findViewById(R.id.card_recycler_view);
        rv.setMaxHeight(rvMaxHeightHourly); //xét độ cao lớn nhất của recyclerView

        View cardContainer = child.findViewById(R.id.card_container);
        int toolbarContainerHeight = parent.getDependencies(child).get(0).getHeight();
        setPaddingTop(cardContainer, rvMaxHeight - toolbarContainerHeight);
        ViewCompat.offsetTopAndBottom(child, toolbarContainerHeight);
        setPaddingBottom(rv, toolbarContainerHeight);
        return true;
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull NestedScrollView child, @NonNull MotionEvent ev) {
        return ev.getActionMasked() == MotionEvent.ACTION_DOWN &&
                isTouchInChildBounds(parent, child, ev)
                && !isTouchInChildBounds(parent, child.findViewById(R.id.relativeLayoutRV), ev)
                && !isTouchInChildBounds(parent, child.findViewById(R.id.img_arrowHourly), ev);
    }

    private boolean isTouchInChildBounds(ViewGroup parent, View child, MotionEvent ev) {
        return MyViewGroupUtils.isPointInChildBounds(parent, child, (int) ev.getX(), (int) ev.getY());
    }

    private void setPaddingBottom(View view, int bottom) {
        if (view.getPaddingBottom() != bottom) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), bottom);
        }
    }

    private void setPaddingTop(View view, int top) {
        if (view.getPaddingTop() != top) {
            view.setPadding(view.getPaddingLeft(), top, view.getPaddingRight(), view.getPaddingBottom());
        }
    }

}
