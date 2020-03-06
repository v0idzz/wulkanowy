package io.github.wulkanowy.data.repositories.gradestatistics

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate.now
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class GradeStatisticsLocalTest {

    private lateinit var gradeStatisticsLocal: GradeStatisticsLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun createDb() {
        testDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .build()
        gradeStatisticsLocal = GradeStatisticsLocal(testDb.gradeStatistics, testDb.gradePointsStatistics)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndRead_subject() {
        gradeStatisticsLocal.saveGradesStatistics(listOf(
            getGradeStatistics("Matematyka", 2, 1),
            getGradeStatistics("Fizyka", 1, 2)
        ))

        val stats = gradeStatisticsLocal.getGradesStatistics(getSemester(), false, "Matematyka").blockingGet()
        assertEquals(1, stats.size)
        assertEquals(stats[0].subject, "Matematyka")
    }

    @Test
    fun saveAndRead_all() {
        gradeStatisticsLocal.saveGradesStatistics(listOf(
            getGradeStatistics("Matematyka", 2, 1),
            getGradeStatistics("Chemia", 2, 1),
            getGradeStatistics("Fizyka", 1, 2)
        ))

        val stats = gradeStatisticsLocal.getGradesStatistics(getSemester(), false, "Wszystkie").blockingGet()
        assertEquals(3, stats.size)
        assertEquals(stats[0].subject, "Wszystkie")
        assertEquals(stats[1].subject, "Matematyka")
        assertEquals(stats[2].subject, "Chemia")
    }

    @Test
    fun saveAndRead_points() {
        gradeStatisticsLocal.saveGradesPointsStatistics(listOf(
            getGradePointsStatistics("Matematyka", 2, 1),
            getGradePointsStatistics("Chemia", 2, 1),
            getGradePointsStatistics("Fizyka", 1, 2)
        ))

        val stats = gradeStatisticsLocal.getGradesPointsStatistics(getSemester(), "Matematyka").blockingGet()
        with(stats[0]) {
            assertEquals(subject, "Matematyka")
            assertEquals(others, 5.0)
            assertEquals(student, 5.0)
        }
    }

    @Test
    fun saveAndRead_subjectEmpty() {
        gradeStatisticsLocal.saveGradesPointsStatistics(listOf())

        val stats = gradeStatisticsLocal.getGradesPointsStatistics(getSemester(), "Matematyka").blockingGet()
        assertEquals(null, stats)
    }

    @Test
    fun saveAndRead_allEmpty() {
        gradeStatisticsLocal.saveGradesPointsStatistics(listOf())

        val stats = gradeStatisticsLocal.getGradesPointsStatistics(getSemester(), "Wszystkie").blockingGet()
        assertEquals(null, stats)
    }

    private fun getSemester(): Semester {
        return Semester(2, 2, "", 2019, 1, 2, now(), now(), 1, 1)
    }

    private fun getGradeStatistics(subject: String, studentId: Int, semesterId: Int): GradeStatistics {
        return GradeStatistics(studentId, semesterId, subject, 5, 5, false)
    }

    private fun getGradePointsStatistics(subject: String, studentId: Int, semesterId: Int): GradePointsStatistics {
        return GradePointsStatistics(studentId, semesterId, subject, 5.0, 5.0)
    }
}
