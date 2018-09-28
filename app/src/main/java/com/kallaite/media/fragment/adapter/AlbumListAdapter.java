package com.kallaite.media.fragment.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kallaite.media.R;
import com.kallaite.media.data.struct.Album;
import com.kallaite.media.util.WLog;

import java.util.List;

/**
 * Created by liuzixiang on 15-12-9.
 */
public class AlbumListAdapter extends BaseListAdapter<Album> {
    /**
     * TAG
     */
    private final static String TAG = AlbumListAdapter.class.getSimpleName();

    /**
     * 当前播放的albumId
     */
    private Album mAlbum;

    public AlbumListAdapter(Context context, List<Album> albums) {
        super(context, albums);
        WLog.d(TAG, "AlbumListAdapter...");
    }

    public void setCurrentAlbum(Album album) {
        WLog.d(TAG, "albumId = " + album.getId());
        this.mAlbum = album;
        notifyDataSetChanged();
    }

    private void loadData(ViewHolder viewHolder, Album album) {
        if (album != null) {
            viewHolder.albumName.setText(
                    TextUtils.isEmpty(album.getName()) ? mContext
                            .getString(R.string.unknown_album_name) : album
                            .getName());
        }
    }

    @Override
    protected View bindView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.source_sec_item_layout, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.albumName = (TextView) convertView.findViewById(R.id.sec_source_text);
            mViewHolder.imageView = (ImageView) convertView.findViewById(R.id.sec_source_image);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        if (null != dataList && !dataList.isEmpty()) {
            Album album = dataList.get(position);
            if (mAlbum != null && album.getId() == mAlbum.getId() &&
                    album.getMediaStorage() == mAlbum.getMediaStorage()) {
                convertView.setBackgroundResource(R.drawable.source_item_current);
                mViewHolder.albumName.setTextColor(mSelectedCsl);
            } else {
                convertView.setBackgroundResource(R.drawable.source_item_normal);
                mViewHolder.albumName.setTextColor(mNormalCsl);
            }
            loadData(mViewHolder, album);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView albumName;

        ImageView imageView;
    }
}
