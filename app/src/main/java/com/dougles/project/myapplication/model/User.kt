package com.dougles.project.myapplication.model

class User {
    private var userName: String = ""
    private var fullName: String = ""
    private var bio: String = ""
    private var image: String = ""
    private var uid: String = ""
    private var email: String = ""

    constructor()

    constructor(
        userName: String,
        fullName: String,
        bio: String,
        image: String,
        uid: String,
        email: String
    ) {
        this.userName = userName
        this.fullName = fullName
        this.bio = bio
        this.image = image
        this.uid = uid
        this.email = email
    }

    fun setUserName(userName: String) {
        this.userName = userName
    }

    fun getUserName(): String {
        return userName
    }

    fun setFullName(fullName: String) {
        this.fullName = fullName
    }

    fun getFullName(): String {
        return fullName
    }

    fun setImage(image: String) {
        this.image = image
    }

    fun getImage(): String {
        return image
    }

    fun setBio(bio: String) {
        this.bio = bio
    }

    fun getBio(): String {
        return bio
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getEmail(): String {
        return email
    }

    fun setUid(uid: String) {
        this.uid = uid
    }

    fun getUid(): String {
        return uid
    }
}