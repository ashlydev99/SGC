package cu.thunder.ai

import android.app.Application
import cu.thunder.ai.data.local.AppDatabase

class ThunderAIApp : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
}