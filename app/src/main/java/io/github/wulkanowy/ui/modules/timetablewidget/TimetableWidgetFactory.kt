package io.github.wulkanowy.ui.modules.timetablewidget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView.INVALID_POSITION
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.data.repositories.timetable.TimetableRepository
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getDateWidgetKey
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getStudentWidgetKey
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getThemeWidgetKey
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.toFormattedString
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import timber.log.Timber

class TimetableWidgetFactory(
    private val timetableRepository: TimetableRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val prefRepository: PreferencesRepository,
    private val sharedPref: SharedPrefProvider,
    private val schedulers: SchedulersProvider,
    private val context: Context,
    private val intent: Intent?
) : RemoteViewsService.RemoteViewsFactory {

    private var lessons = emptyList<Timetable>()

    private var savedTheme: Long? = null

    private var layoutId: Int? = null

    private var primaryColor: Int? = null

    private var textColor: Int? = null

    private var timetableChangeColor: Int? = null

    override fun getLoadingView() = null

    override fun hasStableIds() = true

    override fun getCount() = lessons.size

    override fun getViewTypeCount() = 2

    override fun getItemId(position: Int) = position.toLong()

    override fun onCreate() {}

    override fun onDestroy() {}

    override fun onDataSetChanged() {
        intent?.extras?.getInt(EXTRA_APPWIDGET_ID)?.let { appWidgetId ->
            val date = LocalDate.ofEpochDay(sharedPref.getLong(getDateWidgetKey(appWidgetId), 0))
            val studentId = sharedPref.getLong(getStudentWidgetKey(appWidgetId), 0)

            updateTheme(appWidgetId)

            updateLessons(date, studentId)
        }
    }

    private fun updateTheme(appWidgetId: Int) {
        savedTheme = sharedPref.getLong(getThemeWidgetKey(appWidgetId), 0)
        layoutId = if (savedTheme == 0L) R.layout.item_widget_timetable else R.layout.item_widget_timetable_dark

        primaryColor = if (savedTheme == 0L) R.color.colorPrimary else R.color.colorPrimaryLight
        textColor = if (savedTheme == 0L) android.R.color.black else android.R.color.white
        timetableChangeColor = if (savedTheme == 0L) R.color.timetable_change_dark else R.color.timetable_change_light
    }

    private fun getItemLayout(lesson: Timetable): Int {
        return when {
            prefRepository.showWholeClassPlan == "small" && !lesson.isStudentPlan -> {
                if (savedTheme == 0L) R.layout.item_widget_timetable_small
                else R.layout.item_widget_timetable_small_dark
            }
            savedTheme == 0L -> R.layout.item_widget_timetable
            else -> R.layout.item_widget_timetable_dark
        }
    }

    private fun updateLessons(date: LocalDate, studentId: Long) {
        lessons = try {
            studentRepository.isStudentSaved()
                .filter { true }
                .flatMap { studentRepository.getSavedStudents().toMaybe() }
                .flatMap {
                    val student = it.singleOrNull { student -> student.id == studentId }

                    if (student != null) Maybe.just(student)
                    else Maybe.empty()
                }
                .flatMap { semesterRepository.getCurrentSemester(it).toMaybe() }
                .flatMap { timetableRepository.getTimetable(it, date, date).toMaybe() }
                .map { items -> items.sortedWith(compareBy({ it.number }, { !it.isStudentPlan })) }
                .map { lessons -> lessons.filter { if (prefRepository.showWholeClassPlan == "no") it.isStudentPlan else true } }
                .subscribeOn(schedulers.backgroundThread)
                .blockingGet(emptyList())
        } catch (e: Exception) {
            Timber.e(e, "An error has occurred in timetable widget factory")
            emptyList()
        }
    }

    @SuppressLint("DefaultLocale")
    override fun getViewAt(position: Int): RemoteViews? {
        if (position == INVALID_POSITION || lessons.getOrNull(position) == null) return null

        val lesson = lessons[position]
        return RemoteViews(context.packageName, getItemLayout(lesson)).apply {
            setTextViewText(R.id.timetableWidgetItemSubject, lesson.subject)
            setTextViewText(R.id.timetableWidgetItemNumber, lesson.number.toString())
            setTextViewText(R.id.timetableWidgetItemTimeStart, lesson.start.toFormattedString("HH:mm"))
            setTextViewText(R.id.timetableWidgetItemTimeFinish, lesson.end.toFormattedString("HH:mm"))

            updateDescription(this, lesson)

            if (lesson.canceled) {
                updateStylesCanceled(this)
            } else {
                updateStylesNotCanceled(this, lesson)
            }

            setOnClickFillInIntent(R.id.timetableWidgetItemContainer, Intent())
        }
    }

    private fun updateDescription(remoteViews: RemoteViews, lesson: Timetable) {
        with(remoteViews) {
            if (lesson.info.isNotBlank() && !lesson.changes) {
                setTextViewText(R.id.timetableWidgetItemDescription, lesson.info)
                setViewVisibility(R.id.timetableWidgetItemDescription, VISIBLE)
                setViewVisibility(R.id.timetableWidgetItemRoom, GONE)
                setViewVisibility(R.id.timetableWidgetItemTeacher, GONE)
            } else {
                setViewVisibility(R.id.timetableWidgetItemDescription, GONE)
                setViewVisibility(R.id.timetableWidgetItemRoom, VISIBLE)
                setViewVisibility(R.id.timetableWidgetItemTeacher, VISIBLE)
            }
        }
    }

    private fun updateStylesCanceled(remoteViews: RemoteViews) {
        with(remoteViews) {
            setInt(R.id.timetableWidgetItemSubject, "setPaintFlags",
                STRIKE_THRU_TEXT_FLAG or ANTI_ALIAS_FLAG)
            setTextColor(R.id.timetableWidgetItemNumber, context.getCompatColor(primaryColor!!))
            setTextColor(R.id.timetableWidgetItemSubject, context.getCompatColor(primaryColor!!))
            setTextColor(R.id.timetableWidgetItemDescription, context.getCompatColor(primaryColor!!))
        }
    }

    private fun updateStylesNotCanceled(remoteViews: RemoteViews, lesson: Timetable) {
        with(remoteViews) {
            setInt(R.id.timetableWidgetItemSubject, "setPaintFlags", ANTI_ALIAS_FLAG)
            setTextColor(R.id.timetableWidgetItemSubject, context.getCompatColor(textColor!!))
            setTextColor(R.id.timetableWidgetItemDescription, context.getCompatColor(timetableChangeColor!!))

            updateNotCanceledLessonNumberColor(this, lesson)
            updateNotCanceledSubjectColor(this, lesson)

            val teacherChange = lesson.teacherOld.isNotBlank() && lesson.teacher != lesson.teacherOld
            updateNotCanceledRoom(this, lesson, teacherChange)
            updateNotCanceledTeacher(this, lesson, teacherChange)
        }
    }

    private fun updateNotCanceledLessonNumberColor(remoteViews: RemoteViews, lesson: Timetable) {
        remoteViews.setTextColor(R.id.timetableWidgetItemNumber, context.getCompatColor(
            if (lesson.changes || (lesson.info.isNotBlank() && !lesson.canceled)) timetableChangeColor!!
            else textColor!!
        ))
    }

    private fun updateNotCanceledSubjectColor(remoteViews: RemoteViews, lesson: Timetable) {
        remoteViews.setTextColor(R.id.timetableWidgetItemSubject, context.getCompatColor(
            if (lesson.subjectOld.isNotBlank() && lesson.subject != lesson.subjectOld) timetableChangeColor!!
            else textColor!!
        ))
    }

    private fun updateNotCanceledRoom(remoteViews: RemoteViews, lesson: Timetable, teacherChange: Boolean) {
        with(remoteViews) {
            if (lesson.room.isNotBlank()) {
                setTextViewText(R.id.timetableWidgetItemRoom,
                    if (teacherChange) lesson.room
                    else "${context.getString(R.string.timetable_room)} ${lesson.room}"
                )

                setTextColor(R.id.timetableWidgetItemRoom, context.getCompatColor(
                    if (lesson.roomOld.isNotBlank() && lesson.room != lesson.roomOld) timetableChangeColor!!
                    else textColor!!
                ))
            } else setTextViewText(R.id.timetableWidgetItemRoom, "")
        }
    }

    private fun updateNotCanceledTeacher(remoteViews: RemoteViews, lesson: Timetable, teacherChange: Boolean) {
        remoteViews.setTextViewText(R.id.timetableWidgetItemTeacher,
            if (teacherChange) lesson.teacher
            else ""
        )
    }
}
