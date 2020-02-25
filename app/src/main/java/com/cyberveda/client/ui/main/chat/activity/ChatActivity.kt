package com.cyberveda.client

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration

import com.cyberveda.client.models.chat.ImageMessage
import com.cyberveda.client.models.chat.TextMessage
import com.cyberveda.client.models.chat.User
import com.cyberveda.client.ui.main.chat.activity.NormalUserProfileActivity

import com.cyberveda.client.util.chat.AppConstants
import com.cyberveda.client.util.chat.FirestoreUtil
import com.cyberveda.client.util.chat.StorageUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.util.*

private const val RC_SELECT_IMAGE = 2

class ChatActivity : AppCompatActivity() {
    private val TAG = "lgx_ChatActivity"
    private lateinit var currentChannelId: String
    private lateinit var currentUser: User

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


    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setSupportActionBar(tool_bar_chat)


        Log.d(TAG, "onCreate: 59: ${intent.getStringExtra(AppConstants.USER_NAME)}")

        FirestoreUtil.getCurrentUser {
            currentUser = it
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

        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = otherUserName


        FirestoreUtil.getOrCreateChatChannel(otherUserId) { channelId ->
            currentChannelId = channelId

            messagesListenerRegistration =
                FirestoreUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)

            imageView_send.setOnClickListener {
                val messageToSend =
                    TextMessage(
                        editText_message.text.toString(), Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        otherUserId, currentUser.name
                    )
                editText_message.setText("")
                FirestoreUtil.sendMessage(messageToSend, channelId)
            }

            fab_send_image.setOnClickListener {
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
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.normal_user_profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_user_profile_menu_id -> {
                Log.d(TAG, "onOptionsItemSelected: 102: ${otherUserId}")
                toast("Cool!")
                val intent = Intent(this, NormalUserProfileActivity::class.java)
                intent.putExtra(
                    AppConstants.USER_NAME,
                    otherUserName
                )
                intent.putExtra(
                    AppConstants.USER_ID,
                    otherUserId
                )
                intent.putExtra(
                    AppConstants.USER_BIO,
                    otherUserBio
                )
                intent.putExtra(
                    AppConstants.USER_WEIGHT,
                    otherUserWeight
                )
                intent.putExtra(
                    AppConstants.USER_HEIGHT,
                    otherUserHeight
                )
                intent.putExtra(
                    AppConstants.USER_MARITAL_STATUS,
                    otherUserMaritalStatus
                )
                intent.putExtra(
                    AppConstants.USER_EDUCATION,
                    otherUserEducation
                )
                intent.putExtra(
                    AppConstants.USER_PROFESSION,
                    otherUserProfession
                )
                intent.putExtra(
                    AppConstants.USER_AGE,
                    otherUserAge
                )
                intent.putExtra(
                    AppConstants.USER_GENDER,
                    otherUserGender
                )
                intent.putExtra(
                    AppConstants.USER_EATING_HABITS,
                    otherUserEatingHabits
                )
                intent.putExtra(
                    AppConstants.USER_SLEEPING_HABITS,
                    otherUserSleepingHabits
                )


                intent.putExtra(
                    AppConstants.USER_PROFILE_PICTURE_PATH,
                    otherUserProfilePicturePath
                )

                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("MissingSuperCall") //MY_EDIT
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            val selectedImagePath = data.data

            val selectedImageBmp =
                MediaStore.Images.Media.getBitmap(contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()

            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val selectedImageBytes = outputStream.toByteArray()

            StorageUtil.uploadMessageImage(selectedImageBytes) { imagePath ->
                val messageToSend =
                    ImageMessage(
                        imagePath, Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        otherUserId, currentUser.name
                    )
                FirestoreUtil.sendMessage(messageToSend, currentChannelId)
            }
        }
    }

    private fun updateRecyclerView(messages: List<Item>) {
        fun init() {
            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(messages)

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()

        recycler_view_messages.scrollToPosition(recycler_view_messages.adapter!!.itemCount - 1)
    }


}
