package net.willemml.hlktmc.minecraft.bot

import net.willemml.hlktmc.BotManager
import net.willemml.hlktmc.ChatBotConfig
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChatBotManager(configPath: String) : BotManager<ChatBotConfig, ChatBot>(configPath) {
    override fun startBot(config: ChatBotConfig) = ChatBot(config).apply { GlobalScope.launch { connect() } }

    override fun decodeConfig(jsonString: String): HashMap<String, ChatBotConfig> = Json.decodeFromString(jsonString)

    override fun encodeConfig(configs: HashMap<String, ChatBotConfig>): String = Json.encodeToString(botConfigs)
}