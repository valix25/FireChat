package com.walle.firechat.fragment


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.walle.firechat.AppConstants
import com.walle.firechat.ChatActivity

import com.walle.firechat.R
import com.walle.firechat.recyclerview.item.PersonItem
import com.walle.firechat.util.FirestoreUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_people.*
import org.jetbrains.anko.support.v4.startActivity
import com.mancj.materialsearchbar.MaterialSearchBar
import com.walle.firechat.model.User
import kotlinx.android.synthetic.main.fragment_people.view.*


class PeopleFragment : Fragment() {
    // Remember the firestore listener in order to remove it later
    private lateinit var userListenerRegistration: ListenerRegistration

    private var shouldInitRecyclerView = true

    private lateinit var peopleSection: Section

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        userListenerRegistration = FirestoreUtil.addUsersListener(this.activity!!, this::updateRecyclerView)

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_people, container, false)
        //TODO: optimize search by saving previous items and check if current filtered items are the same in which case
        // don't update
        view.searchBar.setOnSearchActionListener(object: MaterialSearchBar.OnSearchActionListener {
            override fun onButtonClicked(buttonCode: Int) {
                Log.i("search_query", "Pressed buttonCode $buttonCode")
                when (buttonCode) {
                    MaterialSearchBar.BUTTON_NAVIGATION -> {
                        Log.i("search_query", "Pressed BUTTON_NAVIGATION")
                    }
                    MaterialSearchBar.BUTTON_SPEECH -> {
                        Log.i("search_query", "Pressed BUTTON_SPEECH")
                    }
                    MaterialSearchBar.BUTTON_BACK -> {
                        Log.i("search_query", "Pressed BUTTON_BACK")
                        searchBar.disableSearch()
                        resetUserList()
                    }
                }
            }

            override fun onSearchStateChanged(enabled: Boolean) {
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                //Log.i("search_query", "searchConfirmed: $text")
                if(text.toString().isNotEmpty()) {
                    filterSearch(text.toString())
                }
            }

        })
        view.searchBar.addTextChangeListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) =
                //Log.i("search_query", "afterTextChanged: $p0")
                if(p0.toString().isEmpty()) {
                    resetUserList()
                } else {
                    filterSearch(p0.toString())
                }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        FirestoreUtil.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
    }

    private fun updateRecyclerView(items: List<Item>) {
        fun init() {
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@PeopleFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply{
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClick)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = peopleSection.update(items)

        if(shouldInitRecyclerView) {
            init()
        } else {
            updateItems()
        }
    }

    private fun filterSearch(keyword: String) {
        val firestoreInstance = FirebaseFirestore.getInstance()
        firestoreInstance.collection("users").get()
            .addOnSuccessListener {querySnapshot ->
                val items = mutableListOf<Item>()
                querySnapshot.documents.forEach {
                    if(it.id != FirebaseAuth.getInstance().currentUser?.uid) {
                        it.toObject(User::class.java)?.let {user ->
                            if (user.name.toLowerCase().contains(keyword.toLowerCase())) {
                                PersonItem(user, it.id, this.activity!!)
                            } else {
                                null
                            }
                        }?.let {personItem ->
                            items.add(personItem)
                        }
                    }
                }
                updateRecyclerView(items)
            }
    }

    private fun resetUserList() {
        val firestoreInstance = FirebaseFirestore.getInstance()
        firestoreInstance.collection("users").get()
            .addOnSuccessListener {querySnapshot ->
                val items = mutableListOf<Item>()
                querySnapshot.documents.forEach {
                    if(it.id != FirebaseAuth.getInstance().currentUser?.uid) {
                        it.toObject(User::class.java)?.let {user ->
                            PersonItem(user, it.id, this.activity!!)
                        }?.let {personItem ->
                            items.add(personItem)
                        }
                    }
                }
                updateRecyclerView(items)
            }
    }

    private val onItemClick = OnItemClickListener{ item, _ ->
        if(item is PersonItem) {
            startActivity<ChatActivity>(
                AppConstants.USER_NAME to item.user.name,
                AppConstants.USER_ID to item.userId
            )
        }
    }
}
