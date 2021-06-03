package com.shelper.overlay

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import androidx.core.content.ContextCompat
import org.json.JSONException
import org.json.JSONObject

class ProfileActivity : Activity() {

    private var listView:ListView? = null;
    private var listViewLists:ArrayList<ProfileViewList>? = null;
    private var contents_id = 0
    private var contents_userid = 0
    private var contents_name = ""
    private var views = 0
    private var timestamp: String? = null
    private var resultt = ""
    private var listViewAdapter: ProfileListViewAdpater? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        listView = findViewById(R.id.list_view_profile)
        listViewAdapter = ProfileListViewAdpater()

        val intent = intent
        showResult(intent.getStringExtra("search_result"))
        listViewAdapter!!.notifyDataSetChanged()
    }

    fun showResult(result: String?): java.util.ArrayList<ProfileViewList> {
        listViewLists = java.util.ArrayList<ProfileViewList>()
        val TAG_JSON = "webnautes"
        try {
            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray(TAG_JSON)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                contents_id = item.getInt("id")
                contents_userid = item.getInt("userid")
                contents_name = item.getString("content_name")
                views = item.getInt("views")
                timestamp = item.getString("created_at")
                listViewAdapter?.addItem(ContextCompat.getDrawable(this, R.drawable.ic_android_black_24dp), contents_name, contents_id, views, timestamp)
            }
        } catch (e: JSONException) {
            Log.d(ContentValues.TAG, "showResult : ", e)
        }
        listView!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as ListViewList
            val title = item.name
            val desc = item.id.toString() + ""
            val icon = item.iconDrawable
            val str = """
                 id : ${java.lang.Long.toString(id)}
                 title : $title
                 desc : $desc
                 """.trimIndent()
            val intent = Intent(this, InformationPopupActivity::class.java)
            intent.putExtra("id", item.id)
            startActivity(intent)
        }
        return listViewLists as java.util.ArrayList<ProfileViewList>
    }
}