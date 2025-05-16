package com.vishwaagrotech.digitalhr.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.vishwaagrotech.digitalhr.R


/**
 *Copyright (c) 2022, All Rights Reserved.
 */
class ConfirmDialogFragment(val activity: Activity) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_popup, null)
            builder.setView(view)

            val alertDialog = builder.create()
            alertDialog

        }?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DialogClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }

    interface DialogClickListener{
        fun onConfirmClicked(dialogFragment: DialogFragment)
        fun onCancelClicked(dialogFragment: DialogFragment)
    }

    internal lateinit var listener: DialogClickListener

}