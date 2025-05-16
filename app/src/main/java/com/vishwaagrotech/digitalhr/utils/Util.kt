package com.vishwaagrotech.digitalhr.utils

import android.app.Activity
import android.graphics.Rect
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.DialogPopupBinding

fun Activity.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
    else Rect().apply { window.decorView.getWindowVisibleDisplayFrame(this) }.top
}

fun Activity.makeToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.showConfirmDialog(value: String) {
    val builder = AlertDialog.Builder(this)

    val dialogBinding : DialogPopupBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_popup,null, false)
    builder.setView(dialogBinding.root)

    dialogBinding.textValue.text = value

    val alertDialog = builder.create()
    alertDialog.setCancelable(false)

    dialogBinding.buttonConfirm.setOnClickListener {
        alertDialog.dismiss()
    }

    dialogBinding.buttonClose.setOnClickListener {
        alertDialog.dismiss()
    }

    alertDialog.show()
}
