package cu.thunder.ai.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "thunderai_settings")

class PreferencesManager(private val context: Context) {
    
    companion object {
        private val MODEL_PATH_KEY = stringPreferencesKey("model_path")
        private val TEMPERATURE_KEY = floatPreferencesKey("temperature")
        private val MAX_TOKENS_KEY = intPreferencesKey("max_tokens")
    }
    
    val modelPathFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[MODEL_PATH_KEY] ?: "" }
    
    val temperatureFlow: Flow<Float> = context.dataStore.data
        .map { preferences -> preferences[TEMPERATURE_KEY] ?: 0.7f }
    
    val maxTokensFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[MAX_TOKENS_KEY] ?: 2048 }
    
    suspend fun saveModelPath(path: String) {
        context.dataStore.edit { preferences ->
            preferences[MODEL_PATH_KEY] = path
        }
    }
    
    suspend fun saveTemperature(temp: Float) {
        context.dataStore.edit { preferences ->
            preferences[TEMPERATURE_KEY] = temp
        }
    }
    
    suspend fun saveMaxTokens(tokens: Int) {
        context.dataStore.edit { preferences ->
            preferences[MAX_TOKENS_KEY] = tokens
        }
    }
}