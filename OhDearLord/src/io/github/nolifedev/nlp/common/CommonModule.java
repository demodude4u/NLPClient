package io.github.nolifedev.nlp.common;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class CommonModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(EventBus.class).annotatedWith(Names.named("gamebus")).toInstance(
				new EventBus("Game Bus"));
		bind(EventBus.class).annotatedWith(Names.named("out")).toInstance(
				new EventBus("Outbound"));
	}

}
