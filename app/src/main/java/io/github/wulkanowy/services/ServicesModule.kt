package io.github.wulkanowy.services

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoSet
import io.github.wulkanowy.services.sync.channels.Channel
import io.github.wulkanowy.services.sync.channels.DebugChannel
import io.github.wulkanowy.services.sync.channels.LuckyNumberChannel
import io.github.wulkanowy.services.sync.channels.NewGradesChannel
import io.github.wulkanowy.services.sync.channels.NewMessagesChannel
import io.github.wulkanowy.services.sync.channels.NewNotesChannel
import io.github.wulkanowy.services.sync.works.AttendanceSummaryWork
import io.github.wulkanowy.services.sync.works.AttendanceWork
import io.github.wulkanowy.services.sync.works.CompletedLessonWork
import io.github.wulkanowy.services.sync.works.ExamWork
import io.github.wulkanowy.services.sync.works.GradeStatisticsWork
import io.github.wulkanowy.services.sync.works.GradeSummaryWork
import io.github.wulkanowy.services.sync.works.GradeWork
import io.github.wulkanowy.services.sync.works.HomeworkWork
import io.github.wulkanowy.services.sync.works.LuckyNumberWork
import io.github.wulkanowy.services.sync.works.MessageWork
import io.github.wulkanowy.services.sync.works.NoteWork
import io.github.wulkanowy.services.sync.works.RecipientWork
import io.github.wulkanowy.services.sync.works.TeacherWork
import io.github.wulkanowy.services.sync.works.TimetableWork
import io.github.wulkanowy.services.sync.works.Work
import io.github.wulkanowy.services.widgets.TimetableWidgetService
import javax.inject.Singleton

@Suppress("unused")
@AssistedModule
@Module(includes = [AssistedInject_ServicesModule::class])
abstract class ServicesModule {

    companion object {

        @Provides
        fun provideWorkManager(context: Context) = WorkManager.getInstance(context)

        @Singleton
        @Provides
        fun provideNotificationManager(context: Context) = NotificationManagerCompat.from(context)
    }

    @ContributesAndroidInjector
    abstract fun bindTimetableWidgetService(): TimetableWidgetService

    @Binds
    @IntoSet
    abstract fun provideGradeWork(work: GradeWork): Work

    @Binds
    @IntoSet
    abstract fun provideNoteWork(work: NoteWork): Work

    @Binds
    @IntoSet
    abstract fun provideAttendanceWork(work: AttendanceWork): Work

    @Binds
    @IntoSet
    abstract fun provideGradeSummaryWork(work: GradeSummaryWork): Work

    @Binds
    @IntoSet
    abstract fun provideExamWork(work: ExamWork): Work

    @Binds
    @IntoSet
    abstract fun provideAttendanceSummaryWork(work: AttendanceSummaryWork): Work

    @Binds
    @IntoSet
    abstract fun provideTimetableWork(work: TimetableWork): Work

    @Binds
    @IntoSet
    abstract fun provideTeacherWork(work: TeacherWork): Work

    @Binds
    @IntoSet
    abstract fun provideLuckyNumberWork(work: LuckyNumberWork): Work

    @Binds
    @IntoSet
    abstract fun provideCompletedLessonWork(work: CompletedLessonWork): Work

    @Binds
    @IntoSet
    abstract fun provideHomeworkWork(work: HomeworkWork): Work

    @Binds
    @IntoSet
    abstract fun provideMessageWork(work: MessageWork): Work

    @Binds
    @IntoSet
    abstract fun provideRecipientWork(work: RecipientWork): Work

    @Binds
    @IntoSet
    abstract fun provideGradeStatistics(work: GradeStatisticsWork): Work

    @Binds
    @IntoSet
    abstract fun provideDebugChannel(channel: DebugChannel): Channel

    @Binds
    @IntoSet
    abstract fun provideLuckyNumberChannel(channel: LuckyNumberChannel): Channel

    @Binds
    @IntoSet
    abstract fun provideNewGradesChannel(channel: NewGradesChannel): Channel

    @Binds
    @IntoSet
    abstract fun provideNewMessageChannel(channel: NewMessagesChannel): Channel

    @Binds
    @IntoSet
    abstract fun provideNewNotesChannel(channel: NewNotesChannel): Channel
}
