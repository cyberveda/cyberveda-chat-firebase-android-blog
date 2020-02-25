package com.cyberveda.client.ui.main.chat.fragment


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.firebase.ui.auth.AuthUI

import com.cyberveda.client.R

import com.cyberveda.client.util.chat.FirestoreUtil
import com.cyberveda.client.util.chat.StorageUtil

import com.cyberveda.client.util.chat.image_annotation.GlideApp
import kotlinx.android.synthetic.main.fragment_my_chat_profile.*
import kotlinx.android.synthetic.main.fragment_my_chat_profile.view.*
import org.jetbrains.anko.support.v4.toast
import java.io.ByteArrayOutputStream


class MyChatProfileFragment : Fragment() {

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)


        val view = inflater.inflate(R.layout.fragment_my_chat_profile, container, false)

        view.apply {
            imageView_profile_picture.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(
                    Intent.createChooser(intent, "Select Image"),
                    RC_SELECT_IMAGE
                )
            }

            btn_save.setOnClickListener {
                if (::selectedImageBytes.isInitialized)
                    StorageUtil.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                        FirestoreUtil.updateCurrentUser(
                            editText_name.text.toString(),
                            editText_bio.text.toString(),

                            input_weight.text.toString(),

                            input_height.text.toString(),
                            input_marital_status.text.toString(),
                            input_education.text.toString(),
                            input_profession.text.toString(),
                            input_age.text.toString(),
                            input_gender.text.toString(),
                            input_eating_habits.text.toString(),
                            input_sleeping_habits.text.toString(),


                            imagePath
                        )
                    }
                else
                    FirestoreUtil.updateCurrentUser(
                        editText_name.text.toString(),
                        editText_bio.text.toString(),
                        input_weight.text.toString(),

                        input_height.text.toString(),
                        input_marital_status.text.toString(),
                        input_education.text.toString(),
                        input_profession.text.toString(),
                        input_age.text.toString(),
                        input_gender.text.toString(),
                        input_eating_habits.text.toString(),
                        input_sleeping_habits.text.toString(),


                        null
                    )
                toast("Saving")
            }

            btn_sign_out.setOnClickListener {
                AuthUI.getInstance()
                    .signOut(this@MyChatProfileFragment.context!!)
                    .addOnCompleteListener {
                        findNavController().navigate(R.id.action_myChatProfileFragment_to_signInFragment2)
//                            startActivity(intentFor<SignInActivity>().newTask().clearTask())
                    }
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media
                .getBitmap(activity?.contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            GlideApp.with(this@MyChatProfileFragment.context!!)
                .load(selectedImageBytes)
                .into(imageView_profile_picture) // MY_EDIT

            pictureJustChanged = true
        }
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser { user ->
            if (this@MyChatProfileFragment.isVisible) {
                editText_name.setText(user.name)
                editText_bio.setText(user.bio)

                input_weight.setText(user.weight)
                input_height.setText(user.height)
                input_marital_status.setText(user.maritalStatus)
                input_education.setText(user.education)
                input_profession.setText(user.profession)
                input_age.setText(user.age)
                input_gender.setText(user.gender)
                input_eating_habits.setText(user.eatingHabits)
                input_sleeping_habits.setText(user.sleepingHabits)



                if (!pictureJustChanged && user.profilePicturePath != null)
                    GlideApp.with(this)
                        .load(StorageUtil.pathToReference(user.profilePicturePath))
                        .placeholder(R.drawable.ic_account_circle_black_24dp)
                        .into(imageView_profile_picture)
            }
        }
    }

}
