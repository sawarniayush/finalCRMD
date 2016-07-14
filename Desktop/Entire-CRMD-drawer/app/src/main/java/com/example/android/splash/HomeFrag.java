package com.example.android.splash;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.splash.data.DBHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFrag extends Fragment {
    TextView nameTextView,deptTextView;

    public HomeFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_home2, container, false);
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> pendingAdapter;

        nameTextView = (TextView) rootView.findViewById(R.id.name_fill);
        String name = getActivity().getIntent().getStringExtra("name");
        String dept = getActivity().getIntent().getStringExtra("dept");
        deptTextView = (TextView) rootView.findViewById(R.id.dept_fill);
        nameTextView.setText(name);
        deptTextView.setText(dept);
//        String supervisor[] = getIntent().getStringArrayExtra("supervisor");

//        JSONObject jo = jo.getJSONObject()
//            Log.v("checking: ",BackgroundTask.sArray[0]);
      //  Log.v("checking: ", BackgroundTask.sString[0]);
//            Log.v("checking type: ",BackgroundTask.supervisor.getClass().getName());
//        Log.v("checking type: ",BackgroundTask.sString.getClass().getName());
//        Log.v("checking: ",BackgroundTask.sString);

        List<String> complaintList =  getTableRows();

        pendingAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_view_item, R.id.textView, complaintList);
//
//
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(pendingAdapter);

        listView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.edit) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
        return rootView;
    }
    public List<String> getTableRows() {
        List<String> result = new ArrayList<String>();
        DBHelper db;
        db = new DBHelper(this.getContext());
        Cursor cursor = db.getListSuperVisor();
        //   cursor.moveToFirst();
        try
        {
            while (cursor.moveToNext()) {
                String columnValue = cursor.getString(0);

                result.add(columnValue);
            }}catch (Exception e){}
        cursor.close();
        return result;
    }

}
