package com.yuan.soft

import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

/**
 * Created by 123456 on 2016/6/24.
 */
class SaleDayAdapter(context: MainActivity, sqlite: SQLiteDatabase, start: Date? = null, end: Date? = null) :
        DataAdapter(context, sqlite, start, end) {

    override fun initData() {
        val s: String
        val e: String
        if (start == null || end == null) {
            val now = Date()
            val calendar = Calendar.getInstance(Locale.CHINA)
            calendar.time = now
            calendar.add(Calendar.DAY_OF_MONTH, -29)
            val before = calendar.time
            e = dateFormatter.format(now)
            s = dateFormatter.format(before)

        } else {
            s = dateFormatter.format(start)
            e = dateFormatter.format(end)
        }
        var id = 1
        val sql = "select date(rq) as rq,sum(sl) as sl,sum(je) as je,shop from sale_db where date(rq)>='$s' and date(rq)<='$e' group by shop,date(rq) order by shop,rq desc"
        val c = db.rawQuery(sql, null)
        while (c.moveToNext()) {
            val map = HashMap<String, String>()
            map["id"] = (id++).toString()
            map["rq"] = c.getString(0)
            map["sl"] = c.getString(1)
            map["je"] = decimalFormatter.format(c.getInt(2))
            map["shop"] = c.getString(3)
            mData.add(map)
        }
        c.close()
    }

    override fun compute() {
        val sum_sl = mData.sumBy { it["sl"]!!.toInt() }
        val sum_je = mData.sumBy { decimalFormatter.parse(it["je"]).toInt() }
        val m = HashMap<String, String>()
        m["id"] = "合计"
        m["rq"] = "汇总天数:${mData.size}"
        m["sl"] = sum_sl.toString()
        m["je"] = decimalFormatter.format(sum_je)
        mData.add(m)
    }

    override fun setSort(v: View) {
        val rq = v.findViewById(R.id.sale_day_header_rq) as TextView
        val sl = v.findViewById(R.id.sale_day_header_sl) as TextView
        val je = v.findViewById(R.id.sale_day_header_je) as TextView
        setClick(rq, "rq")
        setClick(sl, "sl")
        setClick(je, "je")
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val v: View
        val vh: ViewHolder
        if (convertView == null) {
            v = mInflater.inflate(R.layout.sale_day_item, null)
            vh = ViewHolder(v)
            v.tag = vh
        } else {
            v = convertView
            vh = v.tag as ViewHolder
        }
        val m = mData[position]
        vh.id.text = m["id"]
        vh.rq.text = m["rq"]
        vh.sl.text = m["sl"]
        vh.je.text = m["je"]
        vh.shop.text = m["shop"]
        return v
    }

    private class ViewHolder(v: View) {
        val id = v.findViewById(R.id.sale_day_id) as TextView
        val rq = v.findViewById(R.id.sale_day_rq) as TextView
        val sl = v.findViewById(R.id.sale_day_sl) as TextView
        val je = v.findViewById(R.id.sale_day_je) as TextView
        val shop = v.findViewById(R.id.sale_day_shop) as TextView
    }

}
