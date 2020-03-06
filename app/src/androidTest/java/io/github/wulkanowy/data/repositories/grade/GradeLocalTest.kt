package io.github.wulkanowy.data.repositories.grade

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Semester
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.now
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class GradeLocalTest {

    private lateinit var gradeLocal: GradeLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun createDb() {
        testDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .build()
        gradeLocal = GradeLocal(testDb.gradeDao)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        gradeLocal.saveGrades(listOf(
            createGradeLocal(5, 3.0, LocalDate.of(2018, 9, 10), "", 1),
            createGradeLocal(4, 4.0, LocalDate.of(2019, 2, 27), "", 2),
            createGradeLocal(3, 5.0, LocalDate.of(2019, 2, 28), "", 2)
        ))

        val semester = Semester(1, 2, "", 2019, 2, 1, now(), now(), 1, 1)

        val grades = gradeLocal
            .getGrades(semester)
            .blockingGet()

        assertEquals(2, grades.size)
        assertEquals(grades[0].date, LocalDate.of(2019, 2, 27))
        assertEquals(grades[1].date, LocalDate.of(2019, 2, 28))
    }
}
