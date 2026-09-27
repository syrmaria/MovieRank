package com.maria.movierank.network.domain.utils

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.collections.iterator

private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
private const val KEY_ALIAS = "movierank_prefs_key"
private const val TRANSFORMATION = "AES/GCM/NoPadding"
private const val GCM_IV_LENGTH = 12
private const val GCM_TAG_BITS = 128

class SecurePreferences(context: Context) : SharedPreferences {

    private val prefs = context.getSharedPreferences("movierank_secure_prefs", Context.MODE_PRIVATE)
    private val key: SecretKey by lazy { getOrCreateKey() }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).also { it.load(null) }
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER).apply {
                init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(256)
                        .build()
                )
                generateKey()
            }
        }
        return (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
    }

    private fun encrypt(plaintext: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(iv + ciphertext, Base64.DEFAULT)
    }

    private fun decrypt(encoded: String): String {
        val combined = Base64.decode(encoded, Base64.DEFAULT)
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
        val ciphertext = combined.copyOfRange(GCM_IV_LENGTH, combined.size)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))
        return String(cipher.doFinal(ciphertext), Charsets.UTF_8)
    }

    override fun getString(key: String, defValue: String?): String? {
        val encoded = prefs.getString(key, null) ?: return defValue
        return decrypt(encoded)
    }

    override fun edit(): SharedPreferences.Editor = Editor()

    private inner class Editor : SharedPreferences.Editor {
        private val delegate = prefs.edit()
        private var shouldClear = false
        private val pendingStrings = mutableMapOf<String, String?>()

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            pendingStrings[key] = value
            return this
        }

        private fun flush() {
            for ((k, v) in pendingStrings) {
                if (v != null) {
                    delegate.putString(k, encrypt(v))
                } else {
                    delegate.remove(k)
                }
            }
            pendingStrings.clear()
        }

        override fun apply() { flush(); delegate.apply() }
        override fun commit(): Boolean { flush(); return delegate.commit() }

        override fun putStringSet(k: String, v: Set<String>?): SharedPreferences.Editor { delegate.putStringSet(k, v); return this }
        override fun putInt(k: String, v: Int): SharedPreferences.Editor { delegate.putInt(k, v); return this }
        override fun putLong(k: String, v: Long): SharedPreferences.Editor { delegate.putLong(k, v); return this }
        override fun putFloat(k: String, v: Float): SharedPreferences.Editor { delegate.putFloat(k, v); return this }
        override fun putBoolean(k: String, v: Boolean): SharedPreferences.Editor { delegate.putBoolean(k, v); return this }
        override fun remove(k: String): SharedPreferences.Editor { delegate.remove(k); return this }
        override fun clear(): SharedPreferences.Editor {
            shouldClear = true
            pendingStrings.clear()
            delegate.clear()
            return this
        }
    }

    override fun getAll(): Map<String, *> = prefs.all
    override fun getStringSet(k: String, v: Set<String>?): Set<String>? = prefs.getStringSet(k, v)
    override fun getInt(k: String, v: Int): Int = prefs.getInt(k, v)
    override fun getLong(k: String, v: Long): Long = prefs.getLong(k, v)
    override fun getFloat(k: String, v: Float): Float = prefs.getFloat(k, v)
    override fun getBoolean(k: String, v: Boolean): Boolean = prefs.getBoolean(k, v)
    override fun contains(k: String): Boolean = prefs.contains(k)
    override fun registerOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener) = prefs.registerOnSharedPreferenceChangeListener(l)
    override fun unregisterOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener) = prefs.unregisterOnSharedPreferenceChangeListener(l)
}
