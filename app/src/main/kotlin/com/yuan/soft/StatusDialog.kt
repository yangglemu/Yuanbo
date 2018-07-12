package com.yuan.soft

import android.app.Dialog
import android.content.Context
import android.widget.ProgressBar
import android.widget.TextView

class StatusDialog(context: Context, layoutId: Int) : Dialog(context, layoutId) {
    val progressBar by lazy {
        findViewById(R.id.progressBar) as ProgressBar
    }
    val label by lazy {
        findViewById(R.id.label) as TextView
    }

    init {

    }
}
