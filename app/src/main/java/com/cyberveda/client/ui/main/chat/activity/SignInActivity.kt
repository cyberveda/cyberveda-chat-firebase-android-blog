//package com.cyberveda.client.ui.main.chat.activity
//
//import android.app.Activity
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import com.cyberveda.client.R
//import com.firebase.ui.auth.AuthUI
//import com.firebase.ui.auth.ErrorCodes
//import com.firebase.ui.auth.IdpResponse
//import com.google.firebase.iid.FirebaseInstanceId
//import com.cyberveda.client.service.MyFirebaseInstanceIDService
//import com.cyberveda.client.util.chat.FirestoreUtil
//import kotlinx.android.synthetic.main.activity_sign_in.*
//import org.jetbrains.anko.clearTask
//import org.jetbrains.anko.design.longSnackbar
//import org.jetbrains.anko.indeterminateProgressDialog
//import org.jetbrains.anko.intentFor
//import org.jetbrains.anko.newTask
//
//class SignInActivity : AppCompatActivity() {
//
//    private val RC_SIGN_IN = 1
//
//    private val signInProviders =
//            listOf(AuthUI.IdpConfig.EmailBuilder()
//                    .setAllowNewAccounts(true)
//                    .setRequireName(true)
//                    .build())
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_in)
//
//        account_sign_in.setOnClickListener {
//            val intent = AuthUI.getInstance().createSignInIntentBuilder()
//                    .setAvailableProviders(signInProviders)
//                    .setLogo(R.drawable.ic_fire_emoji)
//                    .build()
//            startActivityForResult(intent, RC_SIGN_IN)
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == RC_SIGN_IN) {
//            val response = IdpResponse.fromResultIntent(data)
//
//            if (resultCode == Activity.RESULT_OK) {
//                val progressDialog = indeterminateProgressDialog("Setting up your account")
//                FirestoreUtil.initCurrentUserIfFirstTime {
//
//                    // newTask() and clearTask() flags make sure that we cannot go back to signInActivity after it is finished. This is important as we do not want already signed in user to sign in again.
//
////                    startActivity(intentFor<MainActivity>().newTask().clearTask())
//
//                    val registrationToken = FirebaseInstanceId.getInstance().token
//                    MyFirebaseInstanceIDService.addTokenToFirestore(registrationToken)
//
//                    progressDialog.dismiss()
//                }
//            }
//            else if (resultCode == Activity.RESULT_CANCELED) {
//                if (response == null) return
//
//                when (response.error?.errorCode) {
//                    ErrorCodes.NO_NETWORK ->
//                        constraint_layout.longSnackbar("No network")
//                    ErrorCodes.UNKNOWN_ERROR ->
//                        constraint_layout.longSnackbar("Unknown error")
//                }
//            }
//        }
//    }
//}
