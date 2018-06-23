package com.yuan.yuanbo

import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

/**
 * Created by yuan on 2016/6/18.
 */
class SaleFSAdapter(context: MainActivity, sqlite: SQLiteDatabase) : DataAdapter(context, sqlite) {
    override fun initData() {
        for (i in 11..33) {
            val m = HashMap<String, String>()
            m["sjd"] = "12:00:00-14:00:00"
            m["lks"] = i.toString()
            m["yye"] = "333.00"
            m["kdj"] = "89.00"
            mData.add(m)
        }
    }

    override fun compute() {
    }

    override fun setSort(v: View) {

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val v: View
        val vh: ViewHolder
        if (convertView == null) {
            v = mInflater.inflate(R.layout.sale_fs_item, null)
            vh = ViewHolder(v)
            v.tag = vh
        } else {
            v = convertView
            vh = v.tag as ViewHolder
        }
        val m = mData[position]
        vh.sjd.text = m["sjd"]
        vh.lks.text = m["lks"]
        vh.yye.text = m["yye"]
        vh.kdj.text = m["kdj"]
        return v
    }

    private class ViewHolder(v: View) {
        var sjd = v.findViewById(R.id.sale_fs_sjd) as TextView
        var lks = v.findViewById(R.id.sale_fs_lks) as TextView
        var yye = v.findViewById(R.id.sale_fs_yye) as TextView
        var kdj = v.findViewById(R.id.sale_fs_kdj) as TextView
    }
}