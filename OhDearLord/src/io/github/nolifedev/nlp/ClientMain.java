package io.github.nolifedev.nlp;

import io.github.nolifedev.nlp.client.Client;
import io.github.nolifedev.nlp.client.ClientModule;
import io.github.nolifedev.nlp.common.CommonModule;
import io.github.nolifedev.nlp.common.event.NLPEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

public class ClientMain {

	private static class DebugEvents {
		@Inject
		public DebugEvents(@Named("gamebus") EventBus gameBus,
				@Named("out") EventBus outBus) {
			gameBus.register(new Object() {
				@Subscribe
				public void onEvent(NLPEvent e) {
					System.out.println("GAME <<<< " + e);
				}
			});
			outBus.register(new Object() {
				@Subscribe
				public void onEvent(NLPEvent e) {
					System.out.println("OUT  >>>> " + e);
				}
			});
		}
	}

	public static void main(String[] args) {

		Injector injector = Guice.createInjector(new CommonModule(),
				new ClientModule("retep998.no-ip.org", 273, "DeBrony"));

		injector.getInstance(DebugEvents.class);
		injector.getInstance(Client.class);

	}

}
