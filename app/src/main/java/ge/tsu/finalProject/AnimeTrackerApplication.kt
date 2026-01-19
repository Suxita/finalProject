package ge.tsu.finalProject

import android.app.Application
import ge.tsu.finalProject.di.AppModule

class AnimeTrackerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AppModule.initialize(
            context = this,
            claudeApiKey = BuildConfig.CLAUDE_API_KEY
        )
    }
}