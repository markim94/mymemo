package com.tistory.markim94.mymemo_front;

public class MemoListItem {

    // 각 리스트 아이템의 데이터를 담을 Object array
    private String[] mData;

    // 각 메모의 고유 Id
    private String mId;

    // 메모 아이템이 선택되었는지 여부
    private boolean mSelectable = true;

    // 생성자

    /**
     * Initialize with icon and data array
     *
     * @param obj
     *
     */
    public MemoListItem(String itemId, String[] obj){
        mId = itemId;
        mData = obj;
    }

    /**
     * Initialize with strings
     *
     *
     * @param obj01 - memo input_date
     * @param obj02 - memo memoStr
     * @param obj03 - memo picture_id
     *
     */
    public MemoListItem(String memoId, String memoDate, String memoText,
                        String id_handwriting, String uri_handwriting,
                        String id_photo, String uri_photo,
                        String id_video, String uri_video,
                        String id_voice, String uri_voice){

        mId = memoId;
        mData = new String[10];
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

    // item 선택여부 반환(getter)
    public boolean isSelectable() { return mSelectable; }

    // selectable setter
    public void setSelectable(boolean selectable) { mSelectable = selectable; }

    // Id getter
    public String getId() { return mId; }

    // Id setter
    public void setId(String itemId) { mId = itemId; }

    // Data getter (array 통째로)
    public Object[] getData() { return mData; }

    // Data index getter
    public String getData(int index){
        if (mData == null || index >= mData.length){
            return null;
        }

        return mData[index];
    }

    // Data setter (array)
    public void setData(String[] obj) { mData = obj; }


    // memoListItem 비교
    public int compareTo(MemoListItem other) {
        if (mData != null) {
            Object[] otherData = other.getData();
            if (mData.length == otherData.length) {
                for (int i = 0; i < mData.length; i++) {
                    if (!mData[i].equals(otherData[i])) {
                        return -1;
                    }
                }
            } else {
                return -1;
            }
        } else {
            throw new IllegalArgumentException();
        }

        return 0;
    }

}
