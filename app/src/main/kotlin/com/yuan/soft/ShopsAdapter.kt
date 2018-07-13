package com.yuan.soft

import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

class ShopsAdapter(context: MainActivity, sqlite: SQLiteDatabase) : DataAdapter(context, sqlite) {
    override fun initData() {
        var id = 1
        val sql = "select name,pname,ifnull(tel,''),ifnull(address,'') from shop order by pname"
        val c = db.rawQuery(sql, null)
        while (c.moveToNext()) {
            val map = HashMap<String, String>()
            map["id"] = (id++).toString()
            map["name"] = c.getString(0)
            map["pname"] = c.getString(1)
            map["tel"] = c.getString(2)
            map["address"] = c.getString(3)
            mData.add(map)
        }
        c.close()
    }

    override fun compute() {
    }

    override fun setSort(v: View) {
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View
        val vh: ViewHolder
        if (convertView == null) {
            v = mInflater.inflate(R.layout.shops_item, null)
            vh = ViewHolder(v)
            v.tag = vh
        } else {
            v = convertView
            vh = v.tag as ViewHolder
        }
        val m = mData[position]
        vh.id.text = m["id"]
        vh.name.text = m["name"]
        vh.pname.text = m["pname"]
        vh.tel.text = m["tel"]
        vh.address.text = m["address"]
        return v
    }

    private class ViewHolder(v: View) {
        val id = v.findViewById(R.id.shops_item_id) as TextView
        val name = v.findViewById(R.id.shops_item_name) as TextView
        val pname = v.findViewById(R.id.shops_item_pname) as TextView
        val tel = v.findViewById(R.id.shops_item_tel) as TextView
        val address = v.findViewById(R.id.shops_item_address) as TextView
    }
}
