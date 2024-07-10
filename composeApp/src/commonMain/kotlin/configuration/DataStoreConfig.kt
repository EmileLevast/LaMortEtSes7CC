package configuration

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

/**
 * Gets the singleton DataStore instance, creating it if necessary.
 */
private fun getDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { dataStoreFileName.toPath() }
    )

suspend fun getUrlSharedPreferences() = getDataStore().data.map { preferences -> preferences[PREF_KEY_URL_SERVER] ?: "localhost"}.first()

suspend fun setNewIpAdressToPreferences(newUrl:String){
    getDataStore().edit { preferences ->
        preferences[PREF_KEY_URL_SERVER] = newUrl
    }
}

private const val dataStoreFileName = "datastore.preferences_pb"
private val PREF_KEY_URL_SERVER = stringPreferencesKey("PREF_KEY_URL_SERVER")