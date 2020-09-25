package protocol

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import kotlinx.coroutines.asCoroutineDispatcher
import protocol.packets.client.handshake.Handshake
import protocol.packets.client.handshake.NextState
import protocol.packets.client.handshake.Request
import protocol.packets.server.handshake.Response
import java.net.InetSocketAddress
import java.util.concurrent.Executors

@ExperimentalUnsignedTypes
@KtorExperimentalAPI
suspend fun protocolTest() {val exec = Executors.newCachedThreadPool()
    val selector = ActorSelectorManager(exec.asCoroutineDispatcher())
    val tcpSocketBuilder = aSocket(selector).tcp()
    val client = tcpSocketBuilder.connect(InetSocketAddress("127.0.0.1", 25565))
    println("client socket created")
    val readChannel = client.openReadChannel()
    val writeChannel = client.openWriteChannel(true)
    println("read write channels opened")
    Handshake(751, "127.0.0.1", 25565u, NextState.Login).send(writeChannel)
    Request().send(writeChannel)
    println("data sent?")
    println(Response().readFrom(readChannel).jsonResponse)
}