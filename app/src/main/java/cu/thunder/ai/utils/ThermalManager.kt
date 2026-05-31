package cu.thunder.ai.utils

import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

/**
 * Gestiona la temperatura del dispositivo para evitar overheating
 * durante generaciones largas
 */
class ThermalManager(private val context: Context) {
    
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    
    private val _thermalStatus = MutableStateFlow(ThermalStatus.NORMAL)
    val thermalStatus: StateFlow<ThermalStatus> = _thermalStatus.asStateFlow()
    
    private val _recommendedReduction = MutableStateFlow(1.0f)
    val recommendedReduction: StateFlow<Float> = _recommendedReduction.asStateFlow()
    
    suspend fun monitorThermal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            while (true) {
                val status = when (powerManager.currentThermalStatus) {
                    PowerManager.THERMAL_STATUS_NONE -> ThermalStatus.NORMAL
                    PowerManager.THERMAL_STATUS_LIGHT -> ThermalStatus.LIGHT
                    PowerManager.THERMAL_STATUS_MODERATE -> ThermalStatus.MODERATE
                    PowerManager.THERMAL_STATUS_SEVERE -> ThermalStatus.SEVERE
                    PowerManager.THERMAL_STATUS_CRITICAL -> ThermalStatus.CRITICAL
                    else -> ThermalStatus.NORMAL
                }
                _thermalStatus.value = status
                _recommendedReduction.value = when (status) {
                    ThermalStatus.NORMAL -> 1.0f
                    ThermalStatus.LIGHT -> 0.8f
                    ThermalStatus.MODERATE -> 0.6f
                    ThermalStatus.SEVERE -> 0.4f
                    ThermalStatus.CRITICAL -> 0.2f
                }
                delay(2000)
            }
        }
    }
    
    fun shouldReducePerformance(): Boolean {
        return _thermalStatus.value >= ThermalStatus.MODERATE
    }
}

enum class ThermalStatus {
    NORMAL, LIGHT, MODERATE, SEVERE, CRITICAL
}