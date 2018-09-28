package com.kallaite.media.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kallaite.media.R;
import com.kallaite.media.fragment.bean.ModuleLabel;

import java.util.List;

/**
 * Created by liuzixiang on 15-12-15.
 */
public class SourceAdapter extends BaseListAdapter<ModuleLabel> {
    /**
     * TAG
     */
    private final static String TAG = SourceAdapter.class.getSimpleName();

    /**
     * mCurrentPos
     */
    private int mCurrentPos;

    public SourceAdapter(Context mContext, List<ModuleLabel> data) {
        super(mContext, data);
        this.dataList = data;
    }

    @Override
    protected View bindView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.floder_prim_item_layout, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.sourceTextView = (TextView) convertView.findViewById(R.id.prim_source_text);
            mViewHolder.folderLayout = (RelativeLayout) convertView.findViewById(R.id.folder_item_layout);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        if (mCurrentPos == position) {
            convertView.setBackgroundResource(R.drawable.source_item_current);
            mViewHolder.sourceTextView.setTextColor(mSelectedCsl);
        } else {
            convertView.setBackgroundResource(R.drawable.source_item_normal);
            mViewHolder.sourceTextView.setTextColor(mNormalCsl);
        }
        if (null != dataList && !dataList.isEmpty()) {
            loadData(mViewHolder, position);
        }
        return convertView;
    }

    /**
     * 加载数据
     *
     * @param mViewHolder ViewHolder
     * @param position    position
     */
    private void loadData(ViewHolder mViewHolder, int position) {
        ModuleLabel moduleLabel = dataList.get(position);
        if (null != moduleLabel) {
            mViewHolder.sourceTextView.setText(moduleLabel.lableName);
            if (mCurrentPos == position) {
                mViewHolder.folderLayout.setBackgroundResource(moduleLabel.focusIcon);
            } else {
                mViewHolder.folderLayout.setBackgroundResource(moduleLabel.lableIcon);
            }
        }
    }

    public void setCurrentPos(int position) {
        mCurrentPos = position;
    }

    private class ViewHolder {
        TextView sourceTextView;

        RelativeLayout folderLayout;
    }
}
