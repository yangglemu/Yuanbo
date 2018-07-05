package com.yuan.soft

import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

/**
 * Created by 123456 on 2016/6/24.
 */
class SaleFLAdapter(context: MainActivity, sqlite: SQLiteDatabase, start: Date, end: Date) :
        DataAdapter(context, sqlite, start, end) {

    override fun initData() {
        val s = dateFormatter.format(start)
        val e = dateFormatter.format(end)
        val sb = StringBuilder()
        sb.append("select rq, tm,sum(sl) as sl,sum(je) as je,shop from sale_mx ")
        sb.append("where date(rq)>='$s' and date(rq)<='$e' ")
        sb.append("group by shop,tm order by shop,tm asc")
        val c = db.rawQuery(sb.toString(), null)
        var id = 1
        while (c.moveToNext()) {
            val map = HashMap<String, String>()
            map["id"] = (id++).toString()
            map["rq"] = c.getString(0).substring(2)
            map["tm"] = c.getString(1)
            map["sl"] = c.getString(2)
            map["je"] = c.getString(3)
            map["shop"] = DataAdapter.shops[c.getString(4)]!!
            mData.add(map)
        }
        c.close()
    }

    override fun setSort(v: View) {
        val tm = v.findViewById(R.id.sale_fl_header_tm) as TextView
        val sl = v.findViewById(R.id.sale_fl_header_sl) as TextView
        val je = v.findViewById(R.id.sale_fl_header_je) as TextView
        val rq = v.findViewById(R.id.sale_fl_header_rq) as TextView
        setClick(tm, "tm")
        setClick(sl, "sl")
        setClick(je, "je")
        setClick(rq, "rq")
    }

    override fun compute() {
        val sl = mData.sumBy { it["sl"]!!.toInt() }
        val je = mData.sumBy { it["je"]!!.toInt() }
        val map = HashMap<String, String>()
        map["rq"] = "合计"
        map["sl"] = sl.toString()
        map["je"] = je.toString()
        mData.add(map)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val v: View
        val vh: ViewHolder
        if (convertView == null) {
            v = mInflater.inflate(R.layout.sale_fl_item, null)
            vh = ViewHolder(v)
            v.tag = vh
        } else {
            v = convertView
            vh = v.tag as ViewHolder
        }
        val map = mData[position]
        vh.id.text = map["id"]
        vh.rq.text = map["rq"]
        vh.tm.text = map["tm"]
        vh.sl.text = map["sl"]
        vh.je.text = map["je"]
        vh.shop.text = map["shop"]
        return v
    }

    private class ViewHolder(v: View) {
        val id = v.findViewById(R.id.sale_fl_id) as TextView
        val rq = v.findViewById(R.id.sale_fl_rq) as TextView
        val tm = v.findViewById(R.id.sale_fl_tm) as TextView
        val sl = v.findViewById(R.id.sale_fl_sl) as TextView
        val je = v.findViewById(R.id.sale_fl_je) as TextView
        val shop = v.findViewById(R.id.sale_fl_shop) as TextView
    }
}
