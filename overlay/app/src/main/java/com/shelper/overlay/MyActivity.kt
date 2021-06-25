package com.shelper.overlay

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.core.content.ContextCompat
import org.json.JSONException
import org.json.JSONObject
import java.lang.Long
import java.util.concurrent.ExecutionException

class MyActivity : Activity() {
    var listView: ListView? = null
    var user_id = 0
    var contents_id = 0
    var contents_userid = 0
    var contents_name = ""
    var image_path = ""
    var views = 0
    var timestamp: String? = null
    var listViewAdapter: ListViewAdapter? = null
    var searchText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mycontents)
        listView = findViewById(R.id.my_list_view)
        listViewAdapter = ListViewAdapter()
        listView!!.setAdapter(listViewAdapter)
        val intent = intent
        user_id = intent.getIntExtra("userid", 0)
        showResult(intent.getStringExtra("search_result"))
        listViewAdapter!!.notifyDataSetChanged()
    }

    fun showResult(result: String?) {
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
                image_path = item.getString("image_path")
                timestamp = item.getString("created_at")
                listViewAdapter!!.addItem(ContextCompat.getDrawable(this, R.drawable.ic_baseline_account_circle_56), contents_name, contents_id, views, timestamp, image_path)
            }
        } catch (e: JSONException) {
            Toast.makeText(applicationContext,"즐겨찾기가 없습니다.", Toast.LENGTH_SHORT).show()
            Log.d(ContentValues.TAG, "showResult : ", e)
        }
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as ListViewList
            val title = item.name
            val desc = item.id.toString() + ""
            var pictureA = ""
            val icon = item.iconDrawable
            val str = """
                 id : ${Long.toString(id)}
                 title : $title
                 desc : $desc
                 """.trimIndent()
            val asyncPictureDownload = AsyncPictureDownload(this@MyActivity)
            try {
                pictureA = asyncPictureDownload.execute("https://shelper3.azurewebsites.net/downloadpic.php?id=" + item.id, "" + item.id).get()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val intent = Intent(this@MyActivity, InformationPopupActivity::class.java)
            intent.putExtra("id", item.id)
            intent.putExtra("userid", user_id)
            intent.putExtra("name", item.name)
            intent.putExtra("picture", pictureA)
            intent.putExtra("image_path", item.getImage_path())
            startActivity(intent)
        }
    }
}