package cu.thunder.ai.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ChatHistory::class],
    version = 1,
    exportSchema = false
)
abstract class ChatHistoryDatabase : RoomDatabase() {
    abstract fun chatHistoryDao(): ChatHistoryDao
    
    companion object {
        @Volatile
        private var INSTANCE: ChatHistoryDatabase? = null
        
        fun getInstance(context: Context): ChatHistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatHistoryDatabase::class.java,
                    "chat_history_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}