package com.example.android.splash;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.splash.data.DBHelper;

import java.util.ArrayList;
import java.util.List;



public class PrevFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    public static ArrayAdapter<String> pendingAdapter;
    public ListView listView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public PrevFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment PrevReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public void PrevReportFragment ()
    {
    }

    public void updateList(){

        //TODO: update fetchinfo from database and update the list adapter
        //TODO: update fetch
        List<String> complaintList= getTableRows();
        pendingAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_view_item, R.id.textView, complaintList);

        listView.setAdapter(pendingAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Toast.makeText(getActivity(),"Entry number"+position,Toast.LENGTH_SHORT).show();
                List<String> data= getListItem(position);
                Bundle args=new Bundle();
                Log.e("TESDSDSDSD",data.get(0)+data.get(1));
                Intent i=new Intent(getActivity(),Main2Activity.class);
                i.putExtra("text",data.get(0));
                i.putExtra("image",data.get(1));
                startActivity(i);
            }
        });


    }

    public void onStart(){
        super.onStart();
        updateList();
        Toast.makeText(getActivity(), "Completed Work", Toast.LENGTH_SHORT).show();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toast.makeText(getActivity(),"Updating List",Toast.LENGTH_SHORT).show();
        View rootView=inflater.inflate(R.layout.fragment_prev, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
       DBHelper db=new DBHelper(this.getContext());
      //  db.deletePrevList();

        return rootView;
    }
    public List<String> getTableRows() {
        List<String> result = new ArrayList<String>();
       DBHelper db;
        db = new DBHelper(this.getContext());
        Cursor cursor = db.getListReportPrev();
        //   cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String columnValue = cursor.getString(6);
            String offline=cursor.getString(5);
            String off;
            if(offline.equals("1"))
                off="online";
            else
                off="offline";
            result.add(columnValue+", Submitted "+off);
        }
        cursor.close();
        return result;
    }
    public List<String> getListItem(int position){

        DBHelper db;
        db = new DBHelper(this.getContext());
        Cursor cursor = db.getListReportPrev();
        cursor.moveToPosition(position);
        List<String> data= new ArrayList<String>();
        data.add(cursor.getString(0));
        data.add(cursor.getString(4));
        return data;
    }
}