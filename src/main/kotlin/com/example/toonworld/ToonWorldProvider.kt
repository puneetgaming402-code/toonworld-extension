package com.example.toonworld

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class ToonWorldProvider : MainAPI() {
    override var mainUrl = "https://toonworldforall.me"
    override var name = "ToonWorldForAll"
    override val hasMainPage = true
    override var lang = "en"
    override val supportedTypes = setOf(TvType.Anime, TvType.Cartoon)

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get(mainUrl).document
        val list = document.select("article.post").mapNotNull {
            val title = it.selectFirst("h2.entry-title a")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("h2.entry-title a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img")?.attr("src")
            newAnimeSearchResponse(title, href, TvType.Anime) {
                this.posterUrl = poster
            }
        }
        return newHomePageResponse(name, list)
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document
        val title = doc.selectFirst("h1.entry-title")?.text() ?: ""
        val poster = doc.selectFirst("img")?.attr("src")
        val description = doc.selectFirst("div.entry-content p")?.text()
        val episodes = doc.select("a[href*='.mp4']").mapIndexed { index, a ->
            Episode(a.attr("href"), "Episode ${index + 1}")
        }
        return newAnimeLoadResponse(title, url, TvType.Anime) {
            this.posterUrl = poster
            this.plot = description
            this.episodes = episodes
        }
    }
}
