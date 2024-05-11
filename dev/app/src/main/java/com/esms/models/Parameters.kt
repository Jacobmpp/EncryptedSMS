package com.esms.models

import android.app.Application
import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import com.esms.services.CryptographyEngineGenerator
import com.esms.services.SharedPreferencesService
import com.esms.services.engines.CryptographyEngine
import com.esms.services.engines.custom.PlainTextEngine
import com.esms.views.parameters.selectors.ColorSelector
import com.esms.views.parameters.selectors.FreeSelector
import com.esms.views.parameters.selectors.ModalSelector
import com.esms.views.parameters.selectors.OptionsSelector
import com.esms.views.parameters.selectors.SectionMarker
import com.esms.views.parameters.selectors.ToggleSelector
import java.lang.Long.parseLong

class Parameters (application: Application) : AndroidViewModel(application){
    // Constants
    val DEFAULT_ENCRYPTION_ALGORITHM = "AES"
    val DEFAULT_ENCRYPTION_PARAMETERS = "insecure"
    val DEFAULT_LABEL = "Default"

    // Services
    private val engineGen = CryptographyEngineGenerator()
    private val saveSystem = SharedPreferencesService(application.applicationContext)
    val app = application

    // Ephemeral Params
    var loaded = mutableStateOf(false)
    var currentContact = mutableStateOf<PhoneContact?>(null)
        private set

    var currentEncryptionEngine = mutableStateOf<CryptographyEngine>(PlainTextEngine(""))
        private set
    fun setCurrentEncryptionEngineFromNumber(number: String){
        currentEncryptionEngine.value = engineGen.createEngine(getEncryptionAlgorithmForNumber(number), getEncryptionParametersForNumber(number))
    }

    private var currentMessageAdder = mutableStateOf<((SMSMessage) -> Unit)?>(null)
    fun setCurrentMessageAdder(func: (SMSMessage)->Unit){
        currentMessageAdder.value = func
    }
    fun runCurrentMessageAdder(msg: SMSMessage){
        currentMessageAdder.value?.invoke(msg)
    }

    // Saved Params
    private var numberToEncryptionAlgorithm = mutableMapOf<String, String>()
    private fun getEncryptionAlgorithmForNumber(number: String?) : String{
        return numberToEncryptionAlgorithm[number]
            ?: numberToEncryptionAlgorithm[""]
            ?: DEFAULT_ENCRYPTION_ALGORITHM
    }

    private var numberToEncryptionParameters = mutableMapOf<String, String>()
    private fun getEncryptionParametersForNumber(number: String?) : String {
        return numberToEncryptionParameters[number]
            ?: numberToEncryptionParameters[""]
            ?: DEFAULT_ENCRYPTION_PARAMETERS
    }

    private var saveEncryptionParameter = mutableStateOf(DEFAULT_ENCRYPTION_PARAMETERS)

    private var numberToNickname = mutableMapOf<String, String>()
    fun getNicknameForNumber(number: String, default: String) : String {
        return numberToNickname[number] ?: default
    }
    fun setNicknameForNumber(number: String, nickname: String) {
        numberToNickname[number] = nickname
    }

    private var numberToLastMessageTime = mutableMapOf<String, String>()
    fun getLastMessageTimeForNumber(number: String) : Long {
        return parseLong(numberToLastMessageTime[number] ?: "0")
    }
    fun setLastMessageTimeForNumber(number: String, timestamp: Long) {
        if(getLastMessageTimeForNumber(number) < timestamp){
            numberToLastMessageTime[number] = timestamp.toString()
            save()
        }
    }

    private var numberToSortingPriority = mutableMapOf<String, Float>()
    fun getSortingPriorityForNumber(number: String) : Float {
        return numberToSortingPriority[number] ?: 0f
    }
    fun setSortingPriorityForNumber(number: String, value: Float) {
        numberToSortingPriority[number] = value
        save()
    }

    var theme = mutableStateOf("System") // Light, Dark, System, Custom

