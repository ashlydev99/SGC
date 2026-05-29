package cu.sg.system.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cu.sg.system.data.local.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    val userName: StateFlow<String> = userPreferences.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Administrador")
    
    val currency: StateFlow<String> = userPreferences.currency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "USD")
    
    val isDarkMode: StateFlow<Boolean> = userPreferences.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    private val _userNameInput = MutableStateFlow("")
    val userNameInput: StateFlow<String> = _userNameInput.asStateFlow()
    
    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()
    
    init {
        viewModelScope.launch {
            userPreferences.userName.collect { name ->
                _userNameInput.value = name
            }
        }
    }
    
    fun updateUserName(name: String) {
        _userNameInput.value = name
    }
    
    fun saveUserName() {
        viewModelScope.launch {
            userPreferences.saveUserName(_userNameInput.value)
            _isSaved.value = true
        }
    }
    
    fun saveCurrency(currency: String) {
        viewModelScope.launch {
            userPreferences.saveCurrency(currency)
        }
    }
    
    fun saveDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            userPreferences.saveDarkMode(isDark)
        }
    }
    
    fun clearSaved() {
        _isSaved.value = false
    }
    
    class Factory(
        private val userPreferences: UserPreferences
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(userPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}