import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Tcp;
import akka.util.ByteString;

/**
 * Created by Artem Karpov
 */
public class TcpConsumerClient {


	private static final ActorSystem system = ActorSystem.create("TcpConsumerClient");
	private static final Materializer materializer = ActorMaterializer.create(system);


	private static final Function<String, ByteString> printMessage = text -> {
		System.out.println("Server message ->: " + text);
		return ByteString.fromString(text);
	};

	public static void main(String[] args) {
		//will listen and consume message from TcpConsumerServer at 127.0.0.1:8888
		final Flow<ByteString, ByteString, CompletionStage<Tcp.OutgoingConnection>> connection =
				Tcp.get(system).outgoingConnection("127.0.0.1", 8888);


		final Flow<ByteString, ByteString, NotUsed> repl = Flow.of(ByteString.class)
				.map(ByteString::utf8String)
				.map(printMessage::apply);

		connection.join(repl).run(materializer);
	}

}
