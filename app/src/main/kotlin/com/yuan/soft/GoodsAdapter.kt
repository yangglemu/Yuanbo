package com.yuan.soft

import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

/**
 * Created by yuan on 2016/6/17.
 */
class GoodsAdapter(context: MainActivity, db: SQLiteDatabase) : DataAdapter(context, db) {

    override fun initData() {
        val cursor = db.rawQuery("select tm,sl,tm*sl as je,shop from goods order by shop,tm asc", null)
        var id = 1
        var tm: Int
        var sl: Int
        var je: Int
        var shop: String
        while (cursor.moveToNext()) {
            val map: HashMap<String, String> = HashMap()
            tm = cursor.getInt(0)
            sl = cursor.getInt(1)
            je = cursor.getInt(2)
            shop = cursor.getString(3)
            map["id"] = (id++).toString()
            map["tm"] = tm.toString()
            map["sl"] = sl.toString()
            map["je"] = je.toString()
            map["shop"] = DataAdapter.shops[shop]!!
            mData.add(map)
        }
        cursor.close()
    }

    override fun compute() {
        val sum_sl = mData.sumBy { it["sl"]!!.toInt() }
        val sum_je = mData.sumBy { it["je"]!!.toInt() }
        val map = HashMap<String, String>()
        map["id"] = "合计"
        map["tm"] = ""
        map["sl"] = sum_sl.toString()
        map["je"] = sum_je.toString()
        map["shop"] = ""
        mData.add(map)
    }

    override fun setSort(v: View) {
        val tm = v.findViewById(R.id.goods_header_tm) as TextView
        val sl = v.findViewById(R.id.goods_header_sl) as TextView
        val je = v.findViewById(R.id.goods_header_je) as TextView

        setClick(tm, "tm")
        setClick(sl, "sl")
        setClick(je, "je")
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val holder: ViewHolder
        val v: View
        if (convertView == null) {
            v = mInflater.inflate(R.layout.goods_item, null)
            holder = ViewHolder(v)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as ViewHolder
        }
        val map = mData[position]
        holder.id.text = map["id"]
        holder.tm.text = map["tm"]
        holder.sl.text = map["sl"]
        holder.je.text = map["je"]
        holder.shop.text = map["shop"]
        return v
    }

    private class ViewHolder(v: View) {
        var id = v.findViewById(R.id.goods_id) as TextView
        var tm = v.findViewById(R.id.goods_tm) as TextView
        var sl = v.findViewById(R.id.goods_sl) as TextView
        var je = v.findViewById(R.id.goods_je) as TextView
        var shop = v.findViewById(R.id.goods_shop) as TextView
    }
}
