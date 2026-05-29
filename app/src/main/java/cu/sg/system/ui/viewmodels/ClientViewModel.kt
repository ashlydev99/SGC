package cu.sg.system.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cu.sg.system.data.repository.ClientRepository
import cu.sg.system.domain.model.Client
import cu.sg.system.domain.model.Service
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ClientViewModel(
    private val clientRepository: ClientRepository
) : ViewModel() {
    
    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> = _clients.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<Client>>(emptyList())
    val searchResults: StateFlow<List<Client>> = _searchResults.asStateFlow()
    
    private val _selectedClient = MutableStateFlow<Client?>(null)
    val selectedClient: StateFlow<Client?> = _selectedClient.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadClients()
    }
    
    private fun loadClients() {
        viewModelScope.launch {
            clientRepository.getAllClients().collect { clientList ->
                _clients.value = clientList
            }
        }
    }
    
    fun loadClientByUid(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val client = clientRepository.getClientByUid(uid)
                _selectedClient.value = client
            } catch (e: Exception) {
                _error.value = "Error al cargar el cliente: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun createClient(
        firstName: String,
        secondName: String?,
        lastName: String,
        ci: String,
        address: String?,
        contact: String,
        services: List<Service>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val uid = generateUID()
                val client = Client(
                    uid = uid,
                    firstName = firstName,
                    secondName = secondName,
                    lastName = lastName,
                    ci = ci,
                    address = address,
                    contact = contact,
                    status = "En trámite",
                    services = services
                )
                clientRepository.createClient(client)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al crear el cliente: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateClientStatus(uid: String, newStatus: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                clientRepository.updateClientStatus(uid, newStatus)
                loadClientByUid(uid)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al actualizar el estado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun searchClients(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (query.isBlank()) {
                    _searchResults.value = _clients.value
                } else {
                    val results = clientRepository.searchClients(query)
                    _searchResults.value = results
                }
            } catch (e: Exception) {
                _error.value = "Error al buscar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteClient(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                clientRepository.deleteClient(uid)
                _selectedClient.value = null
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al eliminar el cliente: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearSelectedClient() {
        _selectedClient.value = null
    }
    
    private fun generateUID(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = (1..5).map { chars[kotlin.random.Random.nextInt(chars.length)] }.joinToString("")
        return "UID-$random"
    }
    
    class Factory(
        private val clientRepository: ClientRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClientViewModel::class.java)) {
                return ClientViewModel(clientRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}