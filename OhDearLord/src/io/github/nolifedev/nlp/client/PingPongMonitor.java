package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.common.ServiceHandler;
import io.github.nolifedev.nlp.common.event.net.NetDisconnect;
import io.github.nolifedev.nlp.common.event.net.op.Op0001Ping;
import io.github.nolifedev.nlp.common.event.net.op.Op0002Pong;
import io.github.nolifedev.nlp.common.net.SocketConnection;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class PingPongMonitor extends AbstractScheduledService {

	private final SocketConnection socketConnection;
	private final EventBus gameBus;
	private final EventBus outBus;

	private final Random rand = new Random();

	private boolean missingPong = false;
	private int id = 0;

	@Inject
	public PingPongMonitor(ServiceHandler serviceHandler,
			SocketConnection socketConnection,
			@Named("gamebus") EventBus gameBus, @Named("out") EventBus outBus) {
		this.socketConnection = socketConnection;
		this.gameBus = gameBus;
		this.outBus = outBus;

		serviceHandler.add(this);
		gameBus.register(this);
	}

	@Subscribe
	public void onPong(Op0002Pong e) {
		if (missingPong && e.getID() == id) {
			missingPong = false;
		}
	}

	@Override
	protected void runOneIteration() throws Exception {
		if (missingPong) {
			gameBus.post(new NetDisconnect(true, "PingPong Timeout!"));
			socketConnection.connect();
		}
		missingPong = true;
		id = rand.nextInt();
		outBus.post(new Op0001Ping(id));
	}

	@Override
	protected Scheduler scheduler() {
		return Scheduler.newFixedRateSchedule(5, 5, TimeUnit.SECONDS);
	}

}
