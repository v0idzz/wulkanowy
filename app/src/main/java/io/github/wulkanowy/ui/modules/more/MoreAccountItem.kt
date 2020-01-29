package io.github.wulkanowy.ui.modules.more

import android.annotation.SuppressLint
import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_more_account.*

class MoreAccountItem(private val student: Student) :
    AbstractFlexibleItem<MoreAccountItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_more_account

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?) =
        ViewHolder(view, adapter)

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<*>>,
        holder: ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        with(holder) {
            moreAccountItemName.text = student.studentName
            moreAccountItemClass.text = student.className
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MoreAccountItem

        if (student != other.student) return false

        return true
    }

    override fun hashCode() = student.hashCode()

    class ViewHolder(view: View?, adapter: FlexibleAdapter<*>?) : FlexibleViewHolder(view, adapter),
        LayoutContainer {

        override val containerView: View? get() = contentView
    }
}