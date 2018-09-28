package com.kallaite.media.fragment.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kallaite.media.R;
import com.kallaite.media.data.struct.Artist;

import java.util.List;

/**
 * Created by liuzixiang on 15-12-9.
 */
public class ArtistListAdapter extends BaseListAdapter<Artist> {
    /**
     * TAG
     */
    private final static String TAG = ArtistListAdapter.class.getSimpleName();

    public ArtistListAdapter(Context context, List<Artist> artists) {
        super(context, artists);
    }

    private Artist mArtist;

    public void setArtist(Artist artist) {
        this.mArtist = artist;
        notifyDataSetChanged();
    }

    private void loadData(ViewHolder viewHolder, Artist artist) {
        if (null != artist) {
            viewHolder.artistName.setText(
                    TextUtils.isEmpty(artist.getName()) ? mContext
                            .getString(R.string.unknown_artist_name) : artist
                            .getName());
        }
    }

    @Override
    protected View bindView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.source_sec_item_layout, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.artistName = (TextView) convertView.findViewById(R.id.sec_source_text);
            mViewHolder.imageView = (ImageView) convertView.findViewById(R.id.sec_source_image);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        if (null != dataList && !dataList.isEmpty()) {
            Artist artist = dataList.get(position);
            if (mArtist != null && artist.getId() == mArtist.getId() &&
                    artist.getMediaStorage() == mArtist.getMediaStorage()) {
                convertView.setBackgroundResource(R.drawable.source_item_current);
                mViewHolder.artistName.setTextColor(mSelectedCsl);
            } else {
                convertView.setBackgroundResource(R.drawable.source_item_normal);
                mViewHolder.artistName.setTextColor(mNormalCsl);
            }
            loadData(mViewHolder, artist);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView artistName;

        ImageView imageView;
    }
}
