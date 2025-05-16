package com.vishwaagrotech.digitalhr.utils

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import java.lang.Exception

class TypefaceUtil {
    companion object {

        fun overrideFont(context: Context, defaultFontToOverride:String, customFontFileNameInAssets:String){
            try {
                val customTypeface = Typeface.createFromAsset(context.assets,customFontFileNameInAssets)
                val defaultTypefaceField = Typeface::class.java.getDeclaredField(defaultFontToOverride)
                defaultTypefaceField.isAccessible = true
                defaultTypefaceField.set(null,customTypeface)
            }catch (e:Exception){
                Log.d(
                    "font matters : ",
                    "Can not set custom font $customFontFileNameInAssets instead of $defaultFontToOverride"
                )
            }
        }
    }

}