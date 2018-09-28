package com.kallaite.media.fragment.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.kallaite.media.R;
import com.kallaite.media.data.struct.IMusicEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzixiang on 15-12-9.
 */
public abstract class BaseListAdapter<T> extends BaseAdapter
{
    /**
     * TAG
     */
    private final static String TAG = BaseListAdapter.class.getSimpleName();

    /**
     * Context
     */
    protected final Context mContext;

    /**
     * 当前的数据
     */
    protected List<T> dataList = new ArrayList<T>();

    /**
     * 当前的锁
     */
    private Object sync = new Object();

    /**
     * mSelectedCsl
     */
    protected ColorStateList mSelectedCsl;

    /**
     * mNormalCsl
     */
    protected ColorStateList mNormalCsl;

    protected IMusicEntry currentMusicEntry;

    public void setCurrentMusic(IMusicEntry musicEntry)
    {
        this.currentMusicEntry = musicEntry;
        this.notifyDataSetChanged();
    }

    public BaseListAdapter(Context mContext, List<T> data)
    {
        this.mContext = mContext;
        addData(data);
        Resources rs = mContext.getResources();
        mSelectedCsl = rs.getColorStateList(R.color.text_theme_color);
        mNormalCsl = rs.getColorStateList(R.color.white);
        int colorNormal = rs.getColor(R.color.white);
        int colorSelect = rs.getColor(R.color.text_theme_color);
        mNormalCsl = createColorStateList(colorNormal, colorSelect, colorSelect, colorNormal);
    }

    private ColorStateList createColorStateList(int normal, int pressed, int focused, int unable)
    {
        int[] colors = new int[]{pressed, focused, normal, focused, unable, normal};
        int[][] states = new int[6][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[2] = new int[]{android.R.attr.state_enabled};
        states[3] = new int[]{android.R.attr.state_focused};
        states[4] = new int[]{android.R.attr.state_window_focused};
        states[5] = new int[]{};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

    @Override
    public int getCount()
    {
        return dataList.size();
    }

    @Override
    public T getItem(int position)
    {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * 刷新数据
     */
    public void refreshData(List<T> datas)
    {
        synchronized (sync)
        {
            addData(datas);
            this.notifyDataSetChanged();
        }
    }

    public List<T> getDataList()
    {
        return dataList;
    }

    protected abstract View bindView(int position, View convertView, ViewGroup parent);

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return bindView(position, convertView, parent);
    }

    public void addData(List<T> data)
    {
        synchronized (sync)
        {
            dataList.clear();
            if (null != data)
            {
                dataList.addAll(data);
            }
        }
    }
}
