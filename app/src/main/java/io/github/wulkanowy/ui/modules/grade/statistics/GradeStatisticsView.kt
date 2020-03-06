package io.github.wulkanowy.ui.modules.grade.statistics

import io.github.wulkanowy.data.pojos.GradeStatisticsItem
import io.github.wulkanowy.ui.base.BaseView

interface GradeStatisticsView : BaseView {

    val isViewEmpty: Boolean

    val currentType: ViewType

    fun initView()

    fun updateSubjects(data: ArrayList<String>)

    fun updateData(items: List<GradeStatisticsItem>, theme: String)

    fun showSubjects(show: Boolean)

    fun notifyParentDataLoaded(semesterId: Int)

    fun notifyParentRefresh()

    fun clearView()

    fun resetView()

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showRefresh(show: Boolean)
}
