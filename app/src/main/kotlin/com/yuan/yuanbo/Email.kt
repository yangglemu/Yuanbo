package com.yuan.yuanbo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.sun.mail.pop3.POP3Folder
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.*
import javax.mail.Flags
import javax.mail.Folder
import javax.mail.Session
import javax.xml.parsers.DocumentBuilderFactory

fun Date.toString(formatString: String): String {
    return SimpleDateFormat(formatString, Locale.CHINA).format(this)
}

class Email(val context: Context, val db: SQLiteDatabase) {
    companion object {
        const val pop3Host = "pop.126.com"
        const val pop3Port = 995
        const val username = "yangglemu"
        const val password = "yuanbo132"
    }

    /*
    fun send(content: String) {
        val p = Properties()
        //p.put("mail.smtp.ssl.enable", false)
        p.put("mail.smtp.host", smtpHost)
        p.put("mail.smtp.port", smtpPort)
        p.put("mail.smtp.auth", true)
        val session = Session.getInstance(p, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication() = PasswordAuthentication(username, password)
        })
        val msg = MimeMessage(session)
        msg.setFrom(InternetAddress(mailBox))
        msg.setRecipients(MimeMessage.RecipientType.TO, mailBox)
        msg.subject = subject
        msg.setText(content)
        Transport.send(msg)
    }
*/

    fun receive() {
        val p = Properties()
        p.put("mail.pop3.ssl.enable", true)
        p.put("mail.pop3.host", pop3Host)
        p.put("mail.pop3.port", pop3Port)
        val session = Session.getInstance(p)
        val store = session.getStore("pop3")
        store.connect(username, password)
        val folder = store.getFolder("INBOX") as POP3Folder
        folder.open(Folder.READ_WRITE)
        var total = folder.messages.size
        var del = 0
        var read = 0
        for (msg in folder.messages) {
            val buffer = msg.subject.split('@')
            if (buffer.size != 2) {
                msg.setFlag(Flags.Flag.DELETED, true)
                del++
                continue
            }
            if (!isInShops(buffer[0])) {
                msg.setFlag(Flags.Flag.DELETED, true)
                del++
                continue
            }
            val uid = folder.getUID(msg)
            if (isNewMessage(uid)) {
                insertIntoDatabase(msg.content.toString())
                db.execSQL("insert into mail(uid,rq) values('$uid','${buffer[1]}')")
                read++
            }
        }
        folder.close(false)
        store.close()
    }

    private fun insertIntoDatabase(content: String) {
        val doc = string2Document(content)
        val root = doc.documentElement
        val good = root.getElementsByTagName("goods").item(0).childNodes
        val sale_db = root.getElementsByTagName("sale_dbs").item(0).childNodes
        val sale_mx = root.getElementsByTagName("sale_mxs").item(0).childNodes
        if (good.length > 0) {
            for (index in 0 until good.length - 1) {
                val attr = good.item(index).attributes
                val tm = attr.getNamedItem("tm").nodeValue
                val sl = attr.getNamedItem("sl").nodeValue
                val shop = attr.getNamedItem("shop").nodeValue
                db.execSQL("replace into goods (tm,sl,shop) values($tm,$sl,$shop)")
            }
        }

        if (sale_db.length > 0) {
            for (index in 0 until sale_db.length - 1) {
                val attr = sale_db.item(index).attributes
                val rq = attr.getNamedItem("rq").nodeValue
                val sl = attr.getNamedItem("sl").nodeValue
                val je = attr.getNamedItem("je").nodeValue
                db.execSQL("replace into sale_db (rq,sl,je) values('$rq',$sl,$je)")
            }
        }

        if (sale_mx.length > 0) {
            for (index in 0 until sale_mx.length - 1) {
                val attr = sale_mx.item(index).attributes
                val id = attr.getNamedItem("id").nodeValue
                val rq = attr.getNamedItem("rq").nodeValue
                val tm = attr.getNamedItem("tm").nodeValue
                val sl = attr.getNamedItem("sl").nodeValue
                val zq = attr.getNamedItem("zq").nodeValue
                val je = attr.getNamedItem("je").nodeValue
                db.execSQL("replace into sale_mx (id,rq,tm,sl,zq,je) values('$id','$rq','$tm',$sl,$zq,$je)")
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
        val cursor = db.rawQuery("select count(*) as count from shop where shop='$shop'", null)
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

/*
    fun getXmlContent(date: Date): String {
        val formatString = "yyyy-MM-dd"
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val root = doc.createElement("duobao")
        doc.appendChild(root)

        val connection = DriverManager.getConnection(url)
        val statement = connection.createStatement()
        val res_goods = statement.executeQuery("select sj as tm,sum(kc) as sl from goods group by sj")
        val goods = doc.createElement("goods")
        root.appendChild(goods)
        while (res_goods.next()) {
            val tm = res_goods.getInt("tm").toString()
            val sl = res_goods.getInt("sl").toString()
            val element = doc.createElement("goods")
            element.setAttribute("tm", tm)
            element.setAttribute("sl", sl)
            goods.appendChild(element)
        }

        val res_sale_mx = statement.executeQuery("select sale_mx.id as id,sale_db.rq as rq,sale_mx.sj as tm,sale_mx.sl as sl,"
                + "sale_mx.zq as zq,sale_mx.je as je from sale_mx join sale_db "
                + "on(sale_mx.djh=sale_db.djh) where date(sale_db.rq)='${date.toString(formatString)}'")
        val sale_mx = doc.createElement("sale_mx")
        root.appendChild(sale_mx)
        while (res_sale_mx.next()) {
            val id = res_sale_mx.getString("id")
            val rq = res_sale_mx.getString("rq")
            val tm = res_sale_mx.getString("tm")
            val sl = res_sale_mx.getString("sl")
            val zq = res_sale_mx.getString("zq")
            val je = res_sale_mx.getString("je")
            val element = doc.createElement("sale_mx")
            element.setAttribute("id", id)
            element.setAttribute("rq", rq)
            element.setAttribute("tm", tm)
            element.setAttribute("sl", sl)
            element.setAttribute("zq", zq)
            element.setAttribute("je", je)
            sale_mx.appendChild(element)
        }

        val res_sale_db = statement.executeQuery("select rq,sl,je from sale_db where date(rq)='${date.toString(formatString)}'")
        val sale_db = doc.createElement("sale_db")
        root.appendChild(sale_db)
        while (res_sale_db.next()) {
            val rq = res_sale_db.getString("rq")
            val sl = res_sale_db.getString("sl")
            val je = res_sale_db.getString("je")
            val element = doc.createElement("sale_db")
            element.setAttribute("rq", rq)
            element.setAttribute("sl", sl)
            element.setAttribute("je", je)
            sale_db.appendChild(element)
        }
        res_goods.close()
        statement.close()
        connection.close()

        val stream = ByteArrayOutputStream()
        val tans = TransformerFactory.newInstance().newTransformer()
        tans.transform(DOMSource(doc), StreamResult(stream))
        val content = stream.toString()
        stream.close()
        return content
    }
*/

}