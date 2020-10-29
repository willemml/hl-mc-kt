package net.willemml.hlktmc.cli.commands

import chatBotManager
import net.willemml.hlktmc.cli.CLIMessage
import net.willemml.ktcmd.Command

val listBots = Command<CLIMessage>("list", "Lists all bots.", arrayListOf("ls")) { println(chatBotManager.botConfigs.keys.joinToString()) }