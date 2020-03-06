package io.github.wulkanowy.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.JournalMode.TRUNCATE
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import io.github.wulkanowy.data.db.dao.AttendanceDao
import io.github.wulkanowy.data.db.dao.AttendanceSummaryDao
import io.github.wulkanowy.data.db.dao.CompletedLessonsDao
import io.github.wulkanowy.data.db.dao.ExamDao
import io.github.wulkanowy.data.db.dao.GradeDao
import io.github.wulkanowy.data.db.dao.GradePointsStatisticsDao
import io.github.wulkanowy.data.db.dao.GradeStatisticsDao
import io.github.wulkanowy.data.db.dao.GradeSummaryDao
import io.github.wulkanowy.data.db.dao.HomeworkDao
import io.github.wulkanowy.data.db.dao.LuckyNumberDao
import io.github.wulkanowy.data.db.dao.MessagesDao
import io.github.wulkanowy.data.db.dao.MobileDeviceDao
import io.github.wulkanowy.data.db.dao.NoteDao
import io.github.wulkanowy.data.db.dao.RecipientDao
import io.github.wulkanowy.data.db.dao.ReportingUnitDao
import io.github.wulkanowy.data.db.dao.SchoolDao
import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.dao.SubjectDao
import io.github.wulkanowy.data.db.dao.TeacherDao
import io.github.wulkanowy.data.db.dao.TimetableDao
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.School
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.data.db.entities.Teacher
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.db.migrations.Migration10
import io.github.wulkanowy.data.db.migrations.Migration11
import io.github.wulkanowy.data.db.migrations.Migration12
import io.github.wulkanowy.data.db.migrations.Migration13
import io.github.wulkanowy.data.db.migrations.Migration14
import io.github.wulkanowy.data.db.migrations.Migration15
import io.github.wulkanowy.data.db.migrations.Migration16
import io.github.wulkanowy.data.db.migrations.Migration17
import io.github.wulkanowy.data.db.migrations.Migration18
import io.github.wulkanowy.data.db.migrations.Migration19
import io.github.wulkanowy.data.db.migrations.Migration2
import io.github.wulkanowy.data.db.migrations.Migration20
import io.github.wulkanowy.data.db.migrations.Migration21
import io.github.wulkanowy.data.db.migrations.Migration22
import io.github.wulkanowy.data.db.migrations.Migration3
import io.github.wulkanowy.data.db.migrations.Migration4
import io.github.wulkanowy.data.db.migrations.Migration5
import io.github.wulkanowy.data.db.migrations.Migration6
import io.github.wulkanowy.data.db.migrations.Migration7
import io.github.wulkanowy.data.db.migrations.Migration8
import io.github.wulkanowy.data.db.migrations.Migration9
import javax.inject.Singleton

@Singleton
@Database(
    entities = [
        Student::class,
        Semester::class,
        Exam::class,
        Timetable::class,
        Attendance::class,
        AttendanceSummary::class,
        Grade::class,
        GradeSummary::class,
        GradeStatistics::class,
        GradePointsStatistics::class,
        Message::class,
        Note::class,
        Homework::class,
        Subject::class,
        LuckyNumber::class,
        CompletedLesson::class,
        ReportingUnit::class,
        Recipient::class,
        MobileDevice::class,
        Teacher::class,
        School::class
    ],
    version = AppDatabase.VERSION_SCHEMA,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val VERSION_SCHEMA = 22

        fun getMigrations(sharedPrefProvider: SharedPrefProvider): Array<Migration> {
            return arrayOf(
                Migration2(),
                Migration3(),
                Migration4(),
                Migration5(),
                Migration6(),
                Migration7(),
                Migration8(),
                Migration9(),
                Migration10(),
                Migration11(),
                Migration12(),
                Migration13(),
                Migration14(),
                Migration15(),
                Migration16(),
                Migration17(),
                Migration18(),
                Migration19(sharedPrefProvider),
                Migration20(),
                Migration21(),
                Migration22()
            )
        }

        fun newInstance(context: Context, sharedPrefProvider: SharedPrefProvider): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "wulkanowy_database")
                .setJournalMode(TRUNCATE)
                .fallbackToDestructiveMigrationFrom(VERSION_SCHEMA + 1)
                .fallbackToDestructiveMigrationOnDowngrade()
                .addMigrations(*getMigrations(sharedPrefProvider))
                .build()
        }
    }

    abstract val studentDao: StudentDao

    abstract val semesterDao: SemesterDao

    abstract val examsDao: ExamDao

    abstract val timetableDao: TimetableDao

    abstract val attendanceDao: AttendanceDao

    abstract val attendanceSummaryDao: AttendanceSummaryDao

    abstract val gradeDao: GradeDao

    abstract val gradeSummaryDao: GradeSummaryDao

    abstract val gradeStatistics: GradeStatisticsDao

    abstract val gradePointsStatistics: GradePointsStatisticsDao

    abstract val messagesDao: MessagesDao

    abstract val noteDao: NoteDao

    abstract val homeworkDao: HomeworkDao

    abstract val subjectDao: SubjectDao

    abstract val luckyNumberDao: LuckyNumberDao

    abstract val completedLessonsDao: CompletedLessonsDao

    abstract val reportingUnitDao: ReportingUnitDao

    abstract val recipientDao: RecipientDao

    abstract val mobileDeviceDao: MobileDeviceDao

    abstract val teacherDao: TeacherDao

    abstract val schoolDao: SchoolDao
}
