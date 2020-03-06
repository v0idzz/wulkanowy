package io.github.wulkanowy.ui.modules.login.form

import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.ifNullOrBlank
import timber.log.Timber
import javax.inject.Inject

class LoginFormPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    studentRepository: StudentRepository,
    private val loginErrorHandler: LoginErrorHandler,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<LoginFormView>(loginErrorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: LoginFormView) {
        super.onAttachView(view)
        view.run {
            initView()
            showContact(false)
            showVersion()

            loginErrorHandler.onBadCredentials = {
                setErrorPassIncorrect()
                showSoftKeyboard()
                Timber.i("Entered wrong username or password")
            }
        }
    }

    fun onPrivacyLinkClick() {
        view?.openPrivacyPolicyPage()
    }

    fun onAdvancedLoginClick() {
        view?.openAdvancedLogin()
    }

    fun onHostSelected() {
        view?.apply {
            clearPassError()
            clearUsernameError()
            if (formHostValue.contains("fakelog")) {
                setCredentials("jan@fakelog.cf", "jan123")
            }
            setSymbol(formHostSymbol)
            updateUsernameLabel()
            updateSymbolInputVisibility()
        }
    }

    fun updateUsernameLabel() {
        view?.run {
            setUsernameLabel(if ("standard" in formHostValue) emailLabel else nicknameLabel)
        }
    }

    fun updateSymbolInputVisibility() {
        view?.run {
            showSymbol("adfs" in formHostValue)
        }
    }

    fun onPassTextChanged() {
        view?.clearPassError()
    }

    fun onUsernameTextChanged() {
        view?.clearUsernameError()
    }

    fun onSymbolTextChanged() {
        view?.clearSymbolError()
    }

    fun onSignInClick() {
        val email = view?.formUsernameValue.orEmpty().trim()
        val password = view?.formPassValue.orEmpty().trim()
        val host = view?.formHostValue.orEmpty().trim()
        val symbol = view?.formSymbolValue.orEmpty().trim()

        if (!validateCredentials(email, password, host, symbol)) return

        disposable.add(studentRepository.getStudentsScrapper(email, password, host, symbol)
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.apply {
                    hideSoftKeyboard()
                    showProgress(true)
                    showContent(false)
                }
                Timber.i("Login started")
            }
            .doFinally {
                view?.apply {
                    showProgress(false)
                    showContent(true)
                }
            }
            .subscribe({
                Timber.i("Login result: Success")
                analytics.logEvent("registration_form", "success" to true, "students" to it.size, "scrapperBaseUrl" to host, "error" to "No error")
                view?.notifyParentAccountLogged(it, Triple(email, password, host))
            }, {
                Timber.i("Login result: An exception occurred")
                analytics.logEvent("registration_form", "success" to false, "students" to -1, "scrapperBaseUrl" to host, "error" to it.message.ifNullOrBlank { "No message" })
                loginErrorHandler.dispatch(it)
                view?.showContact(true)
            }))
    }

    fun onFaqClick() {
        view?.openFaqPage()
    }

    fun onEmailClick() {
        view?.openEmail()
    }

    fun onRecoverClick() {
        view?.onRecoverClick()
    }

    private fun validateCredentials(login: String, password: String, host: String, symbol: String): Boolean {
        var isCorrect = true

        if (login.isEmpty()) {
            view?.setErrorUsernameRequired()
            isCorrect = false
        }

        if (password.isEmpty()) {
            view?.setErrorPassRequired(focus = isCorrect)
            isCorrect = false
        }

        if (password.length < 6 && password.isNotEmpty()) {
            view?.setErrorPassInvalid(focus = isCorrect)
            isCorrect = false
        }

        if ("standard" !in host && symbol.isBlank()) {
            view?.setErrorSymbolRequired(focus = isCorrect)
            isCorrect = false
        }

        return isCorrect
    }
}