    private val customColors = mutableStateOf(
        darkColors(
            primary = Color(0xFF1111AA),
            primaryVariant = Color(0xFF116666),
            onPrimary = Color(0xFFEEEEEE),

            secondary = Color(0xFF771177),
            secondaryVariant = Color(0xFF804040),
            onSecondary = Color(0xFFEEEEEE),

            background = Color(0xFF333333),
            onBackground = Color(0xFFEEEEEE),

            surface = Color(0xFF111111),
            onSurface = Color(0xFFEEEEEE),

            error = Color(0xFFFF7070),
            onError = Color(0xFF111111),
        )
    )
    private val customColorsMap = mutableMapOf<String, String>()
    fun getCustomColors() : Colors {
        return customColors.value
    }
    private fun setCustomColorsFromMap(stringMap: Map<String, String>) {
        if(stringMap.isEmpty())
            return
        customColors.value = darkColors(
            primary = Color(stringMap["primary"]!!.toInt()),
            primaryVariant = Color(stringMap["primaryVariant"]!!.toInt()),
            onPrimary = Color(stringMap["onPrimary"]!!.toInt()),

            secondary = Color(stringMap["secondary"]!!.toInt()),
            secondaryVariant = Color(stringMap["secondaryVariant"]!!.toInt()),
            onSecondary = Color(stringMap["onSecondary"]!!.toInt()),

            background = Color(stringMap["background"]!!.toInt()),
            onBackground = Color(stringMap["onBackground"]!!.toInt()),

            surface = Color(stringMap["surface"]!!.toInt()),
            onSurface = Color(stringMap["onSurface"]!!.toInt()),

            error = Color(stringMap["error"]!!.toInt()),
            onError = Color(stringMap["onError"]!!.toInt()),
        )
        for (entry in stringMap.entries) {
            customColorsMap[entry.key] = entry.value
        }
    }
    private fun getCustomColorsMap() : Map<String, String>{
        return mapOf(
            "primary" to customColors.value.primary.toArgb().toString(),
            "primaryVariant" to customColors.value.primaryVariant.toArgb().toString(),
            "onPrimary" to customColors.value.onPrimary.toArgb().toString(),
            "secondary" to customColors.value.secondary.toArgb().toString(),
            "secondaryVariant" to customColors.value.secondaryVariant.toArgb().toString(),
            "onSecondary" to customColors.value.onSecondary.toArgb().toString(),
            "background" to customColors.value.background.toArgb().toString(),
            "onBackground" to customColors.value.onBackground.toArgb().toString(),
            "surface" to customColors.value.surface.toArgb().toString(),
            "onSurface" to customColors.value.onSurface.toArgb().toString(),
            "error" to customColors.value.error.toArgb().toString(),
            "onError" to customColors.value.onError.toArgb().toString(),
        )
    }

    // Persistence Functions
    val SINGLE_VALUE_PARAMETERS = "A"
    val ENCRYPTION_ALGORITHMS = "0"
    val ENCRYPTION_PARAMETERS = "1"
    val SAVE_ENCRYPTION_PARAMETER = "2"
    val NICKNAMES = "3"
    val TIMESTAMPS = "4"
    val THEME = "5"
    val CUSTOM_THEME = "6"
    val CONTACT_ORDERING_PRIORITY = "7"

