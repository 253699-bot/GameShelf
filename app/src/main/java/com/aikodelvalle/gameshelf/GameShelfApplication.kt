package com.aikodelvalle.gameshelf

import android.app.Application
import com.aikodelvalle.gameshelf.core.di.AppContainer

class GameShelfApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
