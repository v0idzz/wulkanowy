package io.github.wulkanowy.ui.modules.settings

import android.content.Context
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.yariksoffice.lingver.Lingver
import dagger.android.support.AndroidSupportInjection
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.ErrorDialog
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.AppInfo
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat(), MainView.TitledView, SettingsView {

    @Inject
    lateinit var presenter: SettingsPresenter

    @Inject
    lateinit var appInfo: AppInfo

    @Inject
    lateinit var lingver: Lingver

    @Inject
    lateinit var dataStore: SettingsDataStore

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override val titleStringId get() = R.string.settings_title

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        dataStore.onChangeCallback = { presenter.onSharedPreferenceChanged(it) }
        preferenceManager.preferenceDataStore = dataStore
        setPreferencesFromResource(R.xml.scheme_preferences, rootKey)
        findPreference<Preference>(getString(R.string.pref_key_global_notification_debug))?.isVisible = appInfo.isDebug
    }

    override fun recreateView() {
        activity?.recreate()
    }

    override fun updateLanguage(langCode: String) {
        lingver.setLocale(requireContext(), langCode)
    }

    override fun setServicesSuspended(serviceEnablesKey: String, isHolidays: Boolean) {
        findPreference<Preference>(serviceEnablesKey)?.apply {
            summary = if (isHolidays) getString(R.string.pref_services_suspended) else ""
            isEnabled = !isHolidays
        }
    }

    override fun showError(text: String, error: Throwable) {
        (activity as? BaseActivity<*>)?.showError(text, error)
    }

    override fun showMessage(text: String) {
        (activity as? BaseActivity<*>)?.showMessage(text)
    }

    override fun showExpiredDialog() {
        (activity as? BaseActivity<*>)?.showExpiredDialog()
    }

    override fun openClearLoginView() {
        (activity as? BaseActivity<*>)?.openClearLoginView()
    }

    override fun showErrorDetailsDialog(error: Throwable) {
        ErrorDialog.newInstance(error).show(childFragmentManager, error.toString())
    }
}
