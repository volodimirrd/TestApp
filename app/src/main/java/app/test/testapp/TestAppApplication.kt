package app.test.testapp

import android.app.Application

class TestAppApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initInjector()
    }

    private fun initInjector() {
        Injector.context = this
    }
}