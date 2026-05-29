package cu.sg.system.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cu.sg.system.data.local.dao.*
import cu.sg.system.data.local.entity.*

@Database(
    entities = [
        ClientEntity::class,
        ServiceEntity::class,
        ClientServiceCrossRef::class,
        ClientNoteEntity::class,
        ClientDocumentEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun serviceDao(): ServiceDao
    abstract fun clientNoteDao(): ClientNoteDao
    abstract fun clientDocumentDao(): ClientDocumentDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sgc_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}