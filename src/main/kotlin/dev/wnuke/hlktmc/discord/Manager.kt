package dev.wnuke.hlktmc.discord

import dev.wnuke.hlktmc.BotManager
import dev.wnuke.hlktmc.DiscordConfig
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DiscordBotManager(configPath: String) : BotManager<DiscordConfig, Discord>(configPath) {
    override fun startBot(config: DiscordConfig): Discord {
        return Discord(config).apply { GlobalScope.launch { start() } }
    }
}