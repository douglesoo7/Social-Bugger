package com.dougles.project.myapplication.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.dougles.project.myapplication.R
import com.dougles.project.myapplication.adapters.UserAdapter
import com.dougles.project.myapplication.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : Fragment() {

    lateinit var userAdapter: UserAdapter
    private var mUserList: MutableList<User>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_search, container, false)

        mUserList = ArrayList()

        view.recyclerViewSearch.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            userAdapter = UserAdapter(context, mUserList as ArrayList<User>, true)
            adapter = userAdapter
        }


        retrieveUsers()

        view.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view.etSearch.text.toString() == "") {

                } else {

                    view.recyclerViewSearch.visibility = View.VISIBLE

                    retrieveUsers()
                    searchUser(p0.toString())

                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })


        return view
    }

    private fun retrieveUsers() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (view?.etSearch.toString() == "") {

                    mUserList?.clear()

                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null)
                            mUserList?.add(user)
                    }

                    userAdapter.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun searchUser(searchText: String) {

        val query = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("userName")
            .startAt(searchText)
            .endAt(searchText + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {


            override fun onDataChange(dataSnapshot: DataSnapshot) {

                mUserList?.clear()

                for (snapshot in dataSnapshot.children) {

                    val user = snapshot.getValue(User::class.java)
                    if (user != null) mUserList?.add(user)

                }
                userAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}