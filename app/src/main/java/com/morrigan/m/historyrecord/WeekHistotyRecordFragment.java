package com.morrigan.m.historyrecord;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.morrigan.m.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fei on 2016/10/12.
 */

public class WeekHistotyRecordFragment extends Fragment {
    WeekView weekView;
    public List<HlInfo> llInfo;

    public static WeekHistotyRecordFragment getIntance(HistoryResult result){
        WeekHistotyRecordFragment week= new WeekHistotyRecordFragment();
        Bundle bundle =new Bundle();
        bundle.putSerializable("data",result);
        week.setArguments(bundle);
       return week;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_week, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        weekView=(WeekView)view.findViewById(R.id.weekView);
        HistoryResult result=( HistoryResult) getArguments().getSerializable("data");
        if (result!=null){
            this.llInfo=result.llInfo;
        }
        initData();

    }
    public void initData(){
        if (llInfo==null){
            llInfo=new ArrayList<>();
            for (int i =0; i <7 ; i ++){
                llInfo.add(new HlInfo());
            }
        }
        while (llInfo.size()<7){
            llInfo.add(new HlInfo());
        }
        List<Integer> datas=new ArrayList<>();
        for(int i= 0;i<llInfo.size(); i ++){
            if (llInfo.get(i)==null){
                datas.add(0);
            }else {
                String str=llInfo.get(i).timeLong;
                datas.add(str==null?0:Integer.parseInt(str));
            }

        }
        weekView.refreshData(datas);

    }

}
