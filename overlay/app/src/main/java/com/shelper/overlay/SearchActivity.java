package com.shelper.overlay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

public class SearchActivity extends Activity {

    private ListView listView;
    private int user_id = 0;
    private int contents_id = 0;
    private int contents_userid = 0;
    private String image_path = "";
    private String contents_name = "";
    private String search = "";
    private int views;
    private String timestamp;
    private String resultt = "";
    private ListViewAdapter listViewAdapter;
    private Button searchButton;
    private EditText searchText;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);

        listView = findViewById(R.id.list_view);
        searchButton = findViewById(R.id.searchView);
        searchText = findViewById(R.id.searchText);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String search_word = searchText.getText().toString();
                    if (search_word.length() > 0) {
                    AsyncSearch asyncSearch = new AsyncSearch(SearchActivity.this);
                    try {
                        result = asyncSearch.execute(search_word).get();
                        listViewAdapter = new ListViewAdapter();
                        listView.setAdapter(listViewAdapter);
                        showResult(result);
                        listViewAdapter.notifyDataSetChanged();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        listViewAdapter = new ListViewAdapter();
        listView.setAdapter(listViewAdapter);

        Intent intent = getIntent();
        search = intent.getStringExtra("search");
        searchText.setText(search);
        user_id = intent.getIntExtra("userid",0);
        showResult(intent.getStringExtra("search_result"));
        listViewAdapter.notifyDataSetChanged();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }
    public void showResult(final String result) {

        String TAG_JSON="webnautes";

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                contents_id = item.getInt("id");
                contents_userid = item.getInt("userid");
                image_path = item.getString("image_path");
                contents_name = item.getString("content_name");
                views = item.getInt("views");
                timestamp = item.getString("created_at");
                listViewAdapter.addItem(ContextCompat.getDrawable(this,R.drawable.ic_baseline_account_circle_56),contents_name,contents_id,views,timestamp,image_path);
            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String favoresult = "";
                ListViewList item = (ListViewList) parent.getItemAtPosition(position);
                AsyncFavorInfo asyncFavorInfo = new AsyncFavorInfo();
                try {
                    favoresult = asyncFavorInfo.execute(user_id, item.getId()).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String title = item.getName();
                String desc = item.getId()+"";
                String pictureA = "";
                Drawable icon = item.getIconDrawable();

                String str = "id : " + Long.toString(id) + "\r\ntitle : " + title + "\r\ndesc : " + desc;
                Intent intent = new Intent(SearchActivity.this,InformationPopupActivity.class);
                intent.putExtra("id",item.getId());
                intent.putExtra("tf",favoresult);
                intent.putExtra("userid", user_id);
                intent.putExtra("contents_userid", contents_userid);
                intent.putExtra("name",item.getName());
                intent.putExtra("picture",pictureA);
                intent.putExtra("image_path" ,item.getImage_path());
                startActivityForResult(intent,2222);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2222) {
            if (resultCode == 1125) {
                String search_word = searchText.getText().toString();
                AsyncSearch asyncSearch = new AsyncSearch(SearchActivity.this);
                try {
                    result = asyncSearch.execute(search_word).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showResult(result);
                listViewAdapter.notifyDataSetChanged();
            }
        }
    }
}
