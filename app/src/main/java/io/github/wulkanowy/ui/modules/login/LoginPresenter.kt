package io.github.wulkanowy.ui.modules.login

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<LoginView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: LoginView) {
        super.onAttachView(view)
        with(view) {
            initView()
            showActionBar(false)
        }
        Timber.i("Login view was initialized")
    }

    fun onFormViewAccountLogged(students: List<Student>, loginData: Triple<String, String, String>) {
        view?.apply {
            if (students.isEmpty()) {
                Timber.i("Switch to symbol form")
                notifyInitSymbolFragment(loginData)
                switchView(1)
            } else {
                Timber.i("Switch to student select")
                notifyInitStudentSelectFragment(students)
                switchView(2)
            }
        }
    }

    fun onSymbolViewAccountLogged(students: List<Student>) {
        view?.apply {
            Timber.i("Switch to student select")
            notifyInitStudentSelectFragment(students)
            switchView(2)
        }
    }

    fun onAdvancedLoginClick() {
        view?.switchView(3)
    }

    fun onRecoverClick() {
        view?.switchView(4)
    }

    fun onViewSelected(index: Int) {
        view?.apply {
            when (index) {
                0 -> showActionBar(false)
                1, 2, 3, 4 -> showActionBar(true)
            }
        }
    }

    fun onBackPressed(default: () -> Unit) {
        Timber.i("Back pressed in login view")
        view?.apply {
            when (currentViewIndex) {
                1, 2, 3, 4 -> switchView(0)
                else -> default()
            }
        }
    }
}
