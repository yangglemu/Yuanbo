package com.yuan.soft

import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import com.yuan.soft.R.id.db
import java.lang.ref.WeakReference
import java.util.*

class MainActivity : Activity() {
    val db: SQLiteDatabase by lazy {
        DbHelper(this).writableDatabase
    }
    private val email: Email by lazy {
        Email(this, db)
    }
    private val mainLayout: LinearLayout by lazy {
        findViewById(R.id.mainLayout) as LinearLayout
    }

    lateinit var listLayout: View
    lateinit var listView: ListView
    lateinit var mAdapter: DataAdapter
    var statusDialog: StatusDialog? = null

    companion object {
        var formatString = "yyyy-MM-dd"
        var listShops = mutableListOf<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        createListLayout(R.layout.sale_day, R.id.listView_sale_day, SaleDayAdapter(this, db))
    }

    fun createListLayout(layoutId: Int, listViewId: Int, adapter: DataAdapter) {
        mainLayout.removeAllViews()
        listLayout = layoutInflater.inflate(layoutId, null)
        listView = listLayout.findViewById(listViewId) as ListView
        registerForContextMenu(listView)
        listView.adapter = adapter
        adapter.setSort(listLayout)
        mainLayout.addView(listLayout)
        mAdapter = adapter
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (menu != null) {
            menu.setHeaderTitle("门店过滤")
            var index = 0
            listShops.clear()
            for (pair in DataAdapter.shops) {
                menu.add(0, index, 0, pair.value)
                listShops.add(pair.value)
                index++
            }
            menu.add(0, index, 0, "全部显示")
            listShops.add("全部显示")
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        if (item == null) return true
        mAdapter.filter(listShops[item.itemId])
        toast(listShops[item.itemId])
        return super.onContextItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sp -> {
                createListLayout(R.layout.goods, R.id.listView_goods, GoodsAdapter(this, db))
                toast("商品库存")
            }
            R.id.mx -> {
                val date = Date()
                createListLayout(R.layout.sale_mx, R.id.listView_sale_mx, SaleMXAdapter(this, db, date, date))
                toast("本日销售商品明细")
            }
            R.id.db -> {
                val date = Date()
                val adapter = SaleDBAdapter(this, db, date, date)
                createListLayout(R.layout.sale_db, R.id.listView_sale_db, adapter)
                toast("本日销售单笔明细")
            }
            R.id.fl -> {
                val date = Date()
                val adapter = SaleFLAdapter(this, db, date, date)
                createListLayout(R.layout.sale_fl, R.id.listView_sale_fl, adapter)
                toast("本日销售商品分类汇总")
            }
            R.id.day -> {
                val adapter = SaleDayAdapter(this, db)
                createListLayout(R.layout.sale_day, R.id.listView_sale_day, adapter)
                toast("本日按天汇总")
            }
            R.id.rq_mx -> {
                MyDatePicker(this, R.style.datePickerDialog, object : IPostMessage {
                    override fun postMessage(start: Date, end: Date) {
                        val mx = SaleMXAdapter(this@MainActivity, db, start, end)
                        createListLayout(R.layout.sale_mx, R.id.listView_sale_mx, mx)
                        toast("选择日期:销售商品明细")
                    }
                }).show()
            }
            R.id.rq_db -> {
                MyDatePicker(this, R.style.datePickerDialog, object : IPostMessage {
                    override fun postMessage(start: Date, end: Date) {
                        val adapter = SaleDBAdapter(this@MainActivity, db, start, end)
                        createListLayout(R.layout.sale_db, R.id.listView_sale_db, adapter)
                        toast("选择日期:销售单笔明细")
                    }
                }).show()
            }
            R.id.rq_fl -> {
                MyDatePicker(this, R.style.datePickerDialog, object : IPostMessage {
                    override fun postMessage(start: Date, end: Date) {
                        val fl = SaleFLAdapter(this@MainActivity, db, start, end)
                        createListLayout(R.layout.sale_fl, R.id.listView_sale_fl, fl)
                        toast("选择日期:销售商品分类汇总")
                    }
                }).show()
            }
            R.id.rq_day -> {
                MyDatePicker(this, R.style.datePickerDialog, object : IPostMessage {
                    override fun postMessage(start: Date, end: Date) {
                        val day = SaleDayAdapter(this@MainActivity, db, start, end)
                        createListLayout(R.layout.sale_day, R.id.listView_sale_day, day)
                        toast("选择日期:按天汇总")
                    }
                }).show()
            }
            R.id.shops -> {
                createListLayout(R.layout.shops, R.id.listView_shops, ShopsAdapter(this, db))
                toast("查看所有门店")
            }
            R.id.refresh -> {
                try {
                    statusDialog = StatusDialog(this, R.style.statusDialog)
                    statusDialog!!.show()
                    val lp = statusDialog!!.window.attributes
                    lp.width = windowManager.defaultDisplay.width
                    //lp.alpha = 0.7F
                    statusDialog!!.window.attributes = lp
                    Thread(Runnable {
                        email.receive(myHandler)
                    }).start()
                } catch (e: Exception) {
                    toast(e.message!!)
                }
            }
            R.id.exit -> finish()
            else -> return false
        }
        return true
    }

    fun toast(msg: String) {
        makeText(this, msg, LENGTH_SHORT).show()
    }

    private val myHandler = MyHandler(this)

    class MyHandler(activity: MainActivity) : Handler() {
        private val mActivity: WeakReference<MainActivity> = WeakReference(activity)
        private var total: Int = 0
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            val activity = mActivity.get()!!
            when (msg?.what) {
                -11 -> {
                    total = msg.arg1
                    activity.statusDialog!!.progressBar.max = total
                    activity.statusDialog!!.label.text = "处理邮件：$total / 0}"
                    activity.statusDialog!!.progressBar.progress = 0
                }
                1 -> {
                    val bar = activity.statusDialog!!.progressBar
                    val label = activity.statusDialog!!.label
                    //msg.arg2:total, msg.arg1:done
                    label.text = "处理邮件：$total / ${msg.arg1}"
                    bar.progress = msg.arg1
                }
                2 -> {
                    activity.statusDialog?.dismiss()
                    activity.statusDialog = null
                    activity.toast("共处理邮件 ${total}，其中新邮件 ${msg.arg1}")
                }
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
        fun postMessage(start: Date, end: Date)
    }
}
