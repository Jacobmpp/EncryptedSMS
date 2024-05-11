package com.esms.services

import com.esms.services.engines.CryptographyEngine
import com.esms.services.engines.custom.*
import com.esms.services.engines.predefined.*

class CryptographyEngineGenerator {
    private val engines = mutableMapOf<String, (String) -> CryptographyEngine>()

    init {
        // Register engine types here
        registerEngine("Plain Text") { params -> PlainTextEngine(params) }
        registerEngine("Caesar Cipher") { params -> CaesarCipherEngine(params) }
        registerEngine("AES") { params -> AESCryptographyEngine(params) }
        registerEngine("DES") { params -> DESCryptographyEngine(params) }
        registerEngine("DESede") { params -> DESedeCryptographyEngine(params) }
    }

    private fun registerEngine(name: String, creator: (String) -> CryptographyEngine) {
        engines[name] = creator
    }

    fun backwardCompatibilityNameChanger(name: String): String {
        return when(name) {
            "CaesarCipher" -> "Caesar Cipher"
            "PlainText" -> "Plain Text"
            else -> name
        }
    }

    fun createEngine(name: String, parameters: String): CryptographyEngine {
        val creator = engines[backwardCompatibilityNameChanger(name)] ?: {params -> PlainTextEngine(params)}
        return creator.invoke(parameters)
    }

    fun getRegisteredEngines() : List<String>{
        return engines.keys.toList()
    }
}