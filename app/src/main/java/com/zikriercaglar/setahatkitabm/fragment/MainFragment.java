package com.zikriercaglar.setahatkitabm.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.zikriercaglar.setahatkitabm.MainActivity;
import com.zikriercaglar.setahatkitabm.R;
import com.zikriercaglar.setahatkitabm.adapter.CustomAdapter;

import java.util.ArrayList;

public class MainFragment extends Fragment {
    public static RecyclerView recyclerView;
    ArrayList<String> nameList;
    ArrayList<Integer> idList;
    public static ArrayList<byte[]> imageList;
    SQLiteDatabase database;
    public static CustomAdapter adapter;
    public static int back = 0;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nameList = new ArrayList<>();
        imageList = new ArrayList<>();
        idList = new ArrayList<>();

        getData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        adapter = new CustomAdapter(nameList,imageList,idList);
        recyclerView.setAdapter(adapter);

        back = MainFragmentArgs.fromBundle(getArguments()).getBack();

        if (back == 1){
            MainActivity.menu1.removeItem(R.id.back);
            MainActivity.menu1.removeItem(R.id.update_menu);
            MainActivity.menu1.removeItem(R.id.exit);
            MainActivity.menu1.add(1,R.id.add_art,1,"Tablo Ekle");
            MainActivity.menu1.add(1,R.id.exit,4,"Çıkış");
        }

    }

    public void getData(){

        try{
            database = MainActivity.database;

            Cursor cursor = database.rawQuery("Select * From arts",null);

            int nameIx = cursor.getColumnIndex("name");
            int imageIx = cursor.getColumnIndex("image");
            int idIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()){
                nameList.add(cursor.getString(nameIx));
                imageList.add(cursor.getBlob(imageIx));
                idList.add(cursor.getInt(idIx));
            }

            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}