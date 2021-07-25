package com.duclong5512.myapplication.model

data class SearchResult(
    var title: String,
    var description: String,
    var imageUrl: String? = null,
    var displayLink: String,
    var link: String,
)

