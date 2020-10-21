package com.zikriercaglar.setahatkitabm.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zikriercaglar.setahatkitabm.MainActivity;
import com.zikriercaglar.setahatkitabm.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ArtFragment extends Fragment {
    public static ImageView imageView;
    Button updateButton, saveButton, backButton;
    EditText nameText;
    TextView nameTextView;
    public static Bitmap selectedImage;
    public static Uri imageUri = null;
    String info;
    Bitmap databaseImage;
    SQLiteDatabase database;

    public ArtFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_art, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saveButton = view.findViewById(R.id.save_button);
        updateButton = view.findViewById(R.id.update_button);
        backButton = view.findViewById(R.id.back_button);
        nameText = view.findViewById(R.id.nameText);
        imageView = view.findViewById(R.id.imageView);
        nameTextView = view.findViewById(R.id.nameTextView);


        info = ArtFragmentArgs.fromBundle(getArguments()).getİnfo();

        if (!info.equals("update")){
            MainActivity.menu1.removeItem(R.id.add_art);
            MainActivity.menu1.removeItem(R.id.exit);
            MainActivity.menu1.removeItem(R.id.back);
            MainActivity.menu1.add(1,R.id.back,2,"Geri Dön");
            MainActivity.menu1.add(1,R.id.exit,4,"Çıkış");

        }


        if (info.equals("new")){

            updateButton.setVisibility(View.INVISIBLE);
            nameTextView.setVisibility(View.INVISIBLE);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage(v);
                }
            });
        }
        else if (info.equals("old")){
            imageView.setClickable(false);
            updateButton.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
            nameText.setVisibility(View.INVISIBLE);
            MainActivity.menu1.add(1,R.id.update_menu,3,"Güncelle");

            String name = ArtFragmentArgs.fromBundle(getArguments()).getName();
            int position = ArtFragmentArgs.fromBundle(getArguments()).getPosition();
            byte[] imageBytes = MainFragment.imageList.get(position);

            nameTextView.setText(name);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length));
        }
        else if(info.equals("update")){


            MainActivity.menu1.removeItem(R.id.update_menu);
            saveButton.setVisibility(View.INVISIBLE);
            updateButton.setVisibility(View.VISIBLE);

            int id = ArtFragmentArgs.fromBundle(getArguments()).getİd();
            database = MainActivity.database;

            String query = "Select * From arts where id ="+id;

            Cursor cursor = database.rawQuery(query,null);

            int nameIx = cursor.getColumnIndex("name");
            int imageIx = cursor.getColumnIndex("image");

            String name = "";
            byte[] image = null;

            while(cursor.moveToNext()){
                name = cursor.getString(nameIx);
                image = cursor.getBlob(imageIx);
            }
            databaseImage = BitmapFactory.decodeByteArray(image,0,image.length);
            cursor.close();
            nameText.setText(name);
            imageView.setImageBitmap(databaseImage);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage(v);
                }
            });

        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(v);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(v);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back(v);
            }
        });
    }

    public void save(View view){

        if(imageUri == null || selectedImage == null){
            Toast.makeText(getActivity().getApplicationContext(),"Lütfen Foto Seçiniz",Toast.LENGTH_LONG).show();
        }
        else{
            if(nameText.getText().toString().trim().equals("")){
                nameText.setText("");
                Toast.makeText(getActivity().getApplicationContext(),"Lütfen İsim Giriniz",Toast.LENGTH_LONG).show();
            }
            else {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG,80,outputStream);
                byte[] imageByte = outputStream.toByteArray();

                try {
                    //database = SQLiteDatabase.openOrCreateDatabase("ARTS",null);

                    database = MainActivity.database;
                    database.execSQL("Create Table If Not Exists arts(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,image BLOB)");

                    //veri kaydetme

                    String saveQuery = "Insert Into arts(name,image) values(?,?)";
                    Object[] objects = {nameText.getText().toString().trim(),imageByte};

                    database.execSQL(saveQuery,objects);

                }catch (Exception e){
                    e.printStackTrace();
                }

                //NavDirections action = ArtFragmentDirections.actionArtFragmentToMainFragment();
                ArtFragmentDirections.ActionArtFragmentToMainFragment action = ArtFragmentDirections.actionArtFragmentToMainFragment();
                action.setBack(1);
                Navigation.findNavController(view).navigate(action);

            }
        }
    }
    public void update(View view){

        if (nameText.getText().toString().trim().equals("")){
            Toast.makeText(getActivity().getApplicationContext(),"Lütfen İsim Giriniz",Toast.LENGTH_LONG).show();
        }
        else{
            String name = nameText.getText().toString();

            if (selectedImage == null){
                selectedImage = databaseImage;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG,80,outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            int id = ArtFragmentArgs.fromBundle(getArguments()).getİd();

            database = MainActivity.database;

            String query1 = "Update arts Set name = ? Where id = ?";
            String query2 = "Update arts Set image = ? Where id = ?";
            Object[] newObjects1 = {name,id};
            Object[] newObjects2 = {imageBytes,id};
            database.execSQL(query1,newObjects1);
            database.execSQL(query2,newObjects2);

            ArtFragmentDirections.ActionArtFragmentToMainFragment action = ArtFragmentDirections.actionArtFragmentToMainFragment();
            action.setBack(1);
            Navigation.findNavController(view).navigate(action);
        }
    }
    public void selectImage(View view){
        if (info.equals("new") || info.equals("update")){
            if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            else{
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery,2);
            }
        }

    }
    public void back(View view){
        ArtFragmentDirections.ActionArtFragmentToMainFragment action = ArtFragmentDirections.actionArtFragmentToMainFragment();
        action.setBack(1);
        Navigation.findNavController(view).navigate(action);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null){

            imageUri = data.getData();
            Bitmap newImage;
            try{
                if(Build.VERSION.SDK_INT > 27){

                    ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getApplicationContext().getContentResolver(),imageUri);
                    newImage = ImageDecoder.decodeBitmap(source);
                }
                else{
                    newImage = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(),imageUri);
                }
                selectedImage = resizePicture(newImage,600);
                imageView.setImageBitmap(selectedImage);

            }catch(Exception e){
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap resizePicture(Bitmap picture, int size){

        Bitmap resizeBitmap = picture;
        int height = resizeBitmap.getHeight();
        int weight = resizeBitmap.getWidth();

        if (resizeBitmap.getHeight() < size && resizeBitmap.getWidth() < size){

            return picture;
        }

        else if (resizeBitmap.getHeight() > resizeBitmap.getWidth()){

            int oran = resizeBitmap.getHeight()/resizeBitmap.getWidth();

            height = size;
            weight = (int)size/oran;

        }
        else if (resizeBitmap.getWidth() > resizeBitmap.getHeight()){

            int oran = resizeBitmap.getWidth()/resizeBitmap.getHeight();

            weight = size;
            height = (int)size/oran;
        }
        else{
            weight = size;
            height = size;
        }
        return Bitmap.createScaledBitmap(resizeBitmap,weight,height,true);
    }
}