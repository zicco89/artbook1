package com.zikriercaglar.setahatkitabm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.zikriercaglar.setahatkitabm.adapter.CustomAdapter;
import com.zikriercaglar.setahatkitabm.fragment.ArtFragment;
import com.zikriercaglar.setahatkitabm.fragment.ArtFragmentArgs;
import com.zikriercaglar.setahatkitabm.fragment.ArtFragmentDirections;
import com.zikriercaglar.setahatkitabm.fragment.MainFragment;
import com.zikriercaglar.setahatkitabm.fragment.MainFragmentArgs;
import com.zikriercaglar.setahatkitabm.fragment.MainFragmentDirections;

public class MainActivity extends AppCompatActivity {
    public static Menu menu1;
    public static SQLiteDatabase database;
    SharedPreferences sharedPreferences;
    int status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = openOrCreateDatabase("ARTS",MODE_PRIVATE,null);
        sharedPreferences = getSharedPreferences("com.zikriercaglar.setahatkitabm",MODE_PRIVATE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(MainFragment.back == 0 ){
            menu1.removeItem(R.id.back);
            menu1.removeItem(R.id.update_menu);
            menu1.removeItem(R.id.exit);
            menu1.add(1,R.id.add_art,1,"Tablo Ekle");
            menu1.add(1,R.id.exit,4,"Çıkış");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu1 = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_menu,menu1);
        menu1.removeItem(R.id.back);
        menu1.removeItem(R.id.update_menu);
        return super.onCreateOptionsMenu(menu1);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_art){

            //NavDirections action = MainFragmentDirections.actionMainFragmentToArtFragment();
            ArtFragment.selectedImage = null;
            MainFragmentDirections.ActionMainFragmentToArtFragment action = MainFragmentDirections.actionMainFragmentToArtFragment();
            action.setİnfo("new");
            Navigation.findNavController(MainActivity.this,R.id.fragment).navigate(action);

        }
        if (item.getItemId() == R.id.back){

            //NavDirections action = ArtFragmentDirections.actionArtFragmentToMainFragment();
            ArtFragmentDirections.ActionArtFragmentToMainFragment action = ArtFragmentDirections.actionArtFragmentToMainFragment();
            action.setBack(1);
            Navigation.findNavController(MainActivity.this,R.id.fragment).navigate(action);
        }
        if (item.getItemId() == R.id.update_menu){
            ArtFragment.selectedImage = null;
            int id = CustomAdapter.id;
            ArtFragmentDirections.ActionArtFragmentSelf action = ArtFragmentDirections.actionArtFragmentSelf();
            action.setİd(id);
            action.setİnfo("update");
            Navigation.findNavController(MainActivity.this,R.id.fragment).navigate(action);
        }

        if (item.getItemId() == R.id.exit){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

            if (requestCode == 2 && resultCode == RESULT_OK && data != null){

                ArtFragment.imageUri = data.getData();
                Bitmap newImage;
                try{
                    if(Build.VERSION.SDK_INT > 27){

                        ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),ArtFragment.imageUri);
                        newImage = ImageDecoder.decodeBitmap(source);
                    }
                    else{
                        newImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),ArtFragment.imageUri);
                    }
                    ArtFragment.selectedImage = ArtFragment.resizePicture(newImage,600);
                    ArtFragment.imageView.setImageBitmap(ArtFragment.selectedImage);

                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            super.onActivityResult(requestCode, resultCode, data);
            sharedPreferences.edit().putInt("status",1).apply();
        }




}