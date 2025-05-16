package com.vishwaagrotech.digitalhr.repository.mapper

import android.text.format.DateFormat
import com.vishwaagrotech.digitalhr.model.Notice
import com.vishwaagrotech.digitalhr.repository.network.api.notice.NoticeData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object NoticeMapper {

    fun mapToEntity(notice: NoticeData): Notice {
        val fullDate = notice.notice_published_date
        val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)
        val date = format.parse(fullDate)

        val day = DateFormat.format("dd", date) as String
        val monthNumber = DateFormat.format("MMM", date) as String

        return Notice(
            notice.id,
            notice.notice_title,
            notice.description,
            monthNumber,
            day
        )
    }

    fun mapToEntityList(entities: ArrayList<NoticeData>): List<Notice> {
        return entities.map {
            mapToEntity(it)
        }
    }
}