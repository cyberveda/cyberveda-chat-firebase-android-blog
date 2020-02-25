package com.cyberveda.client.ui.main.chat.fragment


import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyberveda.client.ChatActivity
import com.cyberveda.client.R
import com.cyberveda.client.ui.main.chat.recycler_item.PersonItem
import com.cyberveda.client.util.TopSpacingItemDecoration
import com.cyberveda.client.util.chat.AppConstants
import com.cyberveda.client.util.chat.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_people.*


class PeopleFragment : Fragment() {
    private val TAG = "lgx_PeopleFragment"
    var mLastClickTime: Long = 0

    // This is for remembering the Firestore Listener when we might wanna remove it later.

    // Whenever someone creates a new account, user will be added to list of all people and we always want to have up to date list of people. that is why we use a listener instead of simple getter.

    private lateinit var userListenerRegistration: ListenerRegistration

    private var shouldInitRecyclerView = true

    private lateinit var peopleSection: Section

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.firebase_account_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.firebase_account_menu_id -> {
                findNavController().navigate(R.id.action_peopleFragment_to_myChatProfileFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)





        userListenerRegistration =
            FirestoreUtil.addUsersListener(this.activity!!, this::updateRecyclerView)

        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
    }

    private fun updateRecyclerView(items: List<Item>) {

        fun init() {
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@PeopleFragment.context)
                val topSpacingDecorator = TopSpacingItemDecoration(2)
                removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
                addItemDecoration(topSpacingDecorator)
                if (FirebaseAuth.getInstance().currentUser?.uid == "oR7wbArR5qe49dHXIETAxCQV0QF2") {
                    adapter = GroupAdapter<ViewHolder>().apply {
                        peopleSection = Section(items)
                        add(peopleSection)
                        setOnItemClickListener(onItemClick)
                    }
                } else {
                    adapter = GroupAdapter<ViewHolder>().apply {
                        peopleSection = Section(items)
                        add(peopleSection)
                        setOnItemClickListener(onItemClick)
                    }
                }

            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = peopleSection.update(items)

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()

    }

    //    private val onItemClick = OnItemClickListener { item, view ->
//        if (item is PersonItem) {
//
//            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                return@OnItemClickListener
//            }
//            mLastClickTime = SystemClock.elapsedRealtime()
    // Handle button clicks
//
//
//
//            val intent = Intent(this@PeopleFragment.context, ChatActivity::class.java)
//            intent.putExtra(AppConstants.USER_NAME, item.person.name)
//            intent.putExtra(AppConstants.USER_ID, item.userId)
//
//            startActivity(intent)
//            activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//
////            startActivity<ChatActivity>(
////                AppConstants.USER_NAME to item.person.name,
////                AppConstants.USER_ID to item.userId
////            )
//
//        }
//    }
//}
//
//}
    private val onItemClick = OnItemClickListener { item, view ->
        if (item is PersonItem) {

            if (SystemClock.elapsedRealtime() - mLastClickTime < 400) {
                Log.d(TAG, ": 155: ${SystemClock.elapsedRealtime() - mLastClickTime < 400} ")
                return@OnItemClickListener
            }
            Log.d(TAG, ": 159: ${SystemClock.elapsedRealtime() - mLastClickTime < 400}")

            Log.d(TAG, ": 161: ${SystemClock.elapsedRealtime()}")
            Log.d(TAG, ": 162:  ${SystemClock.elapsedRealtime() - mLastClickTime}")
            Log.d(TAG, ": 163: ${mLastClickTime}")
            Log.d(TAG, ": 164: ${mLastClickTime < 400}")
            mLastClickTime = SystemClock.elapsedRealtime()

            val intent = Intent(this@PeopleFragment.context, ChatActivity::class.java)
            intent.putExtra(AppConstants.USER_NAME, item.person.name)
            intent.putExtra(AppConstants.USER_ID, item.userId)
            intent.putExtra(AppConstants.USER_BIO, item.person.bio)


            if (item.person.weight != null || item.person.height != null || item.person.maritalStatus != null || item.person.education != null || item.person.profession != null || item.person.age != null || item.person.gender != null || item.person.eatingHabits != null || item.person.sleepingHabits != null) {


                intent.putExtra(AppConstants.USER_WEIGHT, item.person.weight)

                intent.putExtra(AppConstants.USER_HEIGHT, item.person.height)

                intent.putExtra(AppConstants.USER_MARITAL_STATUS, item.person.maritalStatus)

                intent.putExtra(AppConstants.USER_EDUCATION, item.person.education)
                intent.putExtra(AppConstants.USER_PROFESSION, item.person.profession)
                intent.putExtra(AppConstants.USER_AGE, item.person.age)
                intent.putExtra(AppConstants.USER_GENDER, item.person.gender)
                intent.putExtra(AppConstants.USER_EATING_HABITS, item.person.eatingHabits)
                intent.putExtra(AppConstants.USER_SLEEPING_HABITS, item.person.sleepingHabits)

            }




            intent.putExtra(AppConstants.USER_PROFILE_PICTURE_PATH, item.person.profilePicturePath)

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

//            startActivity<ChatActivity>(
//                AppConstants.USER_NAME to item.person.name,
//                AppConstants.USER_ID to item.userId
//            )

        }
    }

}