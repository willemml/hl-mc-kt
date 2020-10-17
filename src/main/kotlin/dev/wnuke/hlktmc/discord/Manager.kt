package dev.wnuke.hlktmc.discord

import dev.wnuke.hlktmc.BotManager
import dev.wnuke.hlktmc.DiscordConfig
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DiscordBotManager(configPath: String) : BotManager<DiscordConfig, Discord>(configPath) {
    override fun startBot(config: DiscordConfig): Discord {
        return Discord(config).apply { GlobalScope.launch { start() } }
    }

    override fun decodeConfig(jsonString: String): HashMap<String, DiscordConfig> {
        return Json.decodeFromString(jsonString)
    }

    override fun encodeConfig(configs: HashMap<String, DiscordConfig>): String {
        return Json.encodeToString(configs)
    }
}