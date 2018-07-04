package com.yuan.soft

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by 123456 on 2016/6/23.
 */

class MyDatePicker(context: Context?, theme: Int, postMsg: MainActivity.IPostMessage) : Dialog(context, theme), DatePicker.OnDateChangedListener {
    companion object {
        const val formatString = "yyyy-MM-dd"
    }

    private val postMessage = postMsg

    private val datePickerStart: DatePicker by lazy {
        findViewById(R.id.datePickerStart) as DatePicker
    }
    private val datePickerEnd: DatePicker by lazy {
        findViewById(R.id.datePickerEnd) as DatePicker
    }
    private val buttonOK: Button by lazy {
        findViewById(R.id.buttonOK) as Button
    }
    private val buttonCancel: Button by lazy {
        findViewById(R.id.buttonCancel) as Button
    }
    lateinit var dateStart: Date
    lateinit var dateEnd: Date

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.date_picker)
        val calendar = Calendar.getInstance(Locale.CHINA)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        dateStart = SimpleDateFormat(formatString, Locale.CHINA).parse("$year-${month + 1}-$day")
        dateEnd = dateStart
        datePickerStart.init(year, month, day, this)
        datePickerEnd.init(year, month, day, this)
        buttonOK.setOnClickListener {
            this@MyDatePicker.dismiss()
            this@MyDatePicker.postMessage.postMessage(dateStart, dateEnd)
        }
        buttonCancel.setOnClickListener { dismiss() }
    }

    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date = SimpleDateFormat(formatString, Locale.CHINA).parse("$year-${monthOfYear + 1}-$dayOfMonth")
        if (view == datePickerStart) dateStart = date else if (view == datePickerEnd) dateEnd = date
    }
}
