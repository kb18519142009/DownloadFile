package com.example.downloadfile.util;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by kang on 2018/3/9.
 * Description：RecyclerView的万能间距
 */

public class CommonItemDecoration extends RecyclerView.ItemDecoration {
    private int horizontalSpace; // 整个RecyclerView与左右两侧的间距
    private int verticalSpace; // 整个RecyclerView与上下的间距

    private int leftMargin; // 每个item与左边的间距
    private int topMargin; // 每个item与顶部的间距
    private int rightMargin; // 每个item与右边的间距
    private int bottomMargin; // 每个item与底部的间距

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

        // 得到当前Item在RecyclerView中的位置,从0开始
        int position = parent.getChildAdapterPosition(view);
        // 得到RecyclerView中Item的总个数
        int count = parent.getAdapter().getItemCount();

        if (parent.getLayoutManager() instanceof GridLayoutManager) { // 网格布局
            GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
            // 得到网格布局的列数
            int spanCount = gridLayoutManager.getSpanCount();
            // 判断该网格布局是水平还是垂直
            if (LinearLayoutManager.VERTICAL == gridLayoutManager.getOrientation()) { // 垂直
                if (spanCount == 1) { // 列数为1
                    verticalColumnOne(outRect, position, count);
                } else { // 列数大于1
                    verticalColumnMulti(outRect, position, count, spanCount);
                }
            } else if (LinearLayoutManager.HORIZONTAL == gridLayoutManager.getOrientation()) { // 水平
                if (spanCount == 1) { // 行数为1
                    horizontalColumnOne(outRect, position, count);
                } else { // 行数大于1
                    horizontalColumnMulti(outRect, position, count, spanCount);
                }
            }
        } else if (parent.getLayoutManager() instanceof LinearLayoutManager) { // 线性布局
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            if (LinearLayoutManager.VERTICAL == layoutManager.getOrientation()) { // 垂直
                verticalColumnOne(outRect, position, count);
            } else if (LinearLayoutManager.HORIZONTAL == layoutManager.getOrientation()) { // 水平
                horizontalColumnOne(outRect, position, count);
            }
        } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) { // 流布局
            //TODO 瀑布流布局相关
        }
    }

    /**
     * 列表垂直且列数为1
     *
     * @param outRect  包括左上右下四个参数，分别控制view左上右下的margin
     * @param position 当前view所处位置
     * @param count    RecyclerView中Item的总个数
     */
    private void verticalColumnOne(Rect outRect, int position, int count) {
        if (position == 0) { // 位置为0时(即第一个Item)，不设置底部间距
            outRect.set(leftMargin, topMargin, rightMargin, 0);
        } else if (position == count - 1) { // 最后一个Item
            outRect.set(leftMargin, verticalSpace, rightMargin, bottomMargin);
        } else { // 中间的Item，不设置底部间距
            outRect.set(leftMargin, verticalSpace, rightMargin, 0);
        }
    }

    /**
     * 列表垂直且列数大于1
     *
     * @param outRect   包括左上右下四个参数，分别控制view左上右下的margin
     * @param position  当前view所处位置
     * @param count     RecyclerView中Item的总个数
     * @param spanCount 布局的列数
     */
    private void verticalColumnMulti(Rect outRect, int position, int count, int spanCount) {
        // 通过计算得出总行数
        int totalRow = count / spanCount + ((count % spanCount) == 0 ? 0 : 1);
        // 计算得出当前view所在的行
        int row = position / spanCount;
        // 通过对position加1对spanCount取余得到column
        // 保证column等于1为第一列，等于0为最后一个，其它值为中间列
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

    /**
     * 列表水平且行数为1
     *
     * @param outRect  包括左上右下四个参数，分别控制view左上右下的margin
     * @param position 当前view所处位置
     * @param count    RecyclerView中Item的总个数
     */
    private void horizontalColumnOne(Rect outRect, int position, int count) {

        if (position == 0) { // 位置为0时(即第一个Item)
            outRect.set(leftMargin, topMargin, horizontalSpace / 2, bottomMargin);
        } else if (position == count - 1) { // 最后一个Item
            outRect.set(horizontalSpace / 2, topMargin, rightMargin, bottomMargin);
        } else { // 中间的Item
            outRect.set(horizontalSpace / 2, topMargin, horizontalSpace / 2, bottomMargin);
        }
    }

    /**
     * 列表水平且行数大于1
     *
     * @param outRect   包括左上右下四个参数，分别控制view左上右下的margin
     * @param position  当前view所处位置
     * @param count     RecyclerView中Item的总个数
     * @param spanCount 布局的行数
     */
    private void horizontalColumnMulti(Rect outRect, int position, int count, int spanCount) {

        // 通过计算得出总列数
        int totalColumn = count / spanCount + ((count % spanCount) == 0 ? 0 : 1);

        // 计算得出当前view所在的列
        int column = position / spanCount;

        // 通过对position加1对spanCount取余得到row
        // 保证row等于1为第一行，等于0为最后一个，其它值为中间行
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