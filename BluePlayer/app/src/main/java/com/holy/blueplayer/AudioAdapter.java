package com.holy.blueplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class AudioAdapter extends BaseAdapter {

    // 오디오 리스트
    private final List<AudioInfo> audioInfoList;
    // 클릭 리스너
    private OnItemClickListener onItemClickListener;

    // 생성자

    public AudioAdapter(List<AudioInfo> audioInfoList) {
        this.audioInfoList = audioInfoList;
    }

    // 리스너 설정

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return audioInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return audioInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 아이템 뷰를 인플레이트한다.
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_audio, parent, false);
        }

        // 아이템 뷰에 오디오 정보를 표시한다.
        AudioInfo audioInfo = audioInfoList.get(position);
        ImageView coverImage = convertView.findViewById(R.id.img_album_art);
        TextView titleText = convertView.findViewById(R.id.txt_music_title);

        coverImage.setImageURI(audioInfo.getAlbumArtUri());
        titleText.setText(audioInfo.getTitle());

        // 아이템 뷰에 클릭 리스너 지정
        if (onItemClickListener != null) {
            convertView.setOnClickListener(v -> onItemClickListener.onItemClick(position));
        }

        return convertView;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}


