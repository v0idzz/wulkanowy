package io.github.wulkanowy.ui.modules.license

import com.mikepenz.aboutlibraries.entity.Library
import io.github.wulkanowy.ui.base.BaseView

interface LicenseView : BaseView {

    val appLibraries: ArrayList<Library>?

    fun initView()

    fun updateData(data: List<LicenseItem>)

    fun openLicense(licenseHtml: String)

    fun showProgress(show: Boolean)
}
