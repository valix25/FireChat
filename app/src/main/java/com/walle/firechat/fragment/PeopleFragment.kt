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
import android.view.Gravity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import kotlinx.android.synthetic.main.fragment_people.view.*
import org.jetbrains.anko.support.v4.longToast


class PeopleFragment : Fragment() {
    // Remember the firestore listener in order to remove it later
    private lateinit var userListenerRegistration: ListenerRegistration

    private var shouldInitRecyclerView = true

    private lateinit var peopleSection: Section

    private val lastSearches: List<String>? = null
    private val drawer: DrawerLayout? = null

    private lateinit var searchBarObj: android.widget.SearchView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        userListenerRegistration = FirestoreUtil.addUsersListener(this.activity!!, this::updateRecyclerView)

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_people, container, false)
        view.searchBar.setOnSearchActionListener(object: MaterialSearchBar.OnSearchActionListener {
            override fun onButtonClicked(buttonCode: Int) {
                when (buttonCode) {
                    MaterialSearchBar.BUTTON_NAVIGATION -> {
                    }
                    MaterialSearchBar.BUTTON_SPEECH -> {
                    }
                    MaterialSearchBar.BUTTON_BACK -> searchBar.disableSearch()
                }
            }

            override fun onSearchStateChanged(enabled: Boolean) {
                Log.i("search_query", "searchStateChanged: $enabled")
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                Log.i("search_query", "searchConfirmed: $text")
            }

        })
        view.searchBar.addTextChangeListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.i("search_query", "afterTextChanged: $p0")
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i("search_query", "onTextChanged: $p0")
            }

        })
//        view.searchBar.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(p0: String?): Boolean {
//                print("Text submitted: $p0")
//                Log.i("query", " text submitted: $p0")
//                return false
//            }
//
//            override fun onQueryTextChange(p0: String?): Boolean {
//                print("Text changed: $p0")
//                return false
//            }
//        })
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

    private val onItemClick = OnItemClickListener{ item, _ ->
        if(item is PersonItem) {
            startActivity<ChatActivity>(
                AppConstants.USER_NAME to item.user.name,
                AppConstants.USER_ID to item.userId
            )
        }
    }
}
