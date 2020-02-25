package com.cyberveda.client.ui.main.chat.fragment


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cyberveda.client.R
import com.cyberveda.client.service.MyFirebaseInstanceIDService
import com.cyberveda.client.util.chat.FirestoreUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_sign_in.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.indeterminateProgressDialog


class SignInFragment : Fragment() {

    private val TAG = "lgx_SignInFragment"

    private val RC_SIGN_IN = 1

    private val signInProviders =
        listOf(
            AuthUI.IdpConfig.EmailBuilder()
                .setAllowNewAccounts(true)
                .setRequireName(true)
                .build()
        )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Sign In to Chat"
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: 29: ")
//        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
//
//        (activity as AppCompatActivity).supportActionBar?.title = "Example 1"

        checkIfFirebaseUserIsLoggedIn()

        account_sign_in.setOnClickListener {
            login()
        }
    }

    private fun checkIfFirebaseUserIsLoggedIn() {
        if (FirebaseAuth.getInstance().currentUser != null){
            findNavController().navigate(R.id.action_signInFragment2_to_peopleFragment)
        }

    }


    fun login() {

        val intent = AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(signInProviders)
            .setLogo(R.drawable.ic_check_green_24dp)
            .build()
        startActivityForResult(intent, RC_SIGN_IN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val progressDialog = indeterminateProgressDialog("Setting up your account")
                FirestoreUtil.initCurrentUserIfFirstTime {

                    // newTask() and clearTask() flags make sure that we cannot go back to signInActivity after it is finished. This is important as we do not want already signed in user to sign in again.

//                    startActivity(intentFor<MainActivity>().newTask().clearTask())
                    navToPeopleFragment()
                    val registrationToken = FirebaseInstanceId.getInstance().token
                    MyFirebaseInstanceIDService.addTokenToFirestore(registrationToken)

                    progressDialog.dismiss()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response == null) return

                when (response.error?.errorCode) {
                    ErrorCodes.NO_NETWORK ->
                        constraint_layout.longSnackbar("No network")
                    ErrorCodes.UNKNOWN_ERROR ->
                        constraint_layout.longSnackbar("Unknown error")
                }
            }
        }
    }


    fun navToPeopleFragment(){
        findNavController().navigate(R.id.action_signInFragment2_to_peopleFragment)

    }

}
