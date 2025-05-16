package com.vishwaagrotech.digitalhr.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.vishwaagrotech.digitalhr.R


@BindingAdapter("imageLoad")
fun loadImage(imageView: ImageView, image: Int) {
    image.let {
        Glide.with(imageView.context)
            .load(image)
            .into(imageView)
    }
}

@BindingAdapter("imageUrl")
fun loadImgUrl(imageView: ImageView, image: String?) {
    Glide.with(imageView.context)
        .load(image)
        .placeholder(R.drawable.placeholder)
        .into(imageView)

}

@BindingAdapter("intText")
fun loadToString(textView: TextView, value: Int) {
    textView.text = value.toString()
}

@BindingAdapter("longText")
fun loadToString(textView: TextView, value: Long) {
    textView.text = value.toString()
}

@BindingAdapter("underlineText")
fun underline(textView: TextView, value: String) {
    if(value == "1"){
        textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}

@BindingAdapter("htmlText")
fun loadToHtml(textView: TextView, value: String) {
    textView.text = Html.fromHtml(value, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

@BindingAdapter("text_color")
fun setTextColor(view: TextView, color: String) {
    view.setTextColor(Color.parseColor(color))
}

@BindingAdapter("background_tint")
fun setTextBackgroundTintColor(view: TextView, color: String) {
    view.background.setTint(Color.parseColor(color))
}

@BindingAdapter("progress_color")
fun setProgressbarColor(view: com.google.android.material.progressindicator.CircularProgressIndicator, progress:Int) {
    if(progress<35){
        view.setIndicatorColor(Color.parseColor("#FF0000"))
    }else if(progress<70){
        view.setIndicatorColor(Color.parseColor("#FFA500"))
    }else{
        view.setIndicatorColor(Color.parseColor("#00FF00"))
    }

}

@BindingAdapter("progress_linear_color")
fun setProgressbarLinearColor(view: com.moos.library.HorizontalProgressView, progress:Int) {
    if(progress<35){
        view.setEndColor(Color.parseColor("#FF0000"))
        view.setStartColor(Color.parseColor("#FF0000"))
    }else if(progress<70){
        view.setEndColor(Color.parseColor("#FFA500"))
        view.setStartColor(Color.parseColor("#FFA500"))
    }else{
        view.setEndColor(Color.parseColor("#00FF00"))
        view.setStartColor(Color.parseColor("#00FF00"))
    }

}

@BindingAdapter("card_background_tint_task")
fun setCardBackgroundTintColor(view: com.google.android.material.card.MaterialCardView, status: String) {
    if(status == "In Progress"){
        view.setCardBackgroundColor(Color.parseColor("#40FFBF00"))
    }else if(status == "Completed"){
        view.setCardBackgroundColor(Color.parseColor("#4000FF00"))
    }else if(status == "On Hold"){
        view.setCardBackgroundColor(Color.parseColor("#40CD7F32"))
    }else if(status == "Cancelled"){
        view.setCardBackgroundColor(Color.parseColor("#40EE4B2B"))
    }else if(status == "Not Started"){
        view.setCardBackgroundColor(Color.parseColor("#40880808"))
    }
}

@BindingAdapter("view_background_tint_task")
fun setCardBackgroundTintColor(view: View, status: String) {
    if(status == "In Progress"){
        view.setBackgroundColor(Color.parseColor("#FFBF00"))
    }else if(status == "Completed"){
        view.setBackgroundColor(Color.parseColor("#00FF00"))
    }else if(status == "On Hold"){
        view.setBackgroundColor(Color.parseColor("#CD7F32"))
    }else if(status == "Cancelled"){
        view.setBackgroundColor(Color.parseColor("#EE4B2B"))
    }else if(status == "Not Started"){
        view.setBackgroundColor(Color.parseColor("#880808"))
    }
}

@BindingAdapter("toggle_switch")
fun toggleSwitch(view: ImageView, status: String) {
    if(status == "0"){
        view.setImageDrawable(view.resources.getDrawable(R.drawable.ic_check_incompleted))
        view.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(view.context, R.color.white))
    }else{
        view.setImageDrawable(view.resources.getDrawable(R.drawable.ic_check_completed))
        view.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(view.context, R.color.green_end))
    }
}
