package cu.thunder.ai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cu.thunder.ai.data.local.dao.ConversationDao
import cu.thunder.ai.data.local.entity.Conversation
import cu.thunder.ai.data.local.entity.Converters

@Database(
    entities = [Conversation::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "thunderai_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}