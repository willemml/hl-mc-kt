package net.willemml.hlktmc.minecraft

import com.github.steveice10.mc.protocol.MinecraftConstants
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.willemml.hlktmc.minecraft.objects.Block
import net.willemml.hlktmc.minecraft.objects.BoundingBox
import net.willemml.hlktmc.minecraft.objects.Item
import net.willemml.hlktmc.minecraft.objects.Material
import java.io.File

val AIR_BLOCK = Block(
    id = 0,
    displayName = "Air",
    name = "air",
    hardness = 0.0f,
    stackSize = 0,
    diggable = true,
    BoundingBox.empty,
    material = null,
    drops = emptyArray(),
    emitLight = 0,
    filterLight = 0,
    transparent = true
)

object ResourceManager {
    private val classLoader = javaClass.classLoader
    private val json = Json { ignoreUnknownKeys = true }

    var dataPaths = HashMap<String, HashMap<String, HashMap<String, String>>>()

    var blocks = HashMap<Int, Block>()
    var items = HashMap<Int, Item>()
    var materials = HashMap<String, Material>()

    fun loadPaths() {
        val pathFile = File(classLoader.getResource("minecraft-data/data/dataPaths.json")?.file ?: return)
        dataPaths = json.decodeFromString(pathFile.readText())
    }

    fun loadBlocks() {
        val blocksPath = dataPaths["pc"]?.get(MinecraftConstants.GAME_VERSION)?.get("blocks") ?: return
        val blocksFile = File(classLoader.getResource("minecraft-data/data/$blocksPath/blocks.json")?.file ?: return)
        val blocksArray = json.decodeFromString<Array<Block>>(blocksFile.readText())
        for (block in blocksArray) blocks[block.id] = block
        if (!blocks.containsKey(0)) blocks[0] = AIR_BLOCK
    }

    fun loadItems() {
        val itemsPath = dataPaths["pc"]?.get(MinecraftConstants.GAME_VERSION)?.get("items") ?: return
        val itemsFile = File(classLoader.getResource("minecraft-data/data/$itemsPath/items.json")?.file ?: return)
        val itemsArray = json.decodeFromString<Array<Item>>(itemsFile.readText())
        for (item in itemsArray) items[item.id] = item
    }

    fun loadMaterials() {
        val materialsPath = dataPaths["pc"]?.get(MinecraftConstants.GAME_VERSION)?.get("materials") ?: return
        val materialsFile =
            File(classLoader.getResource("minecraft-data/data/$materialsPath/materials.json")?.file ?: return)
        materials = json.decodeFromString(materialsFile.readText())
    }
}