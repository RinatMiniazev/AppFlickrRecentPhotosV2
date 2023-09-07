package com.minyazev.appflickrrecentphotos;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<String> imageURLs;
    public static final String TAG = "ImageAdapter";
    private int count  = 0;

    private DatabaseHelper dbHelper;

    @Override
    public int getItemCount() {
        return imageURLs.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        count++;
        return new ViewHolder(view);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public ViewHolder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Glide.with(holder.itemView.getContext()).load(imageURLs.get(position)).into(holder.imageView);
        holder.imageView.setImageResource(R.drawable.minyazev);
        loadImageFromURL(holder.imageView, imageURLs.get(position));
    }

    private void loadImageFromURL(ImageView imageView, String url){

        byte [] cachedImage = dbHelper.getImage(url);
        if(cachedImage!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(cachedImage,0,cachedImage.length);
            imageView.setImageBitmap(bitmap);
        }

        else {
            new Thread(() -> {
                try {
                    URL imageUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    //Bitmap bitmap = BitmapFactory.decodeStream(input);
                    byte[] imageBytes = IOUtils.toByteArray(input);
                    Bitmap bitmap =  BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);

                    imageView.post(() -> imageView.setImageBitmap(bitmap));

                    dbHelper.saveImage(url, imageBytes);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }

    public ImageAdapter(List<String> imageURLs, Context context){
        this.imageURLs = imageURLs;
       this.dbHelper = new DatabaseHelper(context);
    }

}
