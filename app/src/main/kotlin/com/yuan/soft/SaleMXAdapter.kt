package com.yuan.soft


import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

/**
 * Created by yuan on 2016/6/20.
 */
class SaleMXAdapter(context: MainActivity, sqlite: SQLiteDatabase, start: Date, end: Date) : DataAdapter(context, sqlite, start, end) {
    override fun initData() {
        val s = start?.toString(MainActivity.formatString)
        val e = end?.toString(MainActivity.formatString)
        val c = db.rawQuery("select rq,tm,sl,zq,je,shop from sale_mx where date(rq)>='$s' and date(rq)<='$e' order by shop,rq asc", null)
        var id = 1
        while (c.moveToNext()) {
            val m = HashMap<String, String>()
            m["id"] = (id++).toString()
            m["rq"] = c.getString(0).substring(2,16)
            m["tm"] = c.getString(1)
            m["sl"] = c.getString(2)
            m["zq"] = c.getString(3)
            m["je"] = c.getString(4)
            m["shop"] = DataAdapter.shops[c.getString(5)]!!
            mData.add(m)
        }
        c.close()
    }

    override fun compute() {
        val sl = mData.sumBy { it["sl"]!!.toInt() }
        val je = mData.sumBy { it["je"]!!.toInt() }
        val map = HashMap<String, String>()
        map["id"] = "合计"
        map["tm"] = ""
        map["sl"] = sl.toString()
        map["zq"] = ""
        map["je"] = je.toString()
        mData.add(map)
    }

    override fun setSort(v: View) {
        val tm = v.findViewById(R.id.sale_mx_header_tm) as TextView
        val sl = v.findViewById(R.id.sale_mx_header_sl) as TextView
        val zq = v.findViewById(R.id.sale_mx_header_zq) as TextView
        val je = v.findViewById(R.id.sale_mx_header_je) as TextView
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
        vh.rq.text = m["rq"]
        vh.tm.text = m["tm"]
        vh.sl.text = m["sl"]
        vh.zq.text = m["zq"]
        vh.je.text = m["je"]
        vh.shop.text = m["shop"]
        return v
    }

    private class ViewHolder(v: View) {
        val id = v.findViewById(R.id.sale_mx_id) as TextView
        val rq = v.findViewById(R.id.sale_mx_rq) as TextView
        val tm = v.findViewById(R.id.sale_mx_tm) as TextView
        val sl = v.findViewById(R.id.sale_mx_sl) as TextView
        val zq = v.findViewById(R.id.sale_mx_zq) as TextView
        val je = v.findViewById(R.id.sale_mx_je) as TextView
        val shop = v.findViewById(R.id.sale_mx_shop) as TextView
    }
}
