package com.example.downloadfile.util;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

public class CommonItemDecoration extends RecyclerView.ItemDecoration {

    private int horizontalSpace;
    private int verticalSpace;
    private int leftMargin;
    private int topMargin;
    private int rightMargin;
    private int bottomMargin;

    public CommonItemDecoration(int horizontalSpace, int verticalSpace) {
        this(horizontalSpace, verticalSpace, 0, 0, 0, 0);
    }

    public CommonItemDecoration(int horizontalSpace, int verticalSpace, int margin) {
        this(horizontalSpace, verticalSpace, margin, margin, margin, margin);
    }

    public CommonItemDecoration(int horizontalSpace, int verticalSpace, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        this.horizontalSpace = horizontalSpace;
        this.verticalSpace = verticalSpace;
        this.leftMargin = leftMargin;
        this.topMargin = topMargin;
        this.rightMargin = rightMargin;
        this.bottomMargin = bottomMargin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int count = parent.getAdapter().getItemCount();
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
            int spanCount = gridLayoutManager.getSpanCount();
            if (LinearLayoutManager.VERTICAL == gridLayoutManager.getOrientation()) {
                if (spanCount == 1) {
                    verticalColumnOne(outRect, position, count);
                } else {
                    verticalColumnMulti(outRect, position, count, spanCount);
                }
            } else if (LinearLayoutManager.HORIZONTAL == gridLayoutManager.getOrientation()) {
                if (spanCount == 1) {
                    horizontalColumnOne(outRect, position, count);
                } else {
                    horizontalColumnMulti(outRect, position, count, spanCount);
                }
            }
        } else if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            if (LinearLayoutManager.VERTICAL == layoutManager.getOrientation()) {
                verticalColumnOne(outRect, position, count);
            } else if (LinearLayoutManager.HORIZONTAL == layoutManager.getOrientation()) {
                horizontalColumnOne(outRect, position, count);
            }
        } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            //TODO
        }
    }

    private void verticalColumnOne(Rect outRect, int position, int count) {
        if (position == 0) {
            outRect.set(leftMargin, topMargin, rightMargin, 0);
        } else if (position == count - 1) {
            outRect.set(leftMargin, verticalSpace, rightMargin, bottomMargin);
        } else {
            outRect.set(leftMargin, verticalSpace, rightMargin, 0);
        }
    }

    private void verticalColumnMulti(Rect outRect, int position, int count, int spanCount) {
        int totalRow = count / spanCount + ((count % spanCount) == 0 ? 0 : 1);
        int row = position / spanCount;
        int column = (position + 1) % spanCount;
        if (column == 1) {
            outRect.set(leftMargin,
                    row == 0 ? topMargin : verticalSpace,
                    horizontalSpace / 2,
                    row == totalRow - 1 ? bottomMargin : 0);
        } else if (column == 0) {
            outRect.set(horizontalSpace / 2,
                    row == 0 ? topMargin : verticalSpace,
                    rightMargin,
                    row == totalRow - 1 ? bottomMargin : 0);
        } else {
            outRect.set(horizontalSpace / 2,
                    row == 0 ? topMargin : verticalSpace,
                    horizontalSpace / 2,
                    row == totalRow - 1 ? bottomMargin : 0);
        }
    }

    private void horizontalColumnOne(Rect outRect, int position, int count) {
        if (position == 0) {
            outRect.set(leftMargin, topMargin, horizontalSpace / 2, bottomMargin);
        } else if (position == count - 1) {
            outRect.set(horizontalSpace / 2, topMargin, rightMargin, bottomMargin);
        } else {
            outRect.set(horizontalSpace / 2, topMargin, horizontalSpace / 2, bottomMargin);
        }
    }

    private void horizontalColumnMulti(Rect outRect, int position, int count, int spanCount) {
        int totalColumn = count / spanCount + ((count % spanCount) == 0 ? 0 : 1);
        int column = position / spanCount;
        int row = (position + 1) % spanCount;
        if (row == 1) {
            outRect.set(column == 0 ? leftMargin : horizontalSpace / 2,
                    topMargin,
                    column == totalColumn - 1 ? rightMargin : horizontalSpace / 2,
                    0);
        } else if (row == 0) {
            outRect.set(column == 0 ? leftMargin : horizontalSpace / 2,
                    verticalSpace,
                    column == totalColumn - 1 ? rightMargin : horizontalSpace / 2,
                    bottomMargin);
        } else {
            outRect.set(column == 0 ? leftMargin : horizontalSpace / 2,
                    verticalSpace,
                    column == totalColumn - 1 ? rightMargin : horizontalSpace / 2,
                    0);
        }
    }

}