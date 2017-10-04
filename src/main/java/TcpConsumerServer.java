import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Tcp;
import akka.util.ByteString;
import scala.concurrent.duration.FiniteDuration;

/**
 * Created by Artem Karpov
 */
public class TcpConsumerServer {


	private static final ActorSystem system = ActorSystem.create("QuickStart2");
	private static final Materializer materializer = ActorMaterializer.create(system);

	private static final FiniteDuration DELAY_ONE_SECOND = FiniteDuration.apply(1, TimeUnit.SECONDS);
	private static final String MESSAGE = "I'm the Data!";


	public static void main(String[] args) {

		//TcpConsumerServer will throw data on 127.0.0.1:8888
		final Source<Tcp.IncomingConnection, CompletionStage<Tcp.ServerBinding>> connections =
				Tcp.get(system).bind("127.0.0.1", 8888);

		connections.runForeach(connection -> {
			System.out.println("New connection from: " + connection.remoteAddress());

			  Source.tick(DELAY_ONE_SECOND, DELAY_ONE_SECOND, MESSAGE)
					.map(ByteString::fromString).via(connection.flow())
					.to(Sink.ignore()).run(materializer);
		}, materializer);
	}
}
