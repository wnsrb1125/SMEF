package com.shelper.overlay

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.*

class ProfileListViewAdpater : BaseAdapter() {
    private val listViewLists = ArrayList<ProfileViewList>()

    override fun getCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItem(position: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getItemId(position: Int): Long {
        TODO("Not yet implemented")
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val pos = position
        val context = parent!!.context
        var real_convertView = convertView

        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            real_convertView = inflater.inflate(R.layout.listview_item, parent, false)
        }
        val iconImageView:ImageView = real_convertView!!.findViewById<View>(R.id.Thumsnail) as ImageView
        val titleText:TextView = real_convertView!!.findViewById<View>(R.id.titleText) as TextView
        val count:TextView = real_convertView!!.findViewById<View>(R.id.count2) as TextView
        val timeText:TextView = real_convertView!!.findViewById<View>(R.id.timeText) as TextView
        val desc:TextView = real_convertView!!.findViewById<View>(R.id.timeText) as TextView

        val profileViewList:ProfileViewList = listViewLists.get(position)

        iconImageView.setImageDrawable(profileViewList.getIconDrawable())
        titleText.setText(profileViewList.getName())
        desc.setText(profileViewList.getViews())
        timeText.setText(profileViewList.getTimestamp())
        count.setText(profileViewList.getViews())

        return real_convertView

    }
    fun addItem(icon: Drawable?, title: String?, id: Int, views: Int, timestamp: String?) {
        val item = ProfileViewList()
        item.setIconDrawable(icon)
        item.setName(title)
        item.setViews(views)
        item.setId(id)
        item.setTimestamp(timestamp)
        listViewLists.add(item)
    }

}