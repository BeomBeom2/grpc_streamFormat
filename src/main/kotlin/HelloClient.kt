import com.snowdeer.Hello
import com.snowdeer.HelloServiceGrpc
import io.grpc.ManagedChannelBuilder


fun main() {

    println("[GRPC] main()")
    val channel = ManagedChannelBuilder
        .forAddress("localhost", 10004)
        .usePlaintext()
        .build()

    val stub = HelloServiceGrpc.newBlockingStub(channel)
    val response = stub.sayHello(getHelloRequest("hello. snowdeer"))

    println("[GRPC] response(${response.reply})")
}

fun getHelloRequest(greeting: String): Hello.HelloRequest {
    return Hello.HelloRequest.newBuilder()
        .setGreeting(greeting)
        .build()
}
