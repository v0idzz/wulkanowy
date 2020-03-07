package io.github.wulkanowy.ui.modules.more

import android.graphics.drawable.Drawable
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.ui.base.BaseView

interface MoreView : BaseView {

    val messagesRes: Pair<String, Drawable?>?

    val homeworkRes: Pair<String, Drawable?>?

    val noteRes: Pair<String, Drawable?>?

    val luckyNumberRes: Pair<String, Drawable?>?

    val mobileDevicesRes: Pair<String, Drawable?>?

    val schoolAndTeachersRes: Pair<String, Drawable?>?

    fun initView()

    fun updateData(data: List<AbstractFlexibleItem<*>>)

    fun popView(depth: Int)

    fun openMessagesView()

    fun openHomeworkView()

    fun openNoteView()

    fun openLuckyNumberView()

    fun openMobileDevicesView()

    fun openSchoolAndTeachersView()
}
