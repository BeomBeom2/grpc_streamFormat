import io.grpc.stub.StreamObserver
import com.snowdeer.Hello
import com.snowdeer.HelloServiceGrpc
import io.grpc.ServerBuilder

fun main() {
    println("in main()")
    val service = HelloService()
    val server = ServerBuilder
        .forPort(10004) // ServerBuilder.forPort()에서  ProviderNotFoundException 발생, grpc-netty or grpc-netty-shaded를 종속성에 추가
        .addService(HelloService())
        .build()

    println("[GRPC] server starts()")
    server.start()
    server.awaitTermination()
}

class HelloService : HelloServiceGrpc.HelloServiceImplBase() {
    override fun sayHello(request: Hello.HelloRequest?, responseObserver: StreamObserver<Hello.HelloResponse>?) {
        println("sayHello(${request?.greeting}) method")

        val response = Hello.HelloResponse.newBuilder().setReply(request?.greeting).build()
        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }

    override fun lotsOfReplies(request: Hello.HelloRequest?, responseObserver: StreamObserver<Hello.HelloResponse>?) {
        println("[GRPC, Server] lotsOfReplies()")

        for(i in 0 until 5) {
            val resp = Hello.HelloResponse.newBuilder()
                .setReply("hello - $i")
                .build()
            responseObserver?.onNext(resp)
            println("response $i to Client")
            Thread.sleep(1000)
        }
        responseObserver?.onCompleted()

        println("finished")
    }

    override fun lotsOfGreetings(responseObserver: StreamObserver<Hello.HelloResponse>?): StreamObserver<Hello.HelloRequest> {
        return object : StreamObserver<Hello.HelloRequest> {

            override fun onNext(value: Hello.HelloRequest?) {
                println("[GRPC, Server] lotsOfGreetings() - onNext(${value?.greeting})")
            }

            override fun onError(t: Throwable?) {
                println("[GRPC, Server] lotsOfGreetings() - onError()")
            }

            override fun onCompleted() {
                println("[GRPC, Server] lotsOfGreetings() - onCompleted()")

                val response = Hello.HelloResponse.newBuilder().setReply("lotsOfGreetings is completed").build()
                responseObserver?.onNext(response)
                responseObserver?.onCompleted()
            }
        }
    }

    override fun bidiHello(responseObserver: StreamObserver<Hello.HelloResponse>?): StreamObserver<Hello.HelloRequest> {
        return object : StreamObserver<Hello.HelloRequest> {

            override fun onNext(value: Hello.HelloRequest?) {
                println("[GRPC, Server] bidiHello() - onNext(${value?.greeting})")

                val resp = Hello.HelloResponse.newBuilder()
                    .setReply("Response to Client - (${value?.greeting})")
                    .build()
                responseObserver?.onNext(resp)
            }

            override fun onError(t: Throwable?) {
                println("[GRPC, Server] bidiHello() - onError()")
            }

            override fun onCompleted() {
                println("[GRPC, Server] bidiHello() - onCompleted()")
                responseObserver?.onCompleted()
            }
        }
    }
}