    fun save() {
        val maps = mapOf(
            ENCRYPTION_ALGORITHMS to numberToEncryptionAlgorithm.toMap(),
            ENCRYPTION_PARAMETERS to numberToEncryptionParameters.toMap(),
            NICKNAMES to numberToNickname.toMap(),
            TIMESTAMPS to numberToLastMessageTime.toMap(),
            CUSTOM_THEME to getCustomColorsMap(),
            CONTACT_ORDERING_PRIORITY to numberToSortingPriority
                .mapValues { (_, value) -> value.toString() },
            SINGLE_VALUE_PARAMETERS to mapOf(
                    SAVE_ENCRYPTION_PARAMETER to saveEncryptionParameter.value,
                    THEME to theme.value,
                ),
            )
        val saveEncryptor = engineGen.createEngine("AES", saveEncryptionParameter.value)
        val saveString = saveSystem.serializeMapOfMaps(maps)
        val encryptedSaveString = saveEncryptor.encrypt(saveString)
        saveSystem.write(encryptedSaveString)
    }
    fun load(key: String = DEFAULT_ENCRYPTION_PARAMETERS) {
        val savedString = saveSystem.read()
        val decryptingEngine = engineGen.createEngine("AES", key)
        val decryptedString = try {decryptingEngine.decrypt(savedString)} catch (_: Exception) {savedString}
        try {
            if(decryptedString == savedString && savedString != "")
                throw Exception()
            val maps = saveSystem.deserializeMapOfMaps(decryptedString)

            numberToEncryptionAlgorithm = maps[ENCRYPTION_ALGORITHMS]?.toMutableMap()
                ?: mutableMapOf("" to DEFAULT_ENCRYPTION_ALGORITHM)
            numberToEncryptionParameters = maps[ENCRYPTION_PARAMETERS]?.toMutableMap()
                ?: mutableMapOf("" to DEFAULT_ENCRYPTION_PARAMETERS)
            numberToNickname = maps[NICKNAMES]?.toMutableMap() ?: mutableMapOf()
            numberToLastMessageTime = maps[TIMESTAMPS]?.toMutableMap() ?: mutableMapOf()
            setCustomColorsFromMap(maps[CUSTOM_THEME] ?: getCustomColorsMap())
            numberToSortingPriority = maps[CONTACT_ORDERING_PRIORITY]?.mapValues { (_, value) -> value.toFloat() }?.toMutableMap() ?: mutableMapOf("" to 0f)

            if(maps.containsKey(SINGLE_VALUE_PARAMETERS)){
                val svp = maps[SINGLE_VALUE_PARAMETERS]!!
                theme.value = svp[THEME] ?: "System"
                saveEncryptionParameter.value = svp[SAVE_ENCRYPTION_PARAMETER]
                    ?: DEFAULT_ENCRYPTION_PARAMETERS
            } else {
                // Backwards compatibility load (don't need to add new params because they wont have them saved in the old format)
                theme.value = (maps[THEME] ?: mapOf("" to "System"))[""] ?: "System"
                saveEncryptionParameter.value = (maps[SAVE_ENCRYPTION_PARAMETER]
                    ?: mapOf("" to DEFAULT_ENCRYPTION_PARAMETERS))[""]
                    ?: DEFAULT_ENCRYPTION_PARAMETERS
            }

            loaded.value = true
        } catch (_: Exception){}
    }

    // Editable Parameters
    fun getEditableParameterElementsList() : List<@Composable ()->Unit> {
        val globalParams = currentContact.value == null
        return listOfNotNull(
            SectionMarker("Contact Specific Settings", isNull = globalParams),
            nicknameSelector(currentContact.value),
            contactSortingPrioritySelector(currentContact.value),
            SectionMarker("Encryption Settings", isNull = globalParams),
            encryptionAlgorithmSelector(currentContact.value),
            encryptionParameterSelector(currentContact.value),
            SectionMarker("Default Encryption Settings"),
            defaultEncryptionAlgorithmSelector(),
            defaultEncryptionParameterSelector(),
            globalEncryptionKeySelector(),
            SectionMarker("Theme Settings"),
            primaryThemeSelector(),
            customThemeColorSelectors(theme.value),
        )
    }

