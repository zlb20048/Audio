package com.kallaite.media.fragment.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import com.kallaite.media.R;

/**
 * Created by liuzixiang on 16-2-17.
 */
public class PaginationListView extends ListView implements OnScrollListener
{
    //底部View
    private View footerView;
    //ListView item个数
    int totalItemCount = 0;
    //最后可见的Item
    int lastVisibleItem = 0;
    //是否加载标示
    boolean isLoading = false;

    /**
     * 是否有下一页
     */
    boolean isHasNextPage = false;

    public PaginationListView(Context context)
    {
        super(context);
        initView(context);
    }

    public PaginationListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context);

    }

    public PaginationListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initView(context);
    }

    /**
     * 初始化ListView
     */
    private void initView(Context context)
    {
        LayoutInflater mInflater = LayoutInflater.from(context);
        footerView = mInflater.inflate(R.layout.footer, null, true);
        footerView.setVisibility(View.GONE);
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        //当滑动到底端，并滑动状态为 not scrolling
        if (!isHasNextPage)
        {
            return;
        }
        if (lastVisibleItem == totalItemCount && scrollState == SCROLL_STATE_IDLE)
        {
            if (!isLoading)
            {
                isLoading = true;
                //添加底部View
                footerView.setVisibility(View.VISIBLE);
                //设置可见
                // this.addFooterView(footerView);
                //加载数据
                onLoadListener.onLoad();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    private OnLoadListener onLoadListener;

    public void setOnLoadListener(OnLoadListener onLoadListener)
    {
        this.onLoadListener = onLoadListener;
    }

    /**
     * 加载数据接口
     *
     * @author Administrator
     */
    public interface OnLoadListener
    {
        void onLoad();
    }

    /**
     * 数据加载完成
     */
    public void loadComplete()
    {
        footerView.setVisibility(View.GONE);
        // this.removeFooterView(footerView);
        isLoading = false;
        this.invalidate();
    }

    /**
     * 设置是否有下一页
     *
     * @param isHasNextPage 是否有下一页
     */
    public void setHasNextPage(boolean isHasNextPage)
    {
        this.isHasNextPage = isHasNextPage;
    }
}
