package com.tistory.markim94.mymemo_front;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MemoListItemView extends LinearLayout {

    ImageView itemPhoto;
    TextView itemDate;
    TextView itemText;
    ImageView itemVideoState;
    ImageView itemVoiceState;
    ImageView itemHandWritting;
    Bitmap bitmap;

    public MemoListItemView(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.memo_listitem, this, true);

        itemPhoto = (ImageView) findViewById(R.id.itemPhoto);
        itemDate = (TextView) findViewById(R.id.itemDate);
        itemText = (TextView) findViewById(R.id.itemText);
        itemVideoState = (ImageView) findViewById(R.id.itemVideoState);
        itemVoiceState = (ImageView) findViewById(R.id.itemVoiceState);
        itemHandWritting = (ImageView) findViewById(R.id.itemHandWritting);
    }

    // 인덱스 값에 따른 데이터 설정
    public void setContents(int index, String data){
        if (index == 0){
            itemDate.setText(data);
        } else if (index == 1) {
            itemText.setText(data);
        } else if (index == 2) {
            if (data == null || data.equals("-1") || data.equals("")) {
                itemHandWritting.setImageBitmap(null);
            } else {
                // BasicInfo의 FOLDER_PHOTO 경로에 있는 사진으로 setImageURI
                // itemHandWritting.setImageURI(Uri.parse(BasicInfo.FORDER_PHOTO + data));
            }
        } else if(index == 3){
            if (data == null || data.equals("-1") || data.equals("")) {
                itemPhoto.setImageResource(R.drawable.person);
            } else{
                if(bitmap != null){
                    bitmap.recycle();
                }

                //...

            }


        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setMediaState(Object sVideo, Object sVoice){
        if(sVideo == null) {
            itemVideoState.setImageResource(R.drawable.icon_video_empty);
        } else{
            itemVideoState.setImageResource(R.drawable.icon_video);
        }

        if(sVoice == null) {
            itemVoiceState.setImageResource(R.drawable.icon_voice_empty);
        } else{
            itemVoiceState.setImageResource(R.drawable.icon_voice);
        }
    }
}
