package com.yuan.soft

import android.app.Activity
import android.app.ProgressDialog
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import java.lang.ref.WeakReference
import java.util.*

class MainActivity : Activity() {
    val db: SQLiteDatabase by lazy {
        DbHelper(this).writableDatabase
    }
    val email: Email by lazy {
        Email(this, db)
    }
    private val mainLayout: LinearLayout by lazy {
        findViewById(R.id.mainLayout) as LinearLayout
    }

    lateinit var listLayout: View
    lateinit var listView: ListView
    lateinit var pd: ProgressDialog
    var timer: Timer? = null

    companion object {
        var formatString = "yyyy-MM-dd"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        pd = ProgressDialog(this)

        createListLayout(R.layout.goods, R.id.listView_goods, GoodsAdapter(this, db))
    }

    fun createListLayout(layoutId: Int, listViewId: Int, adapter: DataAdapter) {
        mainLayout.removeAllViews()
        listLayout = layoutInflater.inflate(layoutId, null)
        listView = listLayout.findViewById(listViewId) as ListView
        listView.adapter = adapter
        adapter.setSort(listLayout)
        mainLayout.addView(listLayout)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sp -> {
                createListLayout(R.layout.goods, R.id.listView_goods, GoodsAdapter(this, db))
                toast("商品资料")
            }
            R.id.mx -> {
                val date = Date()
                createListLayout(R.layout.sale_mx, R.id.listView_sale_mx, SaleMXAdapter(this, db, date, date))
                toast("本日销售明细")
            }
            R.id.db -> {
                val date = Date()
                val adapter = SaleDBAdapter(this, db, date, date)
                createListLayout(R.layout.sale_db, R.id.listView_sale_db, adapter)
                toast("本日销售单笔")
            }
            R.id.fl -> {
                val date = Date()
                val adapter = SaleFLAdapter(this, db, date, date)
                createListLayout(R.layout.sale_fl, R.id.listView_sale_fl, adapter)
                toast("本日分类汇总")
            }
            R.id.day -> {
                val adapter = SaleDayAdapter(this, db)
                createListLayout(R.layout.sale_day, R.id.listView_sale_day, adapter)
                toast("本月按天汇总")
            }
            R.id.rq_mx -> {
                MyDatePicker(this, R.style.datePickerDialog, object : IPostMessage {
                    override fun postMessage(start: Date, end: Date) {
                        val sale_mx = SaleMXAdapter(this@MainActivity, db, start, end)
                        createListLayout(R.layout.sale_mx, R.id.listView_sale_mx, sale_mx)
                        toast("选择日期:销售明细")
                    }
                }).show()
            }
            R.id.rq_db -> {
                MyDatePicker(this, R.style.datePickerDialog, object : IPostMessage {
                    override fun postMessage(start: Date, end: Date) {
                        val adapter = SaleDBAdapter(this@MainActivity, db, start, end)
                        createListLayout(R.layout.sale_db, R.id.listView_sale_db, adapter)
                        toast("选择日期:销售单笔")
                    }
                }).show()
            }
            R.id.rq_fl -> {
                MyDatePicker(this, R.style.datePickerDialog, object : IPostMessage {
                    override fun postMessage(start: Date, end: Date) {
                        val sale_fl = SaleFLAdapter(this@MainActivity, db, start, end)
                        createListLayout(R.layout.sale_fl, R.id.listView_sale_fl, sale_fl)
                        toast("选择日期:分类汇总")
                    }
                }).show()
            }
            R.id.rq_day -> {
                MyDatePicker(this, R.style.datePickerDialog, object : IPostMessage {
                    override fun postMessage(start: Date, end: Date) {
                        val sale_day = SaleDayAdapter(this@MainActivity, db, start, end)
                        createListLayout(R.layout.sale_day, R.id.listView_sale_day, sale_day)
                        toast("选择日期:按天汇总")
                    }
                }).show()
            }
            R.id.refresh -> {
                Thread(Runnable {
                    email.receive()
                    myHandler.sendEmptyMessage(0)
                }).start()
            }
            R.id.exit -> finish()
            else -> return false
        }
        return true
    }

    fun toast(msg: String) {
        makeText(this, msg, LENGTH_LONG).show()
    }

    private val myHandler = MyHandler(this)

    private class MyHandler(activity: MainActivity) : Handler() {
        private val mActivity: WeakReference<MainActivity> = WeakReference(activity)
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            val activity = mActivity.get()
            if (activity != null) {
                if (msg?.what == 0)
                    activity.toast("thread message: mail is read.")
            }
        }
    }


    override fun onDestroy() {
        val c = Calendar.getInstance(Locale.CHINA)
        c.time = Date()
        c.add(Calendar.DAY_OF_MONTH, -29)
        val date = c.time.toString(formatString)
        val sql = "delete from mail where date(rq)<'$date'"
        try {
            db.execSQL(sql)
        } catch (e: SQLiteException) {
            toast("退出程序时清理出错!" + e.message)
        } finally {
            db.close()
        }
        super.onDestroy()
    }

    interface IPostMessage {
        fun postMessage(start: Date, end: Date): Unit
    }
}
