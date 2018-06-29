package com.yuan.soft

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by yuan on 2016/6/18.
 */
class DbHelper(context: Context) : SQLiteOpenHelper(context, name, null, version) {
    companion object {
        var name = "yuanBo.db"
        var version = 1
    }


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE [goods] (tm INTEGER,sl INTEGER,shop VARCHAR(20))")
        db.execSQL("CREATE TABLE [mail] (uid VARCHAR(100),rq DATE)")
        db.execSQL("CREATE TABLE [note] (rq DATETIME,text TEXT)")
        db.execSQL("CREATE TABLE [sale_db] (djh VARCHAR(20),sl INTEGER,je INTEGER,ss INTEGER,zl INTEGER,rq DATETIME,syy VARCHAR(10),shop VARCHAR(20))")
        db.execSQL("CREATE TABLE [sale_mx] (djh VARCHAR(20),tm INTEGER,sl INTEGER,zq REAL,je INTEGER,shop VARCHAR(20),rq DATETIME)")
        db.execSQL("CREATE TABLE [shop] (pname VARCHAR(20) PRIMARY KEY,name VARCHAR(40),address VARCHAR(100),worker VARCHAR(20),tel VARCHAR(100),rq DATETIME)")
        db.execSQL("INSERT INTO [shop] (pname,name,rq) values('wkl','万客隆服饰','2018-06-06 06:06:06')")
        db.execSQL("INSERT INTO [shop] (pname,name,rq) values('duobao','阳光服饰','2018-06-06 06:06:06')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) =
            throw UnsupportedOperationException()
}