    private fun encryptionAlgorithmSelector(currentContact: PhoneContact?) : (@Composable ()->Unit)? {
        if(currentContact == null)
            return null
        return OptionsSelector(
            name = "Encryption Algorithm",
            hint = "The algorithmn that will be used to encrypt messages with this contact.\n" +
                    "(default) denotes that if you change your default algorithm, this one will change as well.\n" +
                    "Plain Text means that no encryption is done and the message is sent as is.\n" +
                    "Caeser Cipher is a very old form of cipher that shifts letters by some constant value\n" +
                    "AES is military grade encryption assuming you pick a secure key and share it with the other messenger securely.\n" +
                    "DES is an old insecure algorithm that appears visually similar.\n" +
                    "DESede is a 3 layer version of DES that is basically secure by today's standards.",
            setter = { algorithm: String -> run {
                if (algorithm.contains(DEFAULT_LABEL))
                    numberToEncryptionAlgorithm.remove(currentContact.number)
                else
                    numberToEncryptionAlgorithm[currentContact.number] = algorithm
                save()
                setCurrentEncryptionEngineFromNumber(currentContact.number)
            }},
            options = listOf("$DEFAULT_LABEL (${getEncryptionAlgorithmForNumber("")})") +
                      CryptographyEngineGenerator().getRegisteredEngines(),
            currentState = defaultLabelIfDefault(currentContact, getEncryptionAlgorithmForNumber(currentContact.number))
        )
    }
    private fun encryptionParameterSelector(currentContact: PhoneContact?) : (@Composable ()->Unit)? {
        if(currentContact == null)
            return null
        return FreeSelector(
            name = "Encryption Parameter",
            hint = "This is the key that will be used to encrypt and decrypt the messages you exchange with this person.\n" +
            "Make sure it is long (>8 characters) and hard to guess (think password requirements) if you really want it to be secure.\n" +
            "If you are using Caeser Cipher, this must be a number.",
            setter = { algorithm: String -> run {
                numberToEncryptionParameters[currentContact.number] = algorithm
                save()
                setCurrentEncryptionEngineFromNumber(currentContact.number)
            }},
            currentState = getEncryptionParametersForNumber(currentContact.number)
        )
    }
    private fun defaultEncryptionAlgorithmSelector() : @Composable ()->Unit{
        return OptionsSelector(
            name = "Default Encryption Algorithm",
            hint = "The algorithmn that will be used to encrypt messages by default if you do not change it in the conversation settings.\n" +
                    "(default) denotes that if you change your default algorithm, this one will change as well.\n" +
                    "Plain Text means that no encryption is done and the message is sent as is.\n" +
                    "Caeser Cipher is a very old form of cipher that shifts letters by some constant value\n" +
                    "AES is military grade encryption assuming you pick a secure key and share it with the other messenger securely.\n" +
                    "DES is an old insecure algorithm that appears visually similar.\n" +
                    "DESede is a 3 layer version of DES that is basically secure by today's standards.",
            setter = { algorithm: String -> run {
                numberToEncryptionAlgorithm[""] = algorithm
                save()
                setCurrentEncryptionEngineFromNumber("")
            }},
            options = CryptographyEngineGenerator().getRegisteredEngines(),
            currentState = getEncryptionAlgorithmForNumber("")
        )
    }
    private fun defaultEncryptionParameterSelector() : @Composable ()->Unit {
        return FreeSelector(
            name = "Default Encryption Parameter",
            hint = "This is the key that will be used by default for any conversation where you have not set it.\n" +
                    "Make sure it is long (>8 characters) and hard to guess (think password requirements) if you really want it to be secure.\n" +
                    "If you are using Caeser Cipher, this must be a number.",
            setter = { algorithm: String -> run {
                numberToEncryptionParameters[""] = algorithm
                save()
                setCurrentEncryptionEngineFromNumber("")
            }},
            currentState = getEncryptionParametersForNumber("")
        )
    }
    private fun globalEncryptionKeySelector() : @Composable ()->Unit {
        return FreeSelector(
            name = "Save Encryption Key",
            hint = "The password that you can use to have the saved data of this application securely encrypted when it is not open.\n" +
            "The default password is '${DEFAULT_ENCRYPTION_PARAMETERS}' which is tried automatically. " +
                    "If you change it, you will be asked to input the password upon startup.\n" +
            "If you change the password and then forget it, YOU CANNOT GET BACK ANYTHING you have saved " +
                    "including saved encryption keys and algorithms as well as nicknames and custom themes.",
            setter = { key: String -> run {
                saveEncryptionParameter.value = key
                save()
            }},
            currentState = saveEncryptionParameter.value,
            comment = " (\"$DEFAULT_ENCRYPTION_PARAMETERS\" = no auth screen)"
        )
    }
    private fun nicknameSelector(currentState: PhoneContact?) : (@Composable ()->Unit)? {
        if(currentState == null)
            return null

        return FreeSelector(
            name = "Nickname",
            setter = { key: String -> run {
                if(key.isNotBlank())
                    setNicknameForNumber(currentState.number, key)
                else
                    numberToNickname.remove(currentState.number)
                save()
            }},
            currentState = getNicknameForNumber(currentState.number, currentState.name),
            comment = " (Leave this blank -> Reset to ${currentState.name})"
        )
    }
    private fun contactSortingPrioritySelector(currentState: PhoneContact?) : (@Composable ()->Unit)? {
        if(currentState == null)
            return null

        return FreeSelector(
            name = "Sorting Priority",
            hint = "If this value is higher than the value for another contact (default 0), " +
                    "then this contact will be placed higher. If contact are tied, " +
                    "they are sorted by most recently read message.",
            setter = { key: String -> run {
                if((key.toFloatOrNull() ?: 0f) != 0f)
                    setSortingPriorityForNumber(currentState.number, key.toFloat())
                else
                    numberToSortingPriority.remove(currentState.number)
                save()
            }},
            currentState = getSortingPriorityForNumber(currentState.number).toString(),
        )
    }
    private fun primaryThemeSelector() : @Composable ()->Unit {
        return OptionsSelector(
            name = "Color Theme",
            setter = { key: String -> run {
                theme.value = key
                save()
            }},
            currentState = theme.value,
            options = listOf("System", "Dark", "Light", "Custom")
        )
    }
    private fun customThemeColorSelectors(theme: String): @Composable() (() -> Unit)? {
        if(theme != "Custom")
            return null
        return ModalSelector(
            name ="Custom Theme Colors",
            hint = "Opens a window to set the values for each theme color",
            contents = customColorSelectors()
        )
    }
    private fun customColorSelectors() : Array<@Composable ()->Unit> {
        return listOf(
                listOf("primary","The color of confirmation buttons and drop-down menus"),
                listOf("primaryVariant","The color of cancel buttons"),
                listOf("onPrimary","The color of any text or icon on top of the primary color or primaryVarient color"),
                listOf("secondary","The color of incoming messages"),
                listOf("secondaryVariant","This color is currently unused in the theme, but nothing is stopping you from changing it anyway"),
                listOf("onSecondary","The color of any text or icon on top of something of the secondary color"),
                listOf("background","The color of the very bottom layer of each screen"),
                listOf("onBackground","The color of any text or icon on top of something of the background color"),
                listOf("surface","The color in the background of popup panels like this one. You may notice that the edit icon seems to be missing, but it is just blending in."),
                listOf("onSurface","The color of any text or icon on top of something of the surface color"),
                listOf("error","This is the color that will be displayed in the case of a handled error, though it may not be used at this time. I recommend making it very different so you can recognize any time it appears as an error.")
            ).map {
                ColorSelector(
                    name = it[0],
                    hint = it[1],
                    setter = {
                        color: Color -> run {
                            customColorsMap[it[0]] = color.toArgb().toString()
                            setCustomColorsFromMap(customColorsMap.toMap())
                            save()
                        }
                    },
                    currentState = mutableStateOf(Color(customColorsMap[it[0]]!!.toInt()))
                )
            }.toTypedArray()
    }

    // Initialization
    init {
        load()
    }

    // Private Helper Functions
    private fun defaultLabelIfDefault(currentContact: PhoneContact, currentAlgorithm: String): String {
        return if (!numberToEncryptionAlgorithm.containsKey(currentContact.number))
            "$DEFAULT_LABEL ($currentAlgorithm)"
        else
            currentAlgorithm
    }
}

val LocalParameters = staticCompositionLocalOf<Parameters> {
    error("Parameters ViewModel not provided")
}