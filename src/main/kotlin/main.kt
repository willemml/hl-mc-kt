import dev.wnuke.hlktmc.cli.CLI
import dev.wnuke.hlktmc.readDiscordConfig
import dev.wnuke.hlktmc.readMinecraftConfig
import kotlinx.serialization.InternalSerializationApi

@ExperimentalUnsignedTypes
@InternalSerializationApi
fun main() {
    readDiscordConfig()
    readMinecraftConfig()
    CLI()
}