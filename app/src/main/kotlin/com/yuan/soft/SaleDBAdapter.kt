package com.yuan.soft

import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by 123456 on 2016/6/24.
 */

class SaleDBAdapter(context: MainActivity, db: SQLiteDatabase, start: Date, end: Date) :
        DataAdapter(context, db, start, end) {
    override fun initData() {
        if (end!!.before(start!!)) {
            throw IllegalArgumentException()
        }
        val s = start?.toString(MainActivity.formatString)
        val e = end?.toString(MainActivity.formatString)
        val c = db.rawQuery("select rq,sl,je,ss,zl,shop from sale_db where date(rq)>='$s' and date(rq)<='$e' order by rq asc", null)
        var id = 1
        while (c.moveToNext()) {
            val map = HashMap<String, String>()
            map["id"] = (id++).toString()
            map["rq"] = c.getString(0).substring(2)
            map["sl"] = c.getString(1)
            map["je"] = c.getString(2)
            map["ss"] = c.getString(3)
            map["zl"] = c.getString(4)
            map["shop"] = DataAdapter.shops[c.getString(5)]!!
            mData.add(map)
        }
        c.close()
    }

    override fun compute() {
        val sl = mData.sumBy { it["sl"]!!.toInt() }
        val je = mData.sumBy { it["je"]!!.toInt() }
        val map = HashMap<String, String>()
        map["id"] = "合计"
        map["rq"] = "来客数:${mData.size}"
        map["sl"] = sl.toString()
        map["je"] = je.toString()
        mData.add(map)
    }

    override fun setSort(v: View) {
        //val id = v.findViewById(R.id.sale_db_header_id)
        val rq = v.findViewById(R.id.sale_db_header_rq) as TextView
        val sl = v.findViewById(R.id.sale_db_header_sl) as TextView
        val je = v.findViewById(R.id.sale_db_header_je) as TextView
        setClick(sl, "sl")
        setClick(je, "je")
        setClick(rq, "rq")
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val vh: ViewHolder
        val v: View
        if (convertView == null) {
            v = mInflater.inflate(R.layout.sale_db_item, null)
            vh = ViewHolder(v)
            v.tag = vh
        } else {
            v = convertView
            vh = v.tag as ViewHolder
        }
        val map = mData[position]
        vh.id.text = map["id"]
        vh.rq.text = map["rq"]
        vh.sl.text = map["sl"]
        vh.je.text = map["je"]
        vh.ss.text = map["ss"]
        vh.zl.text = map["zl"]
        vh.shop.text = map["shop"]
        return v
    }

    private class ViewHolder(v: View) {
        val id = v.findViewById(R.id.sale_db_id) as TextView
        val rq = v.findViewById(R.id.sale_db_rq) as TextView
        val sl = v.findViewById(R.id.sale_db_sl) as TextView
        val je = v.findViewById(R.id.sale_db_je) as TextView
        val ss = v.findViewById(R.id.sale_db_ss) as TextView
        val zl = v.findViewById(R.id.sale_db_zl) as TextView
        val shop = v.findViewById(R.id.sale_db_shop) as TextView
    }

}
