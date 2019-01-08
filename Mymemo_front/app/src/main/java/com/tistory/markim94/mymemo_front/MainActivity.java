package com.tistory.markim94.mymemo_front;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView memoListView;
    Button newMemoBtn;
    Button closeBtn;
    // 리스트 어댑터
    MemoListAdapter memoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 새 메모 추가, 닫기 버튼에 대한 객체 참조
        newMemoBtn = (Button) findViewById(R.id.newMemoBtn);
        closeBtn = (Button) findViewById(R.id.closeBtn);

        // 메모 리스트에 대한 객체 참조
        memoListView = (ListView) findViewById(R.id.memoList);

        memoListAdapter = new MemoListAdapter(this);
        memoListView.setAdapter(memoListAdapter);

        memoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 해당 포지션 클릭시 메모 보여주기(메소드 생성)
                viewMemo(position);
            }
        });


//        // 테스트 데이터 로딩
//        loadMemoListData();
    }

//    private void loadMemoListData(){
//        // mItem 객체 생성
//        MemoListItem mItem = new MemoListItem("1", "2011-06-10 10:20", "오늘은 좋은 날!", null, null, null, null, null, null, null, null);
//        // 어댑터 아이템 추가
//        memoListAdapter.addItem(mItem);
//        // 리스트뷰 갱신
//        memoListAdapter.notifyDataSetChanged();
//    }


    // 각 포지션에 따른 메모 데이터 불러오기.
    private void viewMemo(int position){

    }
}
