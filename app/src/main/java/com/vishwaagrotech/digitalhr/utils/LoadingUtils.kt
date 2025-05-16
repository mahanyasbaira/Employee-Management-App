package com.vishwaagrotech.digitalhr.utils

import android.content.Context

class LoadingUtils {

    companion object {
        private var dialogLoader: DialogLoader? = null
        fun showDialog(
            context: Context?,
            isCancelable: Boolean
        ) {
            hideDialog()
            if (context != null) {
                try {
                    dialogLoader = DialogLoader(context)
                    dialogLoader?.let { jarvisLoader->
                        jarvisLoader.setCanceledOnTouchOutside(true)
                        jarvisLoader.setCancelable(isCancelable)
                        jarvisLoader.show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun hideDialog() {
            if (dialogLoader!=null && dialogLoader?.isShowing!!) {
                dialogLoader = try {
                    dialogLoader?.dismiss()
                    null
                } catch (e: Exception) {
                    null
                }
            }
        }

    }
}