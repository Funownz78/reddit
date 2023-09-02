package ru.fwnz.humblr

import android.app.Application
import ru.fwnz.humblr.di.AppModule
import ru.fwnz.humblr.di.DaggerComponent
import ru.fwnz.humblr.di.DaggerDaggerComponent

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        daggerComponent = DaggerDaggerComponent
            .builder()
//            .daggerAuthModule(DaggerAuthModule(this))
            .appModule(AppModule(this))
            .build()
    }

    companion object {
        lateinit var daggerComponent: DaggerComponent
    }
}