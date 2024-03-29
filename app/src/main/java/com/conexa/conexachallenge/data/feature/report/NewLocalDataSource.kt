package com.conexa.conexachallenge.data.feature.report

import android.content.Context
import android.content.SharedPreferences
import com.conexa.conexachallenge.data.api.model.response.posts.NewsResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NewLocalDataSource(context: Context, private val gson: Gson) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(
            PREF_FILE_NAME,
            MODE,
        )

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    private var newsList: List<NewsResponse>?
        get() {
            val json = preferences.getString(PREF_NEWS_LIST, null)
            return if (json != null) {
                gson.fromJson(json, object : TypeToken<List<NewsResponse>>() {}.type)
            } else {
                null
            }
        }
        set(value) {
            preferences.edit {
                it.putString(PREF_NEWS_LIST, gson.toJson(value))
            }
        }

    fun save(news: List<NewsResponse>) {
        newsList = news
    }

    fun get(): List<NewsResponse>? {
        return try {
            newsList
        } catch (e: Exception) {
            null
        }
    }

    fun clear() {
        newsList = null
    }

    companion object {
        private const val MODE = Context.MODE_PRIVATE
        private const val PREF_FILE_NAME = "newsLocalDataSource"
        private const val PREF_NEWS_LIST = "newsList"
    }
}
