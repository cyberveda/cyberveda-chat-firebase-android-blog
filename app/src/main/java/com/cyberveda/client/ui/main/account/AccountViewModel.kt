package com.cyberveda.client.ui.main.account

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.cyberveda.client.models.AccountProperties
import com.cyberveda.client.repository.main.AccountRepository
import com.cyberveda.client.session.SessionManager
import com.cyberveda.client.ui.BaseViewModel
import com.cyberveda.client.ui.DataState
import com.cyberveda.client.ui.Loading
import com.cyberveda.client.ui.main.account.state.AccountStateEvent
import com.cyberveda.client.ui.main.account.state.AccountStateEvent.*
import com.cyberveda.client.ui.main.account.state.AccountViewState
import com.cyberveda.client.util.AbsentLiveData
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
) : BaseViewModel<AccountStateEvent, AccountViewState>() {
    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when (stateEvent) {

            is GetAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.getAccountProperties(authToken)
                } ?: AbsentLiveData.create()
            }

            is UpdateAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    authToken.account_pk?.let { pk ->
                        val newAccountProperties = AccountProperties(
                            pk,
                            stateEvent.email,
                            stateEvent.username
                        )
                        accountRepository.saveAccountProperties(
                            authToken,
                            newAccountProperties
                        )
                    }
                } ?: AbsentLiveData.create()
            }

            is ChangePasswordEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.updatePassword(
                        authToken,
                        stateEvent.currentPassword,
                        stateEvent.newPassword,
                        stateEvent.confirmNewPassword
                    )
                } ?: AbsentLiveData.create()
            }

            is None -> {
                return liveData {
                    emit(
                        DataState(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties) {
        val update = getCurrentViewStateOrNew()
        if (update.accountProperties == accountProperties) {
            return
        }
        update.accountProperties = accountProperties
        setViewState(update)
    }

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    fun logout() {
        sessionManager.logout()
    }

    fun cancelActiveJobs() {
        accountRepository.cancelActiveJobs() // cancel active jobs
        handlePendingData() // hide progress bar
    }

    fun handlePendingData() {
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

//    fun allowFeedbackPost() {
//
//        if (sessionManager.cachedToken.value?.email == "guest@gmail.com"){
//            Log.d(TAG, "allowFeedbackPost: 109: guest user ${sessionManager.cachedToken.value?.email}")
//        }else{
//            Log.d(TAG, "allowFeedbackPost: 111: normal user ${sessionManager.cachedToken.value?.email}")
//        }
//    }
}














