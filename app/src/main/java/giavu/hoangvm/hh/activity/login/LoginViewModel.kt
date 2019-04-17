package giavu.hoangvm.hh.activity.login

import android.annotation.SuppressLint
import androidx.lifecycle.*
import giavu.hoangvm.hh.api.UserApi
import giavu.hoangvm.hh.model.LoginBody
import giavu.hoangvm.hh.model.LoginResponse
import giavu.hoangvm.hh.model.User
import giavu.hoangvm.hh.utils.Status
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * @Author: Hoang Vu
 * @Date:   2018/12/15
 */
class LoginViewModel(private val userApi: UserApi) : ViewModel() {

    private val _status: MutableLiveData<Status<LoginResponse>> = MutableLiveData()
    private val _showProgressRequest: MutableLiveData<Unit> = MutableLiveData()
    private val _hideProgressRequest: MutableLiveData<Unit> = MutableLiveData()
    private val _registerEvent: MutableLiveData<Unit> = MutableLiveData()

    val status: LiveData<Status<LoginResponse>>
        get() = _status

    val showProgressRequest: LiveData<Unit>
        get() = _showProgressRequest

    val hideProgressRequest: LiveData<Unit>
        get() = _hideProgressRequest

    val registerEvent: LiveData<Unit>
        get() = _registerEvent

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val loginBtnEnable: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {

        val observer: Observer<String> = Observer {
            this.value = username.value.orEmpty().isNotEmpty()
                    && password.value.orEmpty().isNotEmpty()
        }
        addSource(username, observer)
        addSource(password, observer)
    }

    @SuppressLint("CheckResult")
    fun login() {
        val loginBody = LoginBody(
            email = username.value.toString(),
            password = password.value.toString()
        )
        val body = User(
            user = loginBody
        )
        userApi.login(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _showProgressRequest.value = Unit }
            .doFinally { _hideProgressRequest.value = Unit }
            .subscribeBy(
                onSuccess = { response ->
                    _status.value = Status.Success(response)
                },
                onError = { error ->
                    _status.value = Status.Failure(error)
                }
            )
    }

    fun register() {
        _registerEvent.value = Unit
    }
}
