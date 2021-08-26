package io.github.wulkanowy.ui.modules.more

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentMoreBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.conference.ConferenceFragment
import io.github.wulkanowy.ui.modules.exam.ExamFragment
import io.github.wulkanowy.ui.modules.homework.HomeworkFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.mobiledevice.MobileDeviceFragment
import io.github.wulkanowy.ui.modules.note.NoteFragment
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersFragment
import io.github.wulkanowy.ui.modules.schoolannouncement.SchoolAnnouncementFragment
import io.github.wulkanowy.ui.modules.settings.SettingsFragment
import io.github.wulkanowy.utils.getCompatDrawable
import javax.inject.Inject

@AndroidEntryPoint
class MoreFragment : BaseFragment<FragmentMoreBinding>(R.layout.fragment_more), MoreView,
    MainView.TitledView, MainView.MainChildView {

    @Inject
    lateinit var presenter: MorePresenter

    @Inject
    lateinit var moreAdapter: MoreAdapter

    companion object {
        fun newInstance() = MoreFragment()
    }

    override val titleStringId: Int
        get() = R.string.more_title

    override val messagesRes: Pair<String, Drawable?>?
        get() = context?.run { getString(R.string.message_title) to getCompatDrawable(R.drawable.ic_more_messages) }

    override val homeworkRes: Pair<String, Drawable?>?
        get() = context?.run { getString(R.string.homework_title) to getCompatDrawable(R.drawable.ic_more_homework) }

    override val noteRes: Pair<String, Drawable?>?
        get() = context?.run { getString(R.string.note_title) to getCompatDrawable(R.drawable.ic_more_note) }

    override val conferencesRes: Pair<String, Drawable?>?
        get() = context?.run { getString(R.string.conferences_title) to getCompatDrawable(R.drawable.ic_more_conferences) }

    override val schoolAnnouncementRes: Pair<String, Drawable?>?
        get() = context?.run { getString(R.string.school_announcement_title) to getCompatDrawable(R.drawable.ic_all_about) }

    override val schoolAndTeachersRes: Pair<String, Drawable?>?
        get() = context?.run { getString(R.string.schoolandteachers_title) to getCompatDrawable((R.drawable.ic_more_schoolandteachers)) }

    override val mobileDevicesRes: Pair<String, Drawable?>?
        get() = context?.run { getString(R.string.mobile_devices_title) to getCompatDrawable(R.drawable.ic_more_mobile_devices) }

    override val settingsRes: Pair<String, Drawable?>?
        get() = context?.run { getString(R.string.settings_title) to getCompatDrawable(R.drawable.ic_more_settings) }

    override val examRes: Pair<String, Drawable?>?
        get() = context?.run { getString(R.string.exam_title) to getCompatDrawable(R.drawable.ic_main_exam) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMoreBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        moreAdapter.onClickListener = presenter::onItemSelected

        with(binding.moreRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = moreAdapter
        }
    }

    override fun onFragmentReselected() {
        if (::presenter.isInitialized) presenter.onViewReselected()
    }

    override fun updateData(data: List<Pair<String, Drawable?>>) {
        with(moreAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun openMessagesView() {
        (activity as? MainActivity)?.pushView(MessageFragment.newInstance())
    }

    override fun openHomeworkView() {
        (activity as? MainActivity)?.pushView(HomeworkFragment.newInstance())
    }

    override fun openNoteView() {
        (activity as? MainActivity)?.pushView(NoteFragment.newInstance())
    }

    override fun openSchoolAnnouncementView() {
        (activity as? MainActivity)?.pushView(SchoolAnnouncementFragment.newInstance())
    }

    override fun openConferencesView() {
        (activity as? MainActivity)?.pushView(ConferenceFragment.newInstance())
    }

    override fun openSchoolAndTeachersView() {
        (activity as? MainActivity)?.pushView(SchoolAndTeachersFragment.newInstance())
    }

    override fun openMobileDevicesView() {
        (activity as? MainActivity)?.pushView(MobileDeviceFragment.newInstance())
    }

    override fun openSettingsView() {
        (activity as? MainActivity)?.pushView(SettingsFragment.newInstance())
    }

    override fun openExamView() {
        (activity as? MainActivity)?.pushView(ExamFragment.newInstance())
    }

    override fun popView(depth: Int) {
        (activity as? MainActivity)?.popView(depth)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
