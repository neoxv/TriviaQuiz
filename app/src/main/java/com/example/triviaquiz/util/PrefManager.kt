package com.example.triviaquiz.util

import android.content.Context
import com.example.triviaquiz.util.Constant.PREF_FILE
import com.example.triviaquiz.util.Constant.PREF_USER_NAME

class PrefManager(context: Context) {
    private val preferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

    var username: String
        get() = preferences.getString(PREF_USER_NAME, "").toString()
        set(name) {
            preferences.edit().putString(PREF_USER_NAME, name).apply()
        }
}