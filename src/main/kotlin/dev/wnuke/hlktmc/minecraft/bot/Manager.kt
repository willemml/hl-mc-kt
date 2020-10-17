package dev.wnuke.hlktmc.minecraft.bot

import dev.wnuke.hlktmc.BotManager
import dev.wnuke.hlktmc.ChatBotConfig
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChatBotManager(configPath: String) : BotManager<ChatBotConfig, ChatBot>(configPath) {
    override fun startBot(config: ChatBotConfig): ChatBot {
        return ChatBot(config).apply { GlobalScope.launch { connect() } }
    }

    override fun decodeConfig(jsonString: String): HashMap<String, ChatBotConfig> {
        return Json.decodeFromString(jsonString)
    }

    override fun encodeConfig(configs: HashMap<String, ChatBotConfig>): String {
        return Json.encodeToString(botConfigs)
    }
}