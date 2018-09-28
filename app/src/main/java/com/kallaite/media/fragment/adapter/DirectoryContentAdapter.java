package com.kallaite.media.fragment.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kallaite.media.util.WLog;
import com.kallaite.media.data.struct.IMusicEntry;
import com.kallaite.media.R;
import com.kallaite.media.presenter.DirectoryBrowserContract;

import java.util.List;

/**
 * Created by liuzixiang on 16-10-10.
 */
public class DirectoryContentAdapter extends BaseListAdapter<DirectoryBrowserContract.Item> {
    private final static String TAG = DirectoryContentAdapter.class.getSimpleName();

    public DirectoryContentAdapter(Context mContext, List<DirectoryBrowserContract.Item> data) {
        super(mContext, data);
    }
    @Override
    protected View bindView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.source_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.iconImage = (ImageView) convertView
                    .findViewById(R.id.icon_image);
            viewHolder.songView = (TextView) convertView
                    .findViewById(R.id.source_title);
            viewHolder.artistName = (TextView) convertView
                    .findViewById(R.id.artist_name);
            viewHolder.imageView = (ImageView) convertView
                    .findViewById(R.id.sec_source_image);
            viewHolder.countNumberView = (TextView) convertView
                    .findViewById(R.id.count_number);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            if (dataList != null && !dataList.isEmpty()) {
                DirectoryBrowserContract.Item item = dataList.get(position);
                if (item.itemType == DirectoryBrowserContract.ITEM_TYPE.DIR) {
                    // 此处判断当前的节点是否需要高亮显示
                    if (item.isPlaying) {
                        convertView.setBackgroundResource(
                                R.drawable.source_item_current);
                        viewHolder.songView.setTextColor(mSelectedCsl);
                        viewHolder.artistName.setTextColor(mSelectedCsl);
                        viewHolder.imageView.setVisibility(View.VISIBLE);
                    } else {
                        convertView.setBackgroundResource(
                                R.drawable.source_item_normal);
                        viewHolder.songView.setTextColor(mNormalCsl);
                        viewHolder.artistName.setTextColor(mNormalCsl);
                        viewHolder.imageView.setVisibility(View.INVISIBLE);
                    }
                    displayFolder(viewHolder, (String) item.itemEntry);
                } else {
                    IMusicEntry musicEntry = (IMusicEntry) item.itemEntry;
                    if (null != musicEntry) {
                        if (item.isPlaying) {
                            convertView.setBackgroundResource(
                                    R.drawable.source_item_current);
                            viewHolder.songView.setTextColor(mSelectedCsl);
                            viewHolder.artistName.setTextColor(mSelectedCsl);
                            viewHolder.imageView.setVisibility(View.VISIBLE);
                        } else {
                            convertView.setBackgroundResource(
                                    R.drawable.source_item_normal);
                            viewHolder.songView.setTextColor(mNormalCsl);
                            viewHolder.artistName.setTextColor(mNormalCsl);
                            viewHolder.imageView.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        convertView.setBackgroundResource(
                                R.drawable.source_item_normal);
                        viewHolder.songView.setTextColor(mNormalCsl);
                        viewHolder.artistName.setTextColor(mNormalCsl);
                        viewHolder.imageView.setVisibility(View.INVISIBLE);
                    }
                    viewHolder.iconImage.setVisibility(View.GONE);
                    displayMusic(viewHolder, musicEntry);
                }
                viewHolder.countNumberView.setText((position + 1) + ".");
            }
        } catch (Exception e) {
            WLog.e(TAG, e);
        }
        return convertView;
    }

    @Override
    public void refreshData(List<DirectoryBrowserContract.Item> list) {
        for(DirectoryBrowserContract.Item item : dataList){
            item.recycle();
        }
        super.refreshData(list);

    }

    private void displayMusic(ViewHolder viewHolder, IMusicEntry entry) {
        viewHolder.songView.setText(entry.getTitle());
        viewHolder.artistName.setText(TextUtils.isEmpty(entry.getArtistName()) ? mContext
                .getString(R.string.unknown_artist_name) : entry.getArtistName());
        viewHolder.artistName.setVisibility(View.VISIBLE);
        viewHolder.iconImage.setVisibility(View.GONE);
    }

    private void displayFolder(ViewHolder viewHolder, String dirName) {
        viewHolder.songView.setText(dirName);
        viewHolder.artistName.setVisibility(View.GONE);
        viewHolder.iconImage.setVisibility(View.VISIBLE);
    }

    private class ViewHolder {
        public ImageView iconImage;

        public TextView songView;

        public TextView artistName;

        public ImageView imageView;

        public TextView countNumberView;
    }
}
