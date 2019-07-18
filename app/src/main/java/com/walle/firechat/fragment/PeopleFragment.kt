package com.walle.firechat.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ListenerRegistration

import com.walle.firechat.R
import com.walle.firechat.util.FirestoreUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_people.*


class PeopleFragment : Fragment() {

    // Remember the firestore listener in order to remove it later
    private lateinit var userListenerRegistration: ListenerRegistration

    private var shouldInitRecyclerView = true

    private lateinit var peopleSection: Section

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        userListenerRegistration = FirestoreUtil.addUsersListener(this.activity!!, this::updateRecyclerView)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_people, container, false)
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
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() {

        }

        if(shouldInitRecyclerView) {
            init()
        } else {
            updateItems()
        }
    }
}
