package com.notex.sd

import android.app.Application
import com.notex.sd.util.CrashHandler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NoteXApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CrashHandler.init(this)
    }
}
