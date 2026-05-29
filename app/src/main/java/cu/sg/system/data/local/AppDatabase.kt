package cu.sg.system.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cu.sg.system.data.local.dao.ClientDao
import cu.sg.system.data.local.dao.ServiceDao
import cu.sg.system.data.local.entity.ClientEntity
import cu.sg.system.data.local.entity.ClientServiceCrossRef
import cu.sg.system.data.local.entity.ServiceEntity

@Database(
    entities = [
        ClientEntity::class,
        ServiceEntity::class,
        ClientServiceCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun serviceDao(): ServiceDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sgc_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}