package cu.thunder.ai.data

import androidx.datastore.preferences.core.stringPreferencesKey

data class UserProfile(
    val name: String = "Usuario",
    val avatarLetter: String = "U",
    val avatarColor: Int = 0xFF00D4FF
)

object UserProfileKeys {
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_AVATAR_LETTER = stringPreferencesKey("user_avatar_letter")
}