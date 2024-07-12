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
private fun getDataStore(): DataStore<Preferences> {
    if (dataStore == null) {
        dataStore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { dataStoreFileName.toPath() }
        )
    }
    return dataStore as DataStore<Preferences>
}

suspend fun getUrlSharedPreferences(): String {
    val urlFoundDataStore = getDataStore().data.map { preferences ->
        preferences[PREF_KEY_URL_SERVER] ?: "localhost"
    }
    return urlFoundDataStore.first()
}

suspend fun setNewIpAdressToPreferences(newUrl: String) {
    getDataStore().edit { preferences ->
        preferences[PREF_KEY_URL_SERVER] = newUrl
    }
}

private var dataStore:DataStore<Preferences>?=null
private const val dataStoreFileName = "datastore.preferences_pb"
private val PREF_KEY_URL_SERVER = stringPreferencesKey("PREF_KEY_URL_SERVER")