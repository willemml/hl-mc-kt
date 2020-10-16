package dev.wnuke.hlktmc.minecraft.bot

import dev.wnuke.hlktmc.BotManager
import dev.wnuke.hlktmc.ChatBotConfig
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChatBotManager(configPath: String) : BotManager<ChatBotConfig, ChatBot>(configPath) {
    override fun startBot(config: ChatBotConfig): ChatBot {
        return ChatBot(config).apply { GlobalScope.launch { connect() } }
    }
}