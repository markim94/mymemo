package com.tistory.markim94.mymemo_front;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MemoListAdapter extends BaseAdapter {

    // main 액티비티에서 context 받아오기 위해서
    private Context mContext;

    public MemoListAdapter(Context context) { mContext = context; }

    // 객체 MemoListItem들을 array에 담기
    private List<MemoListItem> mItems = new ArrayList<MemoListItem>();

    // array clear
    public void clear() { mItems.clear(); }

    // add item
    public void addItem(MemoListItem it) { mItems.add(it); }

    // array setter
    public void setListItems(List<MemoListItem> lit) {
        mItems = lit;
    }

    @Override
    public int getCount() { return mItems.size(); }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) { return position; }

    public boolean areAllItemsSelectable() {
        return false;
    }

    public boolean isSelectable(int position) {
        try {
            return mItems.get(position).isSelectable();
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MemoListItemView itemView;
        if (convertView == null) {
            // 메인 액티비티 mContext 객체 매개변수로 넘겨주기
            itemView = new MemoListItemView(mContext);
        } else {
            itemView = (MemoListItemView) convertView;
        }

        /**
         * memoListItem의 data index info.
         * mData[0] = memoDate;
         * mData[1] = memoText;
         * mData[2] = id_handwriting;
         * mData[3] = uri_handwriting;
         * mData[4] = id_photo;
         * mData[5] = uri_photo;
         * mData[6] = id_video;
         * mData[7] = uri_video;
         * mData[8] = id_voice;
         * mData[9] = uri_voice;
         */

        itemView.setContents(0, ((String) mItems.get(position).getData(0)));
        itemView.setContents(1, ((String) mItems.get(position).getData(1)));
        itemView.setContents(2, ((String) mItems.get(position).getData(3)));
        itemView.setContents(3, ((String) mItems.get(position).getData(5)));

        itemView.setMediaState(mItems.get(position).getData(7),
                mItems.get(position).getData(9));

        return itemView;

    }
}
