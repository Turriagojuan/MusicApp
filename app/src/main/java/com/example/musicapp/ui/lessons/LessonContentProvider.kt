package com.example.musicapp.ui.lessons

import androidx.annotation.StringRes
import com.example.musicapp.R

data class LessonContent(
    val lessonId: String,
    @StringRes val titleResId: Int,
    val pages: List<LessonPage>
)

data class LessonPage(
    @StringRes val textResId: Int
)

object LessonContentProvider {
    fun getLesson(lessonId: String): LessonContent? {
        return allLessons.find { it.lessonId == lessonId }
    }

    private val allLessons = listOf(
        LessonContent("1.1", R.string.lesson_1_1_title, listOf(LessonPage(R.string.lesson_1_1_page_1), LessonPage(R.string.lesson_1_1_page_2))),
        LessonContent("1.2", R.string.lesson_1_2_title, listOf(LessonPage(R.string.lesson_1_2_page_1), LessonPage(R.string.lesson_1_2_page_2), LessonPage(R.string.lesson_1_2_page_3), LessonPage(R.string.lesson_1_2_page_4))),
        LessonContent("1.3", R.string.lesson_1_3_title, listOf(LessonPage(R.string.lesson_1_3_page_1), LessonPage(R.string.lesson_1_3_page_2))),
        LessonContent("2.1", R.string.lesson_2_1_title, listOf(LessonPage(R.string.lesson_2_1_page_1))),
        LessonContent("2.2", R.string.lesson_2_2_title, listOf(LessonPage(R.string.lesson_2_2_page_1))),
        LessonContent("2.3", R.string.lesson_2_3_title, listOf(LessonPage(R.string.lesson_2_3_page_1))),
        LessonContent("2.4", R.string.lesson_2_4_title, listOf(LessonPage(R.string.lesson_2_4_page_1)))
    )
}