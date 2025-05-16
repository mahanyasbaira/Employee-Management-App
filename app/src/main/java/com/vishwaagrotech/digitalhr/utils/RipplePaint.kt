package com.vishwaagrotech.digitalhr.utils

import android.graphics.Paint
import com.skyfishjy.library.RippleBackground

/**
 *Copyright (c) 2022, All Rights Reserved.
 */
val RippleBackground.paint: Paint? get() =
    try {
        val field = RippleBackground::class.java.getDeclaredField("rippleColor")
        field.isAccessible = true
        field.get(this) as? Paint
    } catch (e: Exception) {
        null
    }