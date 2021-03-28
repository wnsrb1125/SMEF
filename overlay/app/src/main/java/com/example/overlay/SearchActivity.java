package com.example.overlay;

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
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

public class SearchActivity extends Activity {
    private ListView listView;
    private ArrayList<ListViewList> listViewLists;
    private int contents_id = 0;
    private int contents_userid = 0;
    private String contents_name = "";
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
                Integer[] contents = new Integer[3];

                contents_id = item.getInt("id");
                contents_userid = item.getInt("userid");
                contents_name = item.getString("content_name");
                listViewAdapter.addItem(ContextCompat.getDrawable(this,R.drawable.ic_android_black_24dp),contents_name,contents_id);
                Log.d("kkkkk",contents_id+contents_userid+contents_name);
                // ListViewList listViewList = new ListViewList();
                //listViewList.setContents(contents_id,contents_userid,contents_name);
                //listViewLists.add(listViewList);
                // Log.d("*****************", listViewLists.toString());
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
                AsyncTodo task = new AsyncTodo();
                Log.d("resulasdad",item.getId()+"");
                try {
                    resultt = task.execute("https://shelper3.azurewebsites.net/getjson.php?id="+item.getId(),"sound").get();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startServ(item.getId());

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
