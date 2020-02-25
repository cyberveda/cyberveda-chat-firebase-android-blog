//package com.cyberveda.client.ui.main.chat.fragment
//
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.content.Intent
//import android.graphics.Bitmap
//import android.os.Bundle
//import android.provider.MediaStore
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.cyberveda.client.R
//import com.cyberveda.client.models.chat.ImageMessage
//import com.cyberveda.client.models.chat.TextMessage
//import com.cyberveda.client.models.chat.User
//import com.cyberveda.client.util.chat.AppConstants
//import com.cyberveda.client.util.chat.FirestoreUtil
//import com.cyberveda.client.util.chat.StorageUtil
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.ListenerRegistration
//import com.xwray.groupie.GroupAdapter
//import com.xwray.groupie.Section
//import com.xwray.groupie.kotlinandroidextensions.Item
//import com.xwray.groupie.kotlinandroidextensions.ViewHolder
//import kotlinx.android.synthetic.main.fragment_chat.*
//import java.io.ByteArrayOutputStream
//import java.util.*
//
//private const val RC_SELECT_IMAGE = 2
//private val TAG = "lgx_ChatFragment"
//
//
//class ChatFragment : Fragment() {
//
//    lateinit var usernamebundle: String
//    lateinit var useridbundle: String
//
//    lateinit var recyclerView: RecyclerView
//
//
//    private lateinit var currentChannelId: String
//    private lateinit var currentUser: User
//    private lateinit var otherUserId: String
//
//    private lateinit var messagesListenerRegistration: ListenerRegistration
//    private var shouldInitRecyclerView = true
//    private lateinit var messagesSection: Section
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        usernamebundle = arguments!!.getString("usernamebundle")
//        useridbundle = arguments!!.getString("useridbundle")
//        Log.d(TAG, "onCreate: 51: $usernamebundle")
//    }
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        activity?.actionBar?.apply {
//            setDisplayHomeAsUpEnabled(true)
//            setTitle(usernamebundle)
//
//        }
//
////        activity?.actionBar?.setTitle("YOUR TITLE")
////
////        supportActionBar?.setDisplayHomeAsUpEnabled(true)
////        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME)
//
//        FirestoreUtil.getCurrentUser {
//            currentUser = it
//        }
//
//        otherUserId = useridbundle
//
//        FirestoreUtil.getOrCreateChatChannel(otherUserId) { channelId ->
//            currentChannelId = channelId
//
//            messagesListenerRegistration =
//                FirestoreUtil.addChatMessagesListener(
//                    channelId,
//                    this@ChatFragment.context!!,
//                    this::updateRecyclerView
//                )
//
//            imageView_send.setOnClickListener {
//                val messageToSend =
//                    TextMessage(
//                        editText_message.text.toString(), Calendar.getInstance().time,
//                        FirebaseAuth.getInstance().currentUser!!.uid,
//                        otherUserId, currentUser.name
//                    )
//                editText_message.setText("")
//                FirestoreUtil.sendMessage(messageToSend, channelId)
//            }
//
//            fab_send_image.setOnClickListener {
//                val intent = Intent().apply {
//                    type = "image/*"
//                    action = Intent.ACTION_GET_CONTENT
//                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
//                }
//                startActivityForResult(
//                    Intent.createChooser(intent, "Select Image"),
//                    RC_SELECT_IMAGE
//                )
//            }
//        }
//
//        return inflater.inflate(R.layout.fragment_chat, container, false)
//    }
//
//    @SuppressLint("MissingSuperCall") //MY_EDIT
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
//            data != null && data.data != null
//        ) {
//            val selectedImagePath = data.data
//
//            val selectedImageBmp =
//                MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImagePath)
//
//            val outputStream = ByteArrayOutputStream()
//
//            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
//            val selectedImageBytes = outputStream.toByteArray()
//
//            StorageUtil.uploadMessageImage(selectedImageBytes) { imagePath ->
//                val messageToSend =
//                    ImageMessage(
//                        imagePath, Calendar.getInstance().time,
//                        FirebaseAuth.getInstance().currentUser!!.uid,
//                        otherUserId, currentUser.name
//                    )
//                FirestoreUtil.sendMessage(messageToSend, currentChannelId)
//            }
//        }
//    }
//
//    private fun updateRecyclerView(messages: List<Item>) {
//        fun init() {
//            recyclerView = view?.findViewById(R.id.recycler_view_messages) as RecyclerView
//
//            recyclerView.apply {
//                layoutManager = LinearLayoutManager(this@ChatFragment.context!!)
//                adapter = GroupAdapter<ViewHolder>().apply {
//                    messagesSection = Section(messages)
//                    this.add(messagesSection)
//                }
//            }
//            shouldInitRecyclerView = false
//        }
//
//        fun updateItems() = messagesSection.update(messages)
//
//        if (shouldInitRecyclerView){
//            init()
//
//        }
//        else{
//            updateItems()
//        }
//
//        recyclerView.scrollToPosition(recyclerView.adapter!!.itemCount - 1)
//    }
//}
