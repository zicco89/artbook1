package com.zikriercaglar.setahatkitabm.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.zikriercaglar.setahatkitabm.MainActivity;
import com.zikriercaglar.setahatkitabm.R;
import com.zikriercaglar.setahatkitabm.fragment.ArtFragment;
import com.zikriercaglar.setahatkitabm.fragment.MainFragment;
import com.zikriercaglar.setahatkitabm.fragment.MainFragmentDirections;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.PostHolder> {
    ArrayList<String> nameList;
    ArrayList<byte[]> imageList;
    ArrayList<Integer> idList;
    public static int id;

    public CustomAdapter(ArrayList<String> nameList, ArrayList<byte[]> imageList, ArrayList<Integer> idList){
        this.nameList = nameList;
        this.imageList = imageList;
        this.idList = idList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view,parent,false);

        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        Bitmap image = BitmapFactory.decodeByteArray(imageList.get(position),0,imageList.get(position).length);
        String name = nameList.get(position);

        holder.nameText.setText(name);
        holder.image.setImageBitmap(ArtFragment.resizePicture(image,200));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = idList.get(position);
                MainFragmentDirections.ActionMainFragmentToArtFragment action = MainFragmentDirections.actionMainFragmentToArtFragment();
                action.setPosition(position);
                action.setName(name);
                action.setİnfo("old");
                Navigation.findNavController(v).navigate(action);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Silme");
                alert.setMessage(name + " Silinsin Mi?");
                alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(v.getContext(),name +" Silinmedi",Toast.LENGTH_LONG).show();
                    }
                });
                alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase database = MainActivity.database;

                        String sorgu = "Delete from arts where id ="+idList.get(position);
                        database.execSQL(sorgu);

                        MainFragment.adapter.notifyDataSetChanged();

                        NavDirections action = MainFragmentDirections.actionMainFragmentSelf();
                        Navigation.findNavController(v).navigate(action);

                        Toast.makeText(v.getContext(),name + " Silindi",Toast.LENGTH_LONG).show();
                    }
                });

                alert.show();

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {

        return idList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        TextView nameText;
        ImageView image;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.rec_nametext);
            image = itemView.findViewById(R.id.rec_imageview);
        }
    }
}
