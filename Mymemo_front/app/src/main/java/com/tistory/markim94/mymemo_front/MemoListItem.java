package com.tistory.markim94.mymemo_front;

public class MemoListItem {

    // 각 리스트 아이템의 데이터를 담을 Object array
    private Object[] mData;

    // 각 메모의 고유 Id
    private String mId;

    // 메모 아이템이 선택되었는지 여부
    private boolean mSelectable = true;

    // 생성자
    public MemoListItem(String itemId, Object[] obj){
        mId = itemId;
        mData = obj;
    }

    public MemoListItem(String memoId, String memoDate, String memoText,
                        String id_handwriting, String uri_handwriting,
                        String id_photo, String uri_photo,
                        String id_video, String uri_video,
                        String id_voice, String uri_voice){

        mId = memoId;
        mData = new Object[10];
        mData[0] = memoDate;
        mData[1] = memoText;
        mData[2] = id_handwriting;
        mData[3] = uri_handwriting;
        mData[4] = id_photo;
        mData[5] = uri_photo;
        mData[6] = id_video;
        mData[7] = uri_video;
        mData[8] = id_voice;
        mData[9] = uri_voice;
    }

    // item 선택여부 반환
    public boolean isSelectable() { return mSelectable; }


}
