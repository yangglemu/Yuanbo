package com.yuan.soft

import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.widget.BaseAdapter
import android.widget.Toast
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

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

    init {
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
                "je" -> {
                    if (tv.tag == "asc") {
                        mData.sortBy { decimalFormatter.parse(it[name]).toInt() }
                        tv.tag = "desc"
                    } else {
                        mData.sortByDescending { decimalFormatter.parse(it[name]).toInt() }
                        tv.tag = "asc"
                    }
                }
                else -> {
                    if (tv.tag == "asc") {
                        mData.sortBy { it[name]?.toFloat() }
                        tv.tag = "desc"
                    } else {
                        mData.sortByDescending { it[name]?.toFloat() }
                        tv.tag = "asc"
                    }
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

    fun toast(msg: String) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()
    }
}
