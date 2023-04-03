package com.hangman.android.core.preferencesadapter.items

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

/**
 * A Serializer describing how to serialize/deserialize the settings in order to be stored
 * in a DataStore.
 * The SettingsPreference class is defined in the datastore proto file
 * (main/proto/settings_preferences.proto) and the whole class is auto generated.
 */
class SettingsPreferencesSerializer: Serializer<SettingsPreferences> {
    override val defaultValue: SettingsPreferences = SettingsPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SettingsPreferences {
        try {
            return SettingsPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read settings protobuf file", exception)
        }
    }

    override suspend fun writeTo(t: SettingsPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}