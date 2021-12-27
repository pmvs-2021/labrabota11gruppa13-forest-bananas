package com.zlatamigas.pvimslab10_4_v2kotlin

class AnimeRVModal {

    var title: String? = null
    var rating: String? = null
    var episodes: String? = null
    var preview: String? = null

    constructor(title: String?, rating: String?, eoisodes: String?, preview: String?) {
        this.title = title
        this.rating = rating
        this.episodes = eoisodes
        this.preview = preview
    }
}