package com.yuan.soft

import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.widget.BaseAdapter
import java.lang.reflect.Type
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by yuan on 2016/6/16.
 */
abstract class DataAdapter(context: MainActivity, sqlite: SQLiteDatabase, var start: Date? = null, var end: Date? = null) : BaseAdapter() {
    private val mContext = context
    val mInflater = LayoutInflater.from(mContext)!!
    val mData = ArrayList<HashMap<String, String>>()
    val db = sqlite
    val decimalFormatter = DecimalFormat("#,##0.00")
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    private val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    companion object {
        val shops: HashMap<String, String> = HashMap()
    }

    init {
        if (shops.size == 0) {
            val sql = "select pname,name from shop"
            val c = db.rawQuery(sql, null)
            while (c.moveToNext()) {
                shops[c.getString(0)] = c.getString(1)
            }
            c.close()
        }
        initData()
        compute()
    }

    protected abstract fun initData()

    protected abstract fun compute()

    abstract fun setSort(v: View)

    fun setClick(tv: View, name: String) {
        tv.setOnClickListener {
            val lastMap = mData.last()
            mData.remove(lastMap)
            when (name) {
                "rq" -> {
                    if (tv.tag == "asc") {
                        mData.sortBy {
                            val df: SimpleDateFormat
                            if (it[name]?.length == 10) df = dateFormatter
                            else df = dateTimeFormatter
                            df.parse(it[name])
                        }
                        tv.tag = "desc"
                    } else {
                        mData.sortByDescending {
                            val df: SimpleDateFormat = if (it[name]?.length == 10) dateFormatter else dateTimeFormatter
                            df.parse(it[name])
                        }
                        tv.tag = "asc"
                    }
                }
                "je", "tm", "sl" -> {
                    if (tv.tag == "asc") {
                        mData.sortBy { it[name]?.toInt() }
                        tv.tag = "desc"
                    } else {
                        mData.sortByDescending { it[name]?.toInt() }
                        tv.tag = "asc"
                    }
                }
                "zq" -> {
                    if (tv.tag == "asc") {
                        mData.sortBy { it[name]?.toFloat() }
                        tv.tag = "desc"
                    } else {
                        mData.sortByDescending { it[name]?.toFloat() }
                        tv.tag = "asc"
                    }
                }
                else -> {
                }
            }
            for (index in 0 until mData.size) {
                mData[index]["id"] = (index + 1).toString()
            }
            mData.add(lastMap)
            notifyDataSetChanged()
        }
    }

    override fun getCount(): Int = mData.size

    override fun getItem(position: Int): Any? = mData[position]

    override fun getItemId(position: Int): Long = position.toLong()

}
