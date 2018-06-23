package com.yuan.yuanbo

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
        db.execSQL("CREATE TABLE [goods] (tm TEXT PRIMARY KEY,sj INTEGER,zq REAL,sl INTEGER)")
        db.execSQL("CREATE TABLE [sale_db] (rq DATETIME PRIMARY KEY,sl INTEGER,je INTEGER)")
        db.execSQL("CREATE TABLE [sale_mx] (id INTEGER PRIMARY KEY,rq DATETIME,tm TEXT,sl INTEGER,zq REAL,je INTEGER)")
        db.execSQL("CREATE TABLE [history] (uid TEXT PRIMARY KEY,rq DATETIME)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) =
            throw UnsupportedOperationException()
}
