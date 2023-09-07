package com.minyazev.appflickrrecentphotos;



import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AppFlickrRecentPhotos";
    public static final int SUCCESS = 1;
    public static final int ERROR = 2;
    private List <String> imageURLs;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private Handler mHandler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage( Message msg) {
            if(msg.what == SUCCESS){
                String responseData = (String)msg.obj;
                imageURLs = parseJSONResult(responseData);
                adapter = new ImageAdapter(imageURLs, getApplicationContext());
                recyclerView.setAdapter(adapter);

            } else{
                if(msg.what == ERROR){
                    Log.e(TAG, "handleMessage: Ошибка при получении данных" );
                    //tvData.setText("Ошибка при получении данных");
                }
            }

        }
    };

    private List<String> parseJSONResult (String jsonString) {

        List<String> urls = new ArrayList<>();

        try {
            JSONObject jsonBody = new JSONObject(jsonString);
            JSONObject photosJSONObject = jsonBody.getJSONObject("photos");
            JSONArray photosJSONArray = photosJSONObject.getJSONArray("photo");

            String farm, id, server, secret, current_url = "";

            for (int i = 0; i < photosJSONArray.length() && i < 100; i++) {
                JSONObject photoJSONObject = photosJSONArray.getJSONObject(i);

                farm = photoJSONObject.getString("farm");
                id = photoJSONObject.getString("id");
                server = photoJSONObject.getString("server");
                secret = photoJSONObject.getString("secret");

                current_url = "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + ".jpg";
                Log.d(TAG, "parseJSONResult: " + i + " current_url:" + current_url);

                urls.add(current_url);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return urls;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String urlString = "https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=c7e5c1c132198370b0a14311c5112530&safe_search=1&format=json&nojsoncallback=1";
        DataFetcherThread thread = new DataFetcherThread(mHandler, urlString);
        thread.start();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

    }
}
