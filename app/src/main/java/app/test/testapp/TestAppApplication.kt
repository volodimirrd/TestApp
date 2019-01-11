package app.test.testapp

import android.app.Application

class TestAppApplication: Application() {
    companion object {
        lateinit var instance: TestAppApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}