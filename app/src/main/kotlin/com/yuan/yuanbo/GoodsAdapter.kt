package com.yuan.yuanbo

import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

/**
 * Created by yuan on 2016/6/17.
 */
class GoodsAdapter(context: MainActivity, sqlite: SQLiteDatabase) : DataAdapter(context, sqlite) {

    override fun initData() {
        val cursor = db.rawQuery("select tm,sl from goods where sl>0 order by sj asc", null)
        var id = 0
        var sl: Int
        var sj: Int
        var je: Int
        while (cursor.moveToNext()) {
            val map: HashMap<String, String> = HashMap()
            sl = cursor.getInt(1)
            sj = cursor.getInt(0)
            map["id"] = (++id).toString()
            map["tm"] = sj.toString() + ".00"
            map["sl"] = sl.toString()
            map["zq"] = "1.00"
            je = sl * sj
            map["je"] = decimalFormatter.format(je)
            mData.add(map)
        }
        cursor.close()
    }

    override fun compute() {
        val sum_sl = mData.sumBy { it["sl"]!!.toInt() }
        val sum_je = mData.sumBy { decimalFormatter.parse(it["je"]).toInt() }
        val map = HashMap<String, String>()
        map["id"] = "合计"
        map["tm"] = ""
        map["sl"] = sum_sl.toString()
        map["zq"] = ""
        map["je"] = decimalFormatter.format(sum_je)
        mData.add(map)
    }

    override fun setSort(v: View) {
        //val id = v.findViewById(R.id.goods_header_id)
        val tm = v.findViewById(R.id.goods_header_tm) as TextView
        val sl = v.findViewById(R.id.goods_header_sl) as TextView
        val zq = v.findViewById(R.id.goods_header_zq) as TextView
        val je = v.findViewById(R.id.goods_header_je) as TextView

        setClick(tm, "tm")
        setClick(sl, "sl")
        setClick(zq, "zq")
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
        var map = mData[position]
        holder.id.text = map["id"]
        holder.tm.text = map["tm"]
        holder.sl.text = map["sl"]
        holder.zq.text = map["zq"]
        holder.je.text = map["je"]
        return v
    }

    private class ViewHolder(var v: View) {
        var id = v.findViewById(R.id.goods_id) as TextView
        var tm = v.findViewById(R.id.goods_tm) as TextView
        var sl = v.findViewById(R.id.goods_sl) as TextView
        var zq = v.findViewById(R.id.goods_zq) as TextView
        var je = v.findViewById(R.id.goods_je) as TextView
    }
}
