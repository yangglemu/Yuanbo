package com.yuan.soft

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import kotlin.math.max

class StatusDialog(context: Context, theme: Int) : Dialog(context, theme) {
    val progressBar by lazy {
        findViewById(R.id.progressBar) as ProgressBar
    }
    val label by lazy {
        findViewById(R.id.label) as TextView
    }

    init {

    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.status_dialog)
        setCancelable(false)
        progressBar.isIndeterminate = false
        progressBar.max = 100
        progressBar.progress = 0
    }
}
