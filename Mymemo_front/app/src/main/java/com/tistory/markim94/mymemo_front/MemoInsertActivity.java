package com.tistory.markim94.mymemo_front;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.tistory.markim94.mymemo_front.db.MemoDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class MemoInsertActivity extends AppCompatActivity {

    Button mPhotoBtn;
    Button mVideoBtn;
    Button mVoiceBtn;
    Button mHandwritingBtn;

    Button titleBackgroundBtn;

    Button insertSaveBtn;
    Button insertCancelBtn;
    Button insert_textBtn;
    Button insert_handwritingBtn;
    Button deleteBtn;

    EditText mMemoEdit;
    ImageView mPhoto;

    String mMemoMode;
    String mMemoId;
    String mMemoDate;

    String mMediaPhotoId;
    String mMediaPhotoUri;
    String mMediaVideoId;
    String mMediaVideoUri;
    String mMediaVoiceId;
    String mMediaVoiceUri;
    String mMediaHandwritingId;
    String mMediaHandwritingUri;

    String tempPhotoUri;
    String tempVideoUri;
    String tempVoiceUri;
    String tempHandwritingUri;

    String mDateStr;
    String mMemoStr;

    Bitmap resultPhotoBitmap;

    Bitmap resultHandwritingBitmap;

    boolean isPhotoCaptured;
    boolean isVideoRecorded;
    boolean isVoiceRecorded;
    boolean isHandwritingMade;

    boolean isPhotoFileSaved;
    boolean isVideoFileSaved;
    boolean isVoiceFileSaved;
    boolean isHandwritingFileSaved;

    boolean isPhotoCanceled;
    boolean isVideoCanceled;
    boolean isVoiceCanceled;
    boolean isHandwritingCanceled;

    Calendar mCalendar = Calendar.getInstance();
    Button insertDateButton;

    int mSelectdContentArray;
    int mChoicedArrayItem;

    // text메모와 handwriting메모의 상태확인을 위한 Mode 설정
    int textViewMode = 0;

    EditText insert_memoEdit;
    ImageView insert_handwriting;

    // 애니메이션
    Animation translateLeftAnim;
    Animation translateRightAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_insert);

        titleBackgroundBtn = (Button) findViewById(R.id.titleBackgroundBtn);
        mPhoto = (ImageView)findViewById(R.id.insert_photo);
        mMemoEdit = (EditText) findViewById(R.id.insert_memoEdit);

        insert_textBtn = (Button)findViewById(R.id.insert_textBtn);
        insert_handwritingBtn = (Button)findViewById(R.id.insert_handwritingBtn);
        insert_memoEdit = (EditText)findViewById(R.id.insert_memoEdit);
        insert_handwriting = (ImageView)findViewById(R.id.insert_handwriting);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);

        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        // Sliding 애니메이션
        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);

        // Frame 레이아웃에서 text메모를 먼저 위로 띄우기 위함임.
        insert_textBtn.setSelected(true);
        insert_handwritingBtn.setSelected(false);


        insert_textBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (textViewMode == 1) {
                    insert_handwriting.setVisibility(View.GONE);
                    insert_memoEdit.setVisibility(View.VISIBLE);
                    insert_memoEdit.startAnimation(translateLeftAnim);

                    textViewMode = 0;
                    insert_textBtn.setSelected(true);
                    insert_handwritingBtn.setSelected(false);
                }
            }
        });

        insert_handwritingBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (textViewMode == 0) {
                    insert_handwriting.setVisibility(View.VISIBLE);
                    insert_memoEdit.setVisibility(View.GONE);
                    insert_handwriting.startAnimation(translateLeftAnim);

                    textViewMode = 1;
                    insert_handwritingBtn.setSelected(true);
                    insert_textBtn.setSelected(false);
                }
            }
        });

        mPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(isPhotoCaptured || isPhotoFileSaved) {
                    showDialog(BasicInfo.CONTENT_PHOTO_EX);
                } else {
                    showDialog(BasicInfo.CONTENT_PHOTO);
                }
            }
        });

        insert_handwriting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HandwritingMakingActivity.class);
                startActivityForResult(intent, BasicInfo.REQ_HANDWRITING_MAKING_ACTIVITY);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(BasicInfo.CONFIRM_DELETE);
            }
        });


        setBottomButtons();

        setMediaLayout();

        setCalendar();

        Intent intent = getIntent();
        mMemoMode = intent.getStringExtra(BasicInfo.KEY_MEMO_MODE);
        if(mMemoMode.equals(BasicInfo.MODE_MODIFY) || mMemoMode.equals(BasicInfo.MODE_VIEW)) {
            processIntent(intent);

            titleBackgroundBtn.setText("메모 보기");
            insertSaveBtn.setText("수정");
            deleteBtn.setVisibility(View.VISIBLE);
        } else {
            titleBackgroundBtn.setText("새 메모");
            insertSaveBtn.setText("저장");
            deleteBtn.setVisibility(View.GONE);
        }

    }

    public void processIntent(Intent intent) {
        mMemoId = intent.getStringExtra(BasicInfo.KEY_MEMO_ID);
        mMemoEdit.setText(intent.getStringExtra(BasicInfo.KEY_MEMO_TEXT));
        mMediaPhotoId = intent.getStringExtra(BasicInfo.KEY_ID_PHOTO);
        mMediaPhotoUri = intent.getStringExtra(BasicInfo.KEY_URI_PHOTO);
        mMediaVideoId = intent.getStringExtra(BasicInfo.KEY_ID_VIDEO);
        mMediaVideoUri = intent.getStringExtra(BasicInfo.KEY_URI_VIDEO);
        mMediaVoiceId = intent.getStringExtra(BasicInfo.KEY_ID_VOICE);
        mMediaVoiceUri = intent.getStringExtra(BasicInfo.KEY_URI_VOICE);
        mMediaHandwritingId = intent.getStringExtra(BasicInfo.KEY_ID_HANDWRITING);
        mMediaHandwritingUri = intent.getStringExtra(BasicInfo.KEY_URI_HANDWRITING);

        setMediaImage(mMediaPhotoId, mMediaPhotoUri, mMediaVideoId, mMediaVoiceId, mMediaHandwritingId);
    }


    public void setMediaImage(String photoId, String photoUri, String videoId, String voiceId, String handwritingId) {

        if(photoId.equals("") || photoId.equals("-1")) {
            mPhoto.setImageResource(R.drawable.image_add);
        } else {
            isPhotoFileSaved = true;
            mPhoto.setImageURI(Uri.parse(BasicInfo.FOLDER_PHOTO + photoUri));
        }

        if(handwritingId.equals("") || handwritingId.equals("-1")) {

        } else {
            isHandwritingFileSaved = true;
            tempHandwritingUri = mMediaHandwritingUri;

            Bitmap resultBitmap = BitmapFactory.decodeFile(BasicInfo.FOLDER_HANDWRITING + tempHandwritingUri);
            insert_handwriting.setImageBitmap(resultBitmap);
        }

    }

    /**
     * 하단 메뉴 버튼 설정
     */
    public void setBottomButtons() {
        insertSaveBtn = (Button)findViewById(R.id.insert_saveBtn);
        insertCancelBtn = (Button)findViewById(R.id.insert_cancelBtn);

        // 저장 버튼
        insertSaveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean isParsed = parseValues();
                if (isParsed) {
                    if(mMemoMode.equals(BasicInfo.MODE_INSERT)) {
                        saveInput();
                    } else if(mMemoMode.equals(BasicInfo.MODE_MODIFY) || mMemoMode.equals(BasicInfo.MODE_VIEW)) {
                        modifyInput();
                    }
                }
            }
        });

        // 닫기 버튼
        insertCancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }



    private class SlidingPageAnimationListener implements Animation.AnimationListener {

        public void onAnimationEnd(Animation animation) {

        }

        public void onAnimationRepeat(Animation animation) {

        }

        public void onAnimationStart(Animation animation) {

        }

    }

    /**
     * 데이터베이스에 레코드 추가
     */
    private void saveInput() {

        String photoFilename = insertPhoto();
        int photoId = -1;

        String SQL = null;

        if (photoFilename != null) {
            // query picture id
            SQL = "select _ID from " + MemoDatabase.TABLE_PHOTO + " where URI = '" + photoFilename + "'";

            if (MainActivity.mDatabase != null) {
                Cursor cursor = MainActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    photoId = cursor.getInt(0);
                }
                cursor.close();
            }
        }

        String handwritingFileName = insertHandwriting();
        int handwritingId = -1;

        if (handwritingFileName != null) {
            // query picture id
            SQL = "select _ID from " + MemoDatabase.TABLE_HANDWRITING + " where URI = '" + handwritingFileName + "'";

            if (MainActivity.mDatabase != null) {
                Cursor cursor = MainActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    handwritingId = cursor.getInt(0);
                }
                cursor.close();
            }
        }


        SQL = "insert into " + MemoDatabase.TABLE_MEMO +
                "(INPUT_DATE, CONTENT_TEXT, ID_PHOTO, ID_VIDEO, ID_VOICE, ID_HANDWRITING) values(" +
                "DATETIME('" + mDateStr + "'), " +
                "'"+ mMemoStr + "', " +
                "'"+ photoId + "', " +
                "'"+ "" + "', " +
                "'"+ "" + "', " +
                "'"+ "" + "')";


        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();

    }

    /**
     * 데이터베이스 레코드 수정
     */
    private void modifyInput() {

        Intent intent = getIntent();

        String photoFilename = insertPhoto();
        int photoId = -1;

        String SQL = null;

        if (photoFilename != null) {
            // query picture id
            SQL = "select _ID from " + MemoDatabase.TABLE_PHOTO + " where URI = '" + photoFilename + "'";

            if (MainActivity.mDatabase != null) {
                Cursor cursor = MainActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    photoId = cursor.getInt(0);
                }
                cursor.close();

                mMediaPhotoUri = photoFilename;

                SQL = "update " + MemoDatabase.TABLE_MEMO +
                        " set " +
                        " ID_PHOTO = '" + photoId + "'" +
                        " where _id = '" + mMemoId + "'";

                if (MainActivity.mDatabase != null) {
                    MainActivity.mDatabase.rawQuery(SQL);
                }

                mMediaPhotoId = String.valueOf(photoId);
            }
        } else if(isPhotoCanceled && isPhotoFileSaved) {
            SQL = "delete from " + MemoDatabase.TABLE_PHOTO +
                    " where _ID = '" + mMediaPhotoId + "'";

            if (MainActivity.mDatabase != null) {
                MainActivity.mDatabase.execSQL(SQL);
            }

            File photoFile = new File(BasicInfo.FOLDER_PHOTO + mMediaPhotoUri);
            if (photoFile.exists()) {
                photoFile.delete();
            }

            SQL = "update " + MemoDatabase.TABLE_MEMO +
                    " set " +
                    " ID_PHOTO = '" + photoId + "'" +
                    " where _id = '" + mMemoId + "'";

            if (MainActivity.mDatabase != null) {
                MainActivity.mDatabase.rawQuery(SQL);
            }

            mMediaPhotoId = String.valueOf(photoId);
        }


        String handwritingFileName = insertHandwriting();
        int handwritingId = -1;

        if (handwritingFileName != null) {
            // query picture id
            SQL = "select _ID from " + MemoDatabase.TABLE_HANDWRITING + " where URI = '" + handwritingFileName + "'";

            if (MainActivity.mDatabase != null) {
                Cursor cursor = MainActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    handwritingId = cursor.getInt(0);
                }
                cursor.close();

                mMediaHandwritingUri = handwritingFileName;

                SQL = "update " + MemoDatabase.TABLE_MEMO +
                        " set " +
                        " ID_HANDWRITING = '" + handwritingId + "' " +
                        " where _id = '" + mMemoId + "'";

                if (MainActivity.mDatabase != null) {
                    MainActivity.mDatabase.rawQuery(SQL);
                }

                mMediaHandwritingId = String.valueOf(handwritingId);
            }
        } else if(isHandwritingCanceled && isHandwritingFileSaved) {
            SQL = "delete from " + MemoDatabase.TABLE_HANDWRITING +
                    " where _ID = '" + mMediaHandwritingId + "'";

            if (MainActivity.mDatabase != null) {
                MainActivity.mDatabase.execSQL(SQL);
            }

            File handwritingFile = new File(BasicInfo.FOLDER_HANDWRITING + mMediaHandwritingUri);
            if (handwritingFile.exists()) {
                handwritingFile.delete();
            }

            SQL = "update " + MemoDatabase.TABLE_MEMO +
                    " set " +
                    " ID_HANDWRITING = '" + handwritingId + "' " +
                    " where _id = '" + mMemoId + "'";

            if (MainActivity.mDatabase != null) {
                MainActivity.mDatabase.rawQuery(SQL);
            }

            mMediaHandwritingId = String.valueOf(handwritingId);
        }



        // update memo info
        SQL = "update " + MemoDatabase.TABLE_MEMO +
                " set " +
                " INPUT_DATE = DATETIME('" + mDateStr + "'), " +
                " CONTENT_TEXT = '" + mMemoStr + "'" +
                " where _id = '" + mMemoId + "'";


        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        intent.putExtra(BasicInfo.KEY_MEMO_TEXT, mMemoStr);
        intent.putExtra(BasicInfo.KEY_ID_PHOTO, mMediaPhotoId);
        intent.putExtra(BasicInfo.KEY_ID_VIDEO, mMediaVideoId);
        intent.putExtra(BasicInfo.KEY_ID_VOICE, mMediaVoiceId);
        intent.putExtra(BasicInfo.KEY_ID_HANDWRITING, mMediaHandwritingId);
        intent.putExtra(BasicInfo.KEY_URI_PHOTO, mMediaPhotoUri);
        intent.putExtra(BasicInfo.KEY_URI_VIDEO, mMediaVideoUri);
        intent.putExtra(BasicInfo.KEY_URI_VOICE, mMediaVoiceUri);
        intent.putExtra(BasicInfo.KEY_URI_HANDWRITING, mMediaHandwritingUri);

        setResult(RESULT_OK, intent);
        finish();
    }



    /**
     * 앨범의 사진을 사진 폴더에 복사한 후, PICTURE 테이블에 사진 정보 추가
     * 이미지의 이름은 현재 시간을 기준으로 한 getTime() 값의 문자열 사용
     *
     * @return 새로 추가된 이미지의 이름
     */

    private String insertPhoto() {
        String photoName = null;

        if (isPhotoCaptured) { // captured Bitmap
            try {
                if (mMemoMode != null && mMemoMode.equals(BasicInfo.MODE_MODIFY)) {


                    String SQL = "delete from " + MemoDatabase.TABLE_PHOTO +
                            " where _ID = '" + mMediaPhotoId + "'";

                    if (MainActivity.mDatabase != null) {
                        MainActivity.mDatabase.execSQL(SQL);
                    }

                    File previousFile = new File(BasicInfo.FOLDER_PHOTO + mMediaPhotoUri);
                    if (previousFile.exists()) {
                        previousFile.delete();
                    }
                }


                File photoFolder = new File(BasicInfo.FOLDER_PHOTO);

                //폴더가 없다면 폴더를 생성한다.
                if(!photoFolder.isDirectory()){

                    photoFolder.mkdirs();
                }

                // Temporary Hash for photo file name
                photoName = createFilename();

                FileOutputStream outstream = new FileOutputStream(BasicInfo.FOLDER_PHOTO + photoName);
                resultPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
                outstream.close();


                if (photoName != null) {


                    // INSERT PICTURE INFO
                    String SQL = "insert into " + MemoDatabase.TABLE_PHOTO + "(URI) values(" + "'" + photoName + "')";
                    if (MainActivity.mDatabase != null) {
                        MainActivity.mDatabase.execSQL(SQL);
                    }
                }

            } catch (IOException ex) {

            }


        }
        return photoName;
    }

    private String insertHandwriting() {
        String handwritingName = null;

        if (isHandwritingMade) { // captured Bitmap
            try {

                if (mMemoMode != null && mMemoMode.equals(BasicInfo.MODE_MODIFY)) {


                    String SQL = "delete from " + MemoDatabase.TABLE_HANDWRITING +
                            " where _ID = '" + mMediaHandwritingId + "'";

                    if (MainActivity.mDatabase != null) {
                        MainActivity.mDatabase.execSQL(SQL);
                    }

                    File previousFile = new File(BasicInfo.FOLDER_HANDWRITING + mMediaHandwritingUri);
                    if (previousFile.exists()) {
                        previousFile.delete();
                    }
                }


                File handwritingFolder = new File(BasicInfo.FOLDER_HANDWRITING);

                //폴더가 없다면 폴더를 생성한다.
                if(!handwritingFolder.isDirectory()){

                    handwritingFolder.mkdirs();
                }

                // Temporal Hash for handwriting file name

                handwritingName = createFilename();

                FileOutputStream outstream = new FileOutputStream(BasicInfo.FOLDER_HANDWRITING + handwritingName);
                // MIKE 20101215
                resultHandwritingBitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
                // MIKE END
                outstream.close();


                if (handwritingName != null) {


                    // INSERT HANDWRITING INFO
                    String SQL = "insert into " + MemoDatabase.TABLE_HANDWRITING + "(URI) values(" + "'" + handwritingName + "')";
                    if (MainActivity.mDatabase != null) {
                        MainActivity.mDatabase.execSQL(SQL);
                    }
                }

            } catch (IOException ex) {

            }


        }
        return handwritingName;
    }




    private String createFilename() {
        Date curDate = new Date();
        String curDateStr = String.valueOf(curDate.getTime());

        return curDateStr;
    }


    public void setMediaLayout() {
        isPhotoCaptured = false;
        isVideoRecorded = false;
        isVoiceRecorded = false;
        isHandwritingMade = false;

        mVideoBtn = (Button)findViewById(R.id.insert_videoBtn);
        mVoiceBtn = (Button)findViewById(R.id.insert_voiceBtn);

    }

    private void setCalendar(){
        insertDateButton = (Button) findViewById(R.id.insert_dateBtn);
        insertDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String mDateStr = insertDateButton.getText().toString();
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                try {
                    date = BasicInfo.dateDayNameFormat.parse(mDateStr);
                } catch(Exception ex) {

                }

                calendar.setTime(date);

                new DatePickerDialog(
                        MemoInsertActivity.this,
                        dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show();

            }
        });

        Date curDate = new Date();
        mCalendar.setTime(curDate);

        int year = mCalendar.get(Calendar.YEAR);
        int monthOfYear = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        insertDateButton.setText(year + "년 " + (monthOfYear+1) + "월 " + dayOfMonth + "일");

    }


    /**
     * 날짜 설정 리스너
     */
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(year, monthOfYear, dayOfMonth);
            insertDateButton.setText(year + "년 " + (monthOfYear+1) + "월 " + dayOfMonth + "일");
        }
    };


    /**
     * 일자와 메모 확인
     */
    private boolean parseValues() {
        String insertDateStr = insertDateButton.getText().toString();
        try {
            Date insertDate = BasicInfo.dateDayNameFormat.parse(insertDateStr);
            mDateStr = BasicInfo.dateDayFormat.format(insertDate);
        } catch(ParseException ex) {

        }

        String memotxt = mMemoEdit.getText().toString();
        mMemoStr = memotxt;

        if (mMemoStr.trim().length() < 1) {
            showDialog(BasicInfo.CONFIRM_TEXT_INPUT);
            return false;
        }

        return true;
    }


    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = null;

        switch(id) {
            case BasicInfo.CONFIRM_TEXT_INPUT:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("메모");
                builder.setMessage("텍스트를 입력하세요.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                break;

            case BasicInfo.CONTENT_PHOTO:
                builder = new AlertDialog.Builder(this);

                mSelectdContentArray = R.array.array_photo;
                builder.setTitle("선택하세요");
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mChoicedArrayItem = whichButton;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(mChoicedArrayItem == 0 ) {
                            showPhotoCaptureActivity();
                        } else if(mChoicedArrayItem == 1) {
                            showPhotoSelectionActivity();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {


                    }
                });

                break;

            case BasicInfo.CONTENT_PHOTO_EX:
                builder = new AlertDialog.Builder(this);

                mSelectdContentArray = R.array.array_photo_ex;
                builder.setTitle("선택하세요");
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mChoicedArrayItem = whichButton;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(mChoicedArrayItem == 0) {
                            showPhotoCaptureActivity();
                        } else if(mChoicedArrayItem == 1) {
                            showPhotoSelectionActivity();
                        } else if(mChoicedArrayItem == 2) {
                            isPhotoCanceled = true;
                            isPhotoCaptured = false;

                            mPhoto.setImageResource(R.drawable.image_add);
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                break;

            case BasicInfo.CONFIRM_DELETE:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("메모");
                builder.setMessage("메모를 삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteMemo();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dismissDialog(BasicInfo.CONFIRM_DELETE);
                    }
                });

            default:
                break;
        }

        return builder.create();
    }


    /**
     * 메모 삭제
     */
    private void deleteMemo() {

        // delete photo record

        String SQL = "delete from " + MemoDatabase.TABLE_PHOTO +
                " where _ID = '" + mMediaPhotoId + "'";

        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        File photoFile = new File(BasicInfo.FOLDER_PHOTO + mMediaPhotoUri);
        if (photoFile.exists()) {
            photoFile.delete();
        }
        // delete handwriting record

        SQL = "delete from " + MemoDatabase.TABLE_HANDWRITING +
                " where _ID = '" + mMediaHandwritingId + "'";

        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        File handwritingFile = new File(BasicInfo.FOLDER_HANDWRITING + mMediaHandwritingUri);
        if (handwritingFile.exists()) {
            handwritingFile.delete();
        }


        // delete memo record

        SQL = "delete from " + MemoDatabase.TABLE_MEMO +
                " where _id = '" + mMemoId + "'";

        if (MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        setResult(RESULT_OK);

        finish();
    }

    public void showPhotoCaptureActivity() {
        Intent intent = new Intent(getApplicationContext(), PhotoCaptureActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY);
    }

    public void showPhotoSelectionActivity() {
        Intent intent = new Intent(getApplicationContext(), PhotoSelectionActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY);
    }

    /**
     * 다른 액티비티로부터의 응답 처리
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode) {
            case BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY:  // 사진 찍는 경우


                if (resultCode == RESULT_OK) {


                    boolean isPhotoExists = checkCapturedPhotoFile();
                    if (isPhotoExists) {


                        resultPhotoBitmap = BitmapFactory.decodeFile(BasicInfo.FOLDER_PHOTO + "captured");

                        tempPhotoUri = "captured";

                        mPhoto.setImageBitmap(resultPhotoBitmap);
                        isPhotoCaptured = true;

                        mPhoto.invalidate();
                    } else {

                    }
                }

                break;

            case BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY:  // 사진을 앨범에서 선택하는 경우


                if (resultCode == RESULT_OK) {


                    Uri getPhotoUri = intent.getParcelableExtra(BasicInfo.KEY_URI_PHOTO);
                    try {

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;

                        resultPhotoBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(getPhotoUri), null, options);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    mPhoto.setImageBitmap(resultPhotoBitmap);
                    isPhotoCaptured = true;

                    mPhoto.invalidate();
                }

                break;

            case BasicInfo.REQ_HANDWRITING_MAKING_ACTIVITY:  // 손글씨를 저장하는 경우


                if (resultCode == RESULT_OK) {
                    boolean isHandwritingExists = checkMadeHandwritingFile();
                    if(isHandwritingExists) {
                        resultHandwritingBitmap = BitmapFactory.decodeFile(BasicInfo.FOLDER_HANDWRITING + "made");
                        tempHandwritingUri = "made";

                        isHandwritingMade = true;

                        insert_handwriting.setImageBitmap(resultHandwritingBitmap);
                    }
                }

                break;

        }
    }


    /**
     * 저장된 사진 파일 확인
     */
    private boolean checkCapturedPhotoFile() {
        File file = new File(BasicInfo.FOLDER_PHOTO + "captured");
        if(file.exists()) {
            return true;
        }

        return false;
    }

    /**
     * 저장된 손글씨 파일 확인
     */
    private boolean checkMadeHandwritingFile() {
        File file = new File(BasicInfo.FOLDER_HANDWRITING + "made");
        if(file.exists()) {
            return true;
        }

        return false;
    }


}
