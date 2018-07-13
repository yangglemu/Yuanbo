package com.yuan.soft

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.sun.mail.pop3.POP3Folder
import com.yuan.soft.R.id.db
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Handler
import javax.mail.Flags
import javax.mail.Folder
import javax.mail.Message
import javax.mail.Session
import javax.xml.parsers.DocumentBuilderFactory

fun Date.toString(formatString: String): String {
    return SimpleDateFormat(formatString, Locale.CHINA).format(this)
}

class Email(val context: Context, val db: SQLiteDatabase) {
    companion object {
        const val pop3Host = "pop.126.com"
        const val pop3Port = "110"
        const val username = "yangglemu"
        const val password = "yuanbo132"
        val shops = HashMap<String, String>()
    }

    init {
        val sql = "select pname,name from shop"
        val cursor = db.rawQuery(sql, null)
        while (cursor.moveToNext()) {
            shops[cursor.getString(0)] = cursor.getString(1)
        }
        cursor.close()
    }

    fun receive(handler: MainActivity.MyHandler) {
        val p = Properties()
        p["mail.pop3.host"] = pop3Host
        p["mail.pop3.port"] = pop3Port
        val session = Session.getInstance(p)
        val store = session.getStore("pop3")
        store.connect(username, password)
        val folder = store.getFolder("INBOX") as POP3Folder
        folder.open(Folder.READ_WRITE)
        val total = folder.messages.size
        val msg = android.os.Message()
        msg.what = -11
        msg.arg1 = total
        handler.sendMessage(msg)
        var read = 0
        var newCount = 0
        //var newShop = 0
        for (msg in folder.messages) {
            read++
            val message = android.os.Message()
            message.arg1 = read
            message.what = 1
            handler.sendMessage(message)
            val buffer = msg.subject.split('@')
            if (buffer.size != 2) {
                msg.setFlag(Flags.Flag.DELETED, true)
                continue
            }
            if (!isInShops(buffer[0])) {
                //newShop++
                continue
            }
            val uid = folder.getUID(msg)
            if (isNewMessage(uid)) {
                newCount++
                insertIntoDatabase(msg.content.toString(), buffer[1])
                db.execSQL("insert into mail(uid,rq) values('$uid','${buffer[1]}')")
            }
        }
        folder.close(false)
        store.close()
        val m = android.os.Message()
        m.what = 2
        m.arg1 = newCount
        handler.sendMessage(m)
    }

    private fun insertIntoDatabase(content: String, date: String) {
        val doc = string2Document(content)
        val root = doc.documentElement
        val shop = root.tagName
        val good = root.getElementsByTagName("good")
        val sale_db = root.getElementsByTagName("sale_db")
        val sale_mx = root.getElementsByTagName("sale_mx")

        var sql = "delete from goods where shop='$shop'"
        db.execSQL(sql)
        if (good.length > 0) {
            for (index in 0 until good.length) {
                val attr = good.item(index).attributes
                val tm = attr.getNamedItem("tm").nodeValue
                val sl = attr.getNamedItem("sl").nodeValue
                db.execSQL("insert into goods (tm,sl,shop) values($tm,$sl,'$shop')")
            }
        }

        sql = "delete from sale_db where shop='$shop' and date(rq)='$date'"
        db.execSQL(sql)
        if (sale_db.length > 0) {
            for (index in 0 until sale_db.length) {
                val attr = sale_db.item(index).attributes
                val djh = attr.getNamedItem("djh").nodeValue
                val sl = attr.getNamedItem("sl").nodeValue
                val je = attr.getNamedItem("je").nodeValue
                val ss = attr.getNamedItem("ss").nodeValue
                val zl = attr.getNamedItem("zl").nodeValue
                val syy = attr.getNamedItem("syy").nodeValue
                // 20180101121314
                val rq = "${djh.subSequence(0, 4)}-${djh.subSequence(4, 6)}-${djh.subSequence(6, 8)} ${djh.subSequence(8, 10)}:${djh.subSequence(10, 12)}:${djh.subSequence(12, 14)}"
                db.execSQL("insert into sale_db (djh,sl,je,ss,zl,syy,rq,shop) values('$djh',$sl,$je,$ss,$zl,'$syy','$rq','$shop')")
            }
        }

        sql = "delete from sale_mx where shop='$shop' and date(rq)='$date'"
        db.execSQL(sql)
        if (sale_mx.length > 0) {
            for (index in 0 until sale_mx.length) {
                val attr = sale_mx.item(index).attributes
                val djh = attr.getNamedItem("djh").nodeValue
                val tm = attr.getNamedItem("tm").nodeValue
                val sl = attr.getNamedItem("sl").nodeValue
                val zq = attr.getNamedItem("zq").nodeValue
                val je = attr.getNamedItem("je").nodeValue
                val rq = "${djh.subSequence(0, 4)}-${djh.subSequence(4, 6)}-${djh.subSequence(6, 8)} ${djh.subSequence(8, 10)}:${djh.subSequence(10, 12)}:${djh.subSequence(12, 14)}"
                db.execSQL("insert into sale_mx (djh,tm,sl,zq,je,shop,rq) values('$djh',$tm,$sl,$zq,$je,'$shop','$rq')")
            }
        }
    }

    private fun string2Document(content: String): Document {
        val reader = StringReader(content)
        val stream = InputSource(reader)
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream)
        return doc
    }

    private fun isInShops(shop: String): Boolean {
        var value = false
        val cursor = db.rawQuery("select count(*) as count from shop where pname='$shop'", null)
        if (cursor.moveToNext()) {
            val count = cursor.getInt(0)
            if (count == 1) value = true
        }
        cursor.close()
        return value
    }

    private fun isNewMessage(uid: String): Boolean {
        var value = true
        val cursor = db.rawQuery("select count(*) as count from mail where uid='$uid'", null)
        if (cursor.moveToNext()) {
            val count = cursor.getInt(0)
            if (count == 1) value = false
        }
        cursor.close()
        return value
    }
}
