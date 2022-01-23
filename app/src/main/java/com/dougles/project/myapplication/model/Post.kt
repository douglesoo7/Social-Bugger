package com.dougles.project.myapplication.model

class Post {
    var description: String? = null
    var postId: String? = null

    var postImage: String? = null
    var publisher: String? = null

    constructor() {}
    constructor(description: String?,postId: String?, postImage: String?, publisher: String?) {
        this.description = description
        this.postId = postId
        this.postImage = postImage
        this.publisher = publisher
    }
}