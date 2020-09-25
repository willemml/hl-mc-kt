package protocol

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import protocol.types.readMinecraftString
import protocol.types.toMinecraftBytes
import java.net.InetSocketAddress

@ExperimentalUnsignedTypes
@KtorExperimentalAPI
fun protocolTest() {
    // test socket for testing reading/writing of custom data types
    GlobalScope.launch {
        val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress("127.0.0.1", 25565))
        while (true) {
            val socket = server.accept()
            launch {
                println("Socket accepted: ${socket.remoteAddress}")

                val input = socket.openReadChannel()
                val output = socket.openWriteChannel(true)

                try {
                    while (true) {
                        "Hello client!".toMinecraftBytes().forEach { output.writeByte(it.toByte()) }
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    socket.close()
                }
            }
        }
    }
    runBlocking {
        val client = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 25565))
        println("client socket created")
        val input = client.openReadChannel()
        val output = client.openWriteChannel(true)
        println("read write channels opened")
        /*Handshake(VarInt(751), "127.0.0.1", 25565u, NextState.Login).send(output)
        Request().send(output)
        println("data sent?")
        println(Response().readFrom(input).jsonResponse)*/
        while (true) {
            val response = input.readMinecraftString()
            println("Server said: $response") // should say "Hello client!", currently just errors out because of VarInt being too long due to read/write problems, check issue #3
        }
    }
}