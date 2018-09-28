package com.kallaite.media.fragment.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kallaite.media.util.WLog;
import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.R;

import java.util.List;

/**
 * Created by liuzixiang on 15-12-9.
 */
public class SongListAdapter extends BaseListAdapter<MusicEntry> {
    /**
     * TAG
     */
    private final static String TAG = SongListAdapter.class.getSimpleName();

    public SongListAdapter(Context mContext, List<MusicEntry> musicEntries) {
        super(mContext, musicEntries);
    }

    private void loadData(ViewHolder viewHolder, MusicEntry musicEntry) {
        if (null != musicEntry) {
            viewHolder.songView.setText(musicEntry.getTitle());
            viewHolder.artistName.setText(
                    TextUtils.isEmpty(musicEntry.getArtistName()) ? mContext
                            .getString(
                                    R.string.unknown_artist_name) : musicEntry
                            .getArtistName());
        }
    }

    @Override
    protected View bindView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.source_item_layout, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.songView = (TextView) convertView
                    .findViewById(R.id.source_title);
            mViewHolder.artistName = (TextView) convertView
                    .findViewById(R.id.artist_name);
            mViewHolder.imageView = (ImageView) convertView
                    .findViewById(R.id.sec_source_image);
            mViewHolder.countView = (TextView) convertView
                    .findViewById(R.id.count_number);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            if (null != dataList && !dataList.isEmpty()) {
                MusicEntry musicEntry = dataList.get(position);
                if (currentMusicEntry != null &&
                        musicEntry.getUri().equals(currentMusicEntry.getUri())) {
                    convertView.setBackgroundResource(
                            R.drawable.source_item_current);
                    mViewHolder.songView.setTextColor(mSelectedCsl);
                    mViewHolder.artistName.setTextColor(mSelectedCsl);
                    mViewHolder.imageView.setVisibility(View.VISIBLE);
                } else {
                    convertView.setBackgroundResource(
                            R.drawable.source_item_normal);
                    mViewHolder.songView.setTextColor(mNormalCsl);
                    mViewHolder.artistName.setTextColor(mNormalCsl);
                    mViewHolder.imageView.setVisibility(View.INVISIBLE);
                }
                mViewHolder.countView.setText((position + 1) + ".");
                loadData(mViewHolder, musicEntry);
            }
        } catch (Exception e) {
            WLog.e(TAG, e);
        }
        return convertView;
    }

    private class ViewHolder {

        TextView songView;

        TextView artistName;

        ImageView imageView;

        TextView countView;
    }
}
