package giavu.hoangvm.japanfood.activity.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import giavu.hoangvm.japanfood.api.UserApi
import giavu.hoangvm.japanfood.model.LoginBody
import giavu.hoangvm.japanfood.model.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject

/**
 * @Author: Hoang Vu
 * @Date:   2018/12/15
 */
class MainViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var navigator: LoginNavigator

    private val userApi: UserApi by application.inject()
    private val _username = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()



    fun apply(navigator: LoginNavigator){
        this.navigator = navigator
    }

    fun onUsernameTextChanged(text: CharSequence){
        Log.d("Hoang", text.toString())
        _username.postValue(text.toString())
    }

    fun onPasswordTextChanged(text: CharSequence){
        Log.d("Hoang", text.toString())
        _password.postValue(text.toString())
    }

    fun login(){
        val loginBody = LoginBody(
                email = _username.value.toString(),
                password = _password.value.toString()
        )
        val body = User(
                user = loginBody
        )
        Log.d("Why", loginBody.email + "-" + loginBody.password)
        userApi.login(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { response ->
                            if(response != null) {
                                navigator.toLogin(response)
                            }else{
                                Log.d("Test Retrofit", response.toString())
                            }

                        },
                        onError = {Log.d("Test Retrofit", it.toString())}
                )
    }

}