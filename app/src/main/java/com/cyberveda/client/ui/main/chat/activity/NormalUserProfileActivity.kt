package com.cyberveda.client.ui.main.chat.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cyberveda.client.R
import com.cyberveda.client.util.chat.AppConstants
import com.cyberveda.client.util.chat.StorageUtil
import com.cyberveda.client.util.chat.image_annotation.GlideApp
import kotlinx.android.synthetic.main.activity_normal_user_profile.*
import org.jetbrains.anko.toast

class NormalUserProfileActivity : AppCompatActivity() {

    private lateinit var otherUserId: String
    private lateinit var otherUserName: String
    private lateinit var otherUserBio: String
    private lateinit var otherUserWeight: String
    private lateinit var otherUserHeight: String
    private lateinit var otherUserMaritalStatus: String
    private lateinit var otherUserEducation: String
    private lateinit var otherUserProfession: String
    private lateinit var otherUserAge: String
    private lateinit var otherUserGender: String
    private lateinit var otherUserEatingHabits: String
    private lateinit var otherUserSleepingHabits: String

    private var otherUserProfilePicturePath: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_user_profile)



        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)



        btn_save.setOnClickListener {
            toast("clicked for fun save")
        }

        btn_sign_out.setOnClickListener {
            toast("clieck sign out")
        }






        otherUserId = intent.getStringExtra(AppConstants.USER_ID)
        otherUserName = intent.getStringExtra(AppConstants.USER_NAME)
        otherUserBio = intent.getStringExtra(AppConstants.USER_BIO)
        otherUserWeight = intent.getStringExtra(AppConstants.USER_WEIGHT)
        otherUserHeight = intent.getStringExtra(AppConstants.USER_HEIGHT)
        otherUserMaritalStatus = intent.getStringExtra(AppConstants.USER_MARITAL_STATUS)
        otherUserEducation = intent.getStringExtra(AppConstants.USER_EDUCATION)
        otherUserProfession = intent.getStringExtra(AppConstants.USER_PROFESSION)
        otherUserAge = intent.getStringExtra(AppConstants.USER_AGE)
        otherUserGender = intent.getStringExtra(AppConstants.USER_GENDER)
        otherUserEatingHabits = intent.getStringExtra(AppConstants.USER_EATING_HABITS)
        otherUserSleepingHabits = intent.getStringExtra(AppConstants.USER_SLEEPING_HABITS)



        otherUserProfilePicturePath = intent.getStringExtra(AppConstants.USER_PROFILE_PICTURE_PATH)

    }


    override fun onStart() {
        super.onStart()

        editText_bio_other.setText(otherUserBio)
        editText_name_other.setText(otherUserName)
        input_weight_other.setText(otherUserWeight)
        input_height_other.setText(otherUserHeight)
        input_marital_status_other.setText(otherUserMaritalStatus)
        input_education_other.setText(otherUserEducation)
        input_profession_other.setText(otherUserProfession)
        input_age_other.setText(otherUserAge)
        input_gender_other.setText(otherUserGender)
        input_eating_habits_other.setText(otherUserEatingHabits)
        input_sleeping_habits_other.setText(otherUserSleepingHabits)










        if (otherUserProfilePicturePath != null)
            GlideApp.with(this)
                .load(StorageUtil.pathToReference(otherUserProfilePicturePath!!))
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .into(imageView_profile_picture_other)

    }
}
