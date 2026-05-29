package cu.sg.system

import android.app.Application
import cu.sg.system.data.local.AppDatabase
import cu.sg.system.data.local.UserPreferences

class SGCApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
    
    val userPreferences: UserPreferences by lazy {
        UserPreferences(this)
    }
}