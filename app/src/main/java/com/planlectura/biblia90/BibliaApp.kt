package com.planlectura.biblia90

import android.app.Application
import com.planlectura.biblia90.notifications.ReminderNotifier

class BibliaApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        ReminderNotifier.createChannel(this)
    }
}
