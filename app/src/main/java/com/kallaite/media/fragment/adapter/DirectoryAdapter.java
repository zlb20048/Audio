package com.kallaite.media.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kallaite.media.R;

import java.util.List;

/**
 * Created by liuzixiang on 16-10-10.
 */
public class DirectoryAdapter extends BaseListAdapter<String> {

    public DirectoryAdapter(Context mContext, List<String> data) {
        super(mContext, data);
    }

    @Override
    protected View bindView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.floder_prim_item_layout, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.sourceTextView = (TextView) convertView
                    .findViewById(R.id.prim_source_text);
            mViewHolder.folderLayout = (RelativeLayout) convertView.findViewById(R.id.folder_item_layout);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
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
        String dirName = dataList.get(position);
        if (dirName != null) {
            mViewHolder.sourceTextView.setText(dirName);
            if (position == 0) {
                mViewHolder.sourceTextView.setText(R.string.my_device);
                mViewHolder.folderLayout.setBackgroundResource(R.drawable.music_equipment_selector);
            } else if (position == 1) {
                mViewHolder.folderLayout.setBackgroundResource(R.drawable.list_usb_selector);
            } else {
                mViewHolder.folderLayout.setBackgroundResource(R.drawable.music_file_selector);
            }
        }
    }

    private class ViewHolder {
        TextView sourceTextView;

        RelativeLayout folderLayout;
    }
}
