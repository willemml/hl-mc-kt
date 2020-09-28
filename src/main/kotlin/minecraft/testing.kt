package minecraft

import com.github.steveice10.mc.protocol.MinecraftProtocol
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun clientTest() {
    GlobalScope.launch {
        val client = MinecraftClient("127.0.0.1", 25565, MinecraftProtocol("clientTest"))
        client.connect()
        client.sendMessage("Hello!").sendMessage("My name is ${client.protocol.profile.name}.").sendMessage("I am an instance of hl-mc-kt.")
    }
}