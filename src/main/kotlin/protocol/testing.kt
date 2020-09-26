package protocol

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import protocol.packets.client.handshake.Handshake
import protocol.packets.client.handshake.NextState
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
                val output = socket.openWriteChannel(true)
                val mcOutput = MinecraftWriteChannel(output)
                try {
                    while (true) {
                        mcOutput.writeString("Hello from server!")
                        mcOutput.writePacket(Handshake(751, "", 243u, NextState.Login))
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    socket.dispose()
                }
            }
        }
    }
    runBlocking {
        val client = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 25565))
        println("client socket created")
        val input = client.openReadChannel()
        val mcInput = MinecraftReadChannel(input)
        println("read write channels opened")
        /*Handshake(VarInt(751), "127.0.0.1", 25565u, NextState.Login).send(output)
        Request().send(output)
        println("data sent?")
        println(Response().readFrom(input).jsonResponse)*/
        while (true) {
            val response = mcInput.readString(32767)
            //println("Server said: $response")
        }
    }
}