package io.github.wulkanowy.data.repositories.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import io.github.wulkanowy.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
    private val sharedPref: SharedPreferences,
    private val local: PreferencesLocal,
    private val context: Context
) {

    val startMenuIndex: Int
        get() = getString(R.string.pref_key_global_start_menu, R.string.pref_default_startup).toInt()

    fun getShowPresent(studentId: Int) = getBooleanSetting(studentId, R.string.pref_key_user_attendance_present, R.bool.pref_default_attendance_present)

    fun getGradeAverageMode(studentId: Int) = getSetting(studentId, R.string.pref_key_user_grade_average_mode, R.string.pref_default_grade_average_mode)

    fun getGradeAverageForceCalc(studentId: Int) = getBooleanSetting(studentId, R.string.pref_key_user_grade_average_force_calc, R.bool.pref_default_grade_average_force_calc)

    val isGradeExpandable: Boolean
        get() = !getBoolean(R.string.pref_key_global_expand_grade, R.bool.pref_default_expand_grade)

    val appThemeKey = context.getString(R.string.pref_key_global_app_theme)
    val appTheme: String
        get() = getString(appThemeKey, R.string.pref_default_app_theme)

    val gradeColorTheme: String
        get() = getString(R.string.pref_key_global_grade_color_scheme, R.string.pref_default_grade_color_scheme)

    val appLanguageKey = context.getString(R.string.pref_key_global_app_language)
    val appLanguage
        get() = getString(appLanguageKey, R.string.pref_default_app_language)

    val serviceEnableKey = context.getString(R.string.pref_key_global_services_enable)
    val isServiceEnabled: Boolean
        get() = getBoolean(serviceEnableKey, R.bool.pref_default_services_enable)

    val servicesIntervalKey = context.getString(R.string.pref_key_global_services_interval)
    val servicesInterval: Long
        get() = getString(servicesIntervalKey, R.string.pref_default_services_interval).toLong()

    val servicesOnlyWifiKey = context.getString(R.string.pref_key_global_services_wifi_only)
    val isServicesOnlyWifi: Boolean
        get() = getBoolean(servicesOnlyWifiKey, R.bool.pref_default_services_wifi_only)

    val isNotificationsEnable: Boolean
        get() = getBoolean(R.string.pref_key_global_notifications_enable, R.bool.pref_default_notifications_enable)

    val isDebugNotificationEnableKey = context.getString(R.string.pref_key_global_notification_debug)
    val isDebugNotificationEnable: Boolean
        get() = getBoolean(isDebugNotificationEnableKey, R.bool.pref_default_notification_debug)

    fun getGradePlusModifier(studentId: Int) = getSetting(studentId, R.string.pref_key_user_grade_modifier_plus, R.string.pref_default_grade_modifier_plus).toDouble()

    fun getGradeMinusModifier(studentId: Int) = getSetting(studentId, R.string.pref_key_user_grade_modifier_minus, R.string.pref_default_grade_modifier_minus).toDouble()

    val fillMessageContent: Boolean
        get() = getBoolean(R.string.pref_key_global_fill_message_content, R.bool.pref_default_fill_message_content)

    fun isShowWholeClassPlan(studentId: Int) = getSetting(studentId, R.string.pref_key_user_timetable_show_whole_class, R.string.pref_default_timetable_show_whole_class)

    private fun getSetting(studentId: Int, key: Int, default: Int) = getSetting(studentId, context.getString(key)) ?: context.getString(default)

    private fun getBooleanSetting(studentId: Int, key: Int, default: Int) = getSetting(studentId, context.getString(key))?.toBoolean() ?: context.resources.getBoolean(default)

    private fun getString(id: Int, default: Int) = getString(context.getString(id), default)

    private fun getString(id: String, default: Int) = getString(id, context.getString(default)) ?: context.getString(default)

    private fun getBoolean(id: Int, default: Int) = getBoolean(context.getString(id), default)

    private fun getBoolean(id: String, default: Int) = getBoolean(id, context.resources.getBoolean(default))

    fun getBoolean(key: String, default: Boolean) = sharedPref.getBoolean(key, default)

    fun getString(key: String, default: String?) = sharedPref.getString(key, default)

    fun getInt(key: String, defValue: Int) = sharedPref.getInt(key, defValue)

    fun getLong(key: String, defValue: Long) = sharedPref.getLong(key, defValue)

    fun getSetting(studentId: Int, key: String?) = local.getPreference(studentId, key.orEmpty())?.value

    fun <T> putValue(key: String, value: T) {
        sharedPref.edit(true) {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
            }
        }
    }

    fun putSetting(studentId: Int, key: String?, value: Any?) {
        local.putPreference(studentId, key.orEmpty(), value.toString())
    }
}
