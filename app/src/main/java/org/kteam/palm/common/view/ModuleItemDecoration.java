package org.kteam.palm.common.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @Package org.kteam.palm.common.view
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-06 02:22
 */
public class ModuleItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public ModuleItemDecoration (int space) {
            this.space = space;
        }

    @Override
    public void getItemOffsets(Rect outRect, View view,
        RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        outRect.top = space;

        if(parent.getChildLayoutPosition(view) == 0) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.top = 0;
        }
    }
}