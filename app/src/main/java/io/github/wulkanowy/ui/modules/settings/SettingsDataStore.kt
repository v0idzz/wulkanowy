package io.github.wulkanowy.ui.modules.settings

import android.content.Context
import androidx.preference.PreferenceDataStore
import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Completable
import timber.log.Timber
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import javax.inject.Inject

class SettingsDataStore @Inject constructor(
    context: Context,
    studentRepository: StudentRepository,
    private val schedulersProvider: SchedulersProvider,
    private val preferencesRepository: PreferencesRepository
) : PreferenceDataStore() {

    var onChangeCallback: (String) -> Unit = {}

    val studentId by lazy {
        studentRepository.getCurrentStudent(false).blockingGet().studentId
    }

    private val globalSettings = listOf(
        context.getString(R.string.pref_key_global_start_menu),
        context.getString(R.string.pref_key_global_app_theme),
        context.getString(R.string.pref_key_global_grade_color_scheme),
        context.getString(R.string.pref_key_global_expand_grade),
        context.getString(R.string.pref_key_global_app_language),
        context.getString(R.string.pref_key_global_services_enable),
        context.getString(R.string.pref_key_global_services_interval),
        context.getString(R.string.pref_key_global_services_wifi_only),
        context.getString(R.string.pref_key_global_fill_message_content),
        context.getString(R.string.pref_key_global_notifications_enable),
        context.getString(R.string.pref_key_global_notification_debug)
    )

    override fun getString(key: String, defValue: String?): String? {
        Timber.v("getString($key, $defValue)")
        return when (key) {
            in globalSettings -> preferencesRepository.getString(key, defValue)
            else -> getUserValue(key) ?: defValue
        }
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        Timber.v("getBoolean($key, $defValue)")
        return when (key) {
            in globalSettings -> preferencesRepository.getBoolean(key, defValue)
            else -> getUserValue(key)?.toBoolean() ?: defValue
        }
    }

    override fun getInt(key: String, defValue: Int): Int {
        Timber.v("getInt($key, $defValue)")
        return when (key) {
            in globalSettings -> preferencesRepository.getInt(key, defValue)
            else -> getUserValue(key)?.toInt() ?: defValue
        }
    }

    override fun getLong(key: String, defValue: Long): Long {
        Timber.v("getLong($key, $defValue)")
        return when (key) {
            in globalSettings -> preferencesRepository.getLong(key, defValue)
            else -> getUserValue(key)?.toLong() ?: defValue
        }
    }

    private fun getUserValue(key: String): String? {
        Timber.v("getUserValue($key)")

        return Executors.newSingleThreadExecutor().submit(Callable {
            Timber.v(" for $studentId")
            preferencesRepository.getSetting(studentId, key)
        }).get()
    }

    override fun putString(key: String, value: String?) = putValue(key, value)
    override fun putBoolean(key: String, value: Boolean) = putValue(key, value)
    override fun putInt(key: String, value: Int) = putValue(key, value)
    override fun putLong(key: String, value: Long) = putValue(key, value)

    private fun <T> putValue(key: String, value: T) {
        Timber.v("putValue: $key=$value")

        when (key) {
            in globalSettings -> preferencesRepository.putValue(key, value)
            else -> Completable.create {
                preferencesRepository.putSetting(studentId, key, value)
            }.subscribeOn(schedulersProvider.backgroundThread).subscribe()
        }
        onChangeCallback(key)
    }
}
