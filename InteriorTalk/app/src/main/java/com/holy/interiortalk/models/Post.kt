package com.holy.interiortalk.models

const val POST_TAG_LIVING_ROOM = "living room"
const val POST_TAG_BEDROOM = "bedroom"
const val POST_TAG_KITCHEN = "kitchen"
const val POST_TAG_BATHROOM = "bathroom"

data class Post(
    var id: String? = null,
    var writer: String? = null,
    var time: String? = null,
    var tag: String? = POST_TAG_LIVING_ROOM,
    var title: String? = null,
    var description: String? = null,
    var furnitureLabels: MutableList<FurnitureLabel> = mutableListOf(),
    var likes: Int = 0,
    var likedUsers: MutableList<String> = mutableListOf()
)