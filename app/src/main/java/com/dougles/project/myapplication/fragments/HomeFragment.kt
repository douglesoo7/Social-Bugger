package com.dougles.project.myapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dougles.project.myapplication.R
import com.dougles.project.myapplication.adapters.PostAdapter
import com.dougles.project.myapplication.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private lateinit var postAdapter: PostAdapter
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        postList = ArrayList()
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        view.recyclerViewHome.layoutManager = linearLayoutManager
        postAdapter = PostAdapter(requireContext(), postList as ArrayList)
        view.recyclerViewHome.adapter = postAdapter

        checkFollowings()

        return view
    }

    private fun checkFollowings() {
        followingList = ArrayList()
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    (followingList as ArrayList<String>).clear()

                    for (snapshot in dataSnapshot.children) {
                        snapshot.key?.let {
                            (followingList as ArrayList<String>).add(it)
                        }
                    }

                    retrievePosts()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun retrievePosts() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)

                    for (id in followingList as ArrayList<String>) {
                        if (post!!.publisher == id) {
                            postList!!.add(post)
                        }
                    }
                    postAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


}