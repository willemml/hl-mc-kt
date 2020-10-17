package dev.wnuke.hlktmc

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

abstract class BotManager<T, U>(private val configPath: String) {
    var botConfigs = HashMap<String, T>()
    val bots = HashMap<String, U>()

    private val configFile = File(configPath).apply {
        parentFile.mkdirs()
        if (createNewFile()) this.writeText(encodeConfig(botConfigs))
    }

    init {
        readConfig()
    }

    private fun writeConfig() {
        configFile.parentFile.mkdirs()
        configFile.createNewFile()
        configFile.writeText(encodeConfig(botConfigs))
    }

    private fun readConfig() {
        try {
            botConfigs = decodeConfig(configFile.readText())
        } catch (e: SerializationException) {
            println("Broken config file, backing up and resetting...")
            configFile.copyTo(File("$configPath.old"), true)
            configFile.delete()
            writeConfig()
        }
    }

    internal abstract fun encodeConfig(configs: HashMap<String, T>): String
    internal abstract fun decodeConfig(jsonString: String): HashMap<String, T>
    internal abstract fun startBot(config: T): U

    fun add(name: String, config: T, overwrite: Boolean = false): Boolean {
        readConfig()
        if (!overwrite && botConfigs.containsKey(name)) return false
        botConfigs[name] = config
        writeConfig()
        return true
    }

    fun remove(name: String): Boolean {
        readConfig()
        return if (botConfigs.containsKey(name)) {
            botConfigs.remove(name)
            writeConfig()
            true
        } else false
    }

    fun start(name: String): Boolean {
        readConfig()
        return if (botConfigs.containsKey(name)) {
            bots[name] = startBot(botConfigs[name]!!)
            true
        } else false
    }

    fun startAll(delay: Long = 0) {
        readConfig()
        GlobalScope.launch {
            for (bot in botConfigs.keys) {
                if (start(bot)) {
                    delay(delay)
                }
            }
        }
    }
}