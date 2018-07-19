package com.yuan.soft

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView

class StatusDialog(context: Context, theme: Int) : Dialog(context, theme) {
    val progressBar: ProgressBar by lazy {
        findViewById<ProgressBar>(R.id.progressBar)
    }
    val label: TextView by lazy {
        findViewById<TextView>(R.id.label)
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
