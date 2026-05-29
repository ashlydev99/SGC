package cu.sg.system.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cu.sg.system.data.repository.ServiceRepository
import cu.sg.system.domain.model.Service
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ServiceViewModel(
    private val serviceRepository: ServiceRepository
) : ViewModel() {
    
    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _operationSuccess = MutableStateFlow(false)
    val operationSuccess: StateFlow<Boolean> = _operationSuccess.asStateFlow()
    
    init {
        loadServices()
    }
    
    private fun loadServices() {
        viewModelScope.launch {
            serviceRepository.getAllServices().collect { serviceList ->
                _services.value = serviceList
            }
        }
    }
    
    fun createService(name: String, type: String, price: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val service = Service(
                    name = name,
                    type = type,
                    price = price
                )
                serviceRepository.createService(service)
                _operationSuccess.value = true
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al crear el servicio: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteService(service: Service) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                serviceRepository.deleteService(service)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al eliminar el servicio: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearOperationSuccess() {
        _operationSuccess.value = false
    }
    
    class Factory(
        private val serviceRepository: ServiceRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ServiceViewModel::class.java)) {
                return ServiceViewModel(serviceRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}