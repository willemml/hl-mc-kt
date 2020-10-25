package net.willemml.hlktmc.minecraft.objects

import com.github.steveice10.mc.protocol.MinecraftConstants
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

object ResourceManager {
    private val classLoader = javaClass.classLoader

    var dataPaths = HashMap<String, HashMap<String, String>>()

    var blocks = HashMap<Int, Block>()
    var items = HashMap<Int, Item>()
    var materials = HashMap<String, Material>()

    fun loadPaths() {
        val pathFile = File(classLoader.getResource("minecraft-data/data/dataPaths.json")?.file?: return)
        dataPaths = Json.decodeFromString(pathFile.readText())
    }

    fun loadBlocks() {
        val blocksPath = dataPaths[MinecraftConstants.GAME_VERSION]?.get("blocks")?: return
        val blocksFile = File(classLoader.getResource("minecraft-data/data/$blocksPath")?.file?: return)
        blocks = Json.decodeFromString(blocksFile.readText())
    }

    fun loadItems() {
        val itemsPath = dataPaths[MinecraftConstants.GAME_VERSION]?.get("items")?: return
        val itemsFile = File(classLoader.getResource("minecraft-data/data/$itemsPath")?.file?: return)
        items = Json.decodeFromString(itemsFile.readText())
    }

    fun loadMaterials() {
        val materialsPath = dataPaths[MinecraftConstants.GAME_VERSION]?.get("materials")?: return
        val materialsFile = File(classLoader.getResource("minecraft-data/data/$materialsPath")?.file?: return)
        materials = Json.decodeFromString(materialsFile.readText())
    }
}