package com.dougles.project.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.dougles.project.myapplication.R
import com.dougles.project.myapplication.fragments.ProfileFragment
import com.dougles.project.myapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_settings.view.*
import kotlinx.android.synthetic.main.user_item_layout.view.*

class UserAdapter(
    private val mContext: Context,
    private val mUsers: List<User>,
    private val isFragment: Boolean = false
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = mUsers[position]

        holder.itemView.setOnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", user.getUid())
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment()).commit()
        }
        holder.setData(user)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        fun setData(user: User) {
            itemView.apply {
                Picasso.get().load(user.getImage())
                    .placeholder(R.drawable.profile)
                    .into(ivProfileSearch)

                tvUsernameSearch.text = user.getUserName()
                tvFullNameSearch.text = user.getFullName()

                checkFollowingStatus(user.getUid(), btn_follow_search)


                btn_follow_search.setOnClickListener {
                    if (btn_follow_search.text.toString() == "Follow") {
                        firebaseUser?.uid.let { it1 ->
                            FirebaseDatabase.getInstance().reference
                                .child("Follow").child(it1.toString())
                                .child("Following").child(user.getUid())
                                .setValue(true).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        firebaseUser?.uid.let { it1 ->
                                            FirebaseDatabase.getInstance().reference
                                                .child("Follow").child(user.getUid())
                                                .child("Followers").child(it1.toString())
                                                .setValue(true).addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {

                                                    } else {

                                                    }

                                                }
                                        }

                                    }
                                }
                        }
                    } else {
                        firebaseUser?.uid.let { it1 ->
                            FirebaseDatabase.getInstance().reference
                                .child("Follow").child(it1.toString())
                                .child("Following").child(user.getUid())
                                .removeValue().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        firebaseUser?.uid.let { it1 ->
                                            FirebaseDatabase.getInstance().reference
                                                .child("Follow").child(user.getUid())
                                                .child("Followers").child(it1.toString())
                                                .removeValue().addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {

                                                    } else {

                                                    }

                                                }
                                        }

                                    }
                                }
                        }
                    }
                }
            }
        }

        private fun checkFollowingStatus(uid: String, followingButton: Button) {
            val followingRef = firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(it1.toString())
                    .child("Following")
            }

            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.child(uid).exists()) {
                        followingButton.text = "Following"
                    } else {
                        followingButton.text = "Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
    }
}
