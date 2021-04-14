package com.shelper.overlay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SearchActivity extends Activity {

    private ListView listView;
    private ArrayList<ListViewList> listViewLists;
    private int contents_id = 0;
    private int contents_userid = 0;
    private String contents_name = "";
    private int views;
    private String timestamp;
    private String resultt = "";
    private ListViewAdapter listViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);

        listView = findViewById(R.id.list_view);
        listViewAdapter = new ListViewAdapter();
        listView.setAdapter(listViewAdapter);

        Intent intent = getIntent();
        showResult(intent.getStringExtra("search_result"));
        Toast.makeText(SearchActivity.this,"엥",Toast.LENGTH_SHORT).show();
        listViewAdapter.notifyDataSetChanged();


    }
    public ArrayList<ListViewList> showResult(String result) {

        listViewLists = new ArrayList<ListViewList>();
        String TAG_JSON="webnautes";

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                contents_id = item.getInt("id");
                contents_userid = item.getInt("userid");
                contents_name = item.getString("content_name");
                views = item.getInt("views");
                timestamp = item.getString("created_at");
                listViewAdapter.addItem(ContextCompat.getDrawable(this,R.drawable.ic_android_black_24dp),contents_name,contents_id,views,timestamp);
            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewList item = (ListViewList) parent.getItemAtPosition(position);

                String title = item.getName();
                String desc = item.getId()+"";
                Drawable icon = item.getIconDrawable();

                String str = "id : " + Long.toString(id) + "\r\ntitle : " + title + "\r\ndesc : " + desc;

                Intent intent = new Intent(SearchActivity.this,InformationPopupActivity.class);
                intent.putExtra("id",item.getId());
                startActivity(intent);

            }
        });
        return listViewLists;
    }

    private void startServ(int id) {
        Intent intent = new Intent(SearchActivity.this, MyService.class);
        intent.putExtra("edu",resultt);
        intent.putExtra("id",id);
        startService(intent);

    }

}
