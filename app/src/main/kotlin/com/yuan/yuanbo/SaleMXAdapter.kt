package com.yuan.yuanbo

import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.w3c.dom.Text
import java.util.*

/**
 * Created by yuan on 2016/6/20.
 */
class SaleMXAdapter(context: MainActivity, sqlite: SQLiteDatabase, start: Date, end: Date) : DataAdapter(context, sqlite, start, end) {
    override fun initData() {
        val s = start?.toString(MainActivity.formatString)
        val e = end?.toString(MainActivity.formatString)
        val c = db.rawQuery("select tm,sl,zq,je from sale_mx where date(rq)>='$s' and date(rq)<='$e' order by rq asc", null)
        var id: Int = 0
        while (c.moveToNext()) {
            val sl = c.getInt(1)
            val je = c.getInt(3)
            val zq = c.getFloat(2)
            val m = HashMap<String, String>()
            m["id"] = (++id).toString()
            m["tm"] = c.getString(0) + ".00"
            m["sl"] = sl.toString()
            m["zq"] = zq.toString()
            m["je"] = decimalFormatter.format(je)
            mData.add(m)
        }
        c.close()
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
        val tm = v.findViewById<TextView>(R.id.sale_mx_header_tm)
        val sl = v.findViewById<TextView>(R.id.sale_mx_header_sl)
        val zq = v.findViewById<TextView>(R.id.sale_mx_header_zq)
        val je = v.findViewById<TextView>(R.id.sale_mx_header_je)
        setClick(tm, "tm")
        setClick(sl, "sl")
        setClick(zq, "zq")
        setClick(je, "je")
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val v: View
        val vh: ViewHolder
        if (convertView == null) {
            v = mInflater.inflate(R.layout.sale_mx_item, null)
            vh = ViewHolder(v)
            v.tag = vh
        } else {
            v = convertView
            vh = v.tag as ViewHolder
        }
        val m = mData[position]
        vh.id.text = m["id"]
        vh.tm.text = m["tm"]
        vh.sl.text = m["sl"]
        vh.zq.text = m["zq"]
        vh.je.text = m["je"]
        return v
    }

    private class ViewHolder(v: View) {
        val id = v.findViewById(R.id.sale_mx_id) as TextView
        var tm = v.findViewById(R.id.sale_mx_tm) as TextView
        var sl = v.findViewById(R.id.sale_mx_sl) as TextView
        var zq = v.findViewById(R.id.sale_mx_zq) as TextView
        var je = v.findViewById(R.id.sale_mx_je) as TextView
    }
}