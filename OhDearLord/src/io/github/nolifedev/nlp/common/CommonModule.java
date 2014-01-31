package io.github.nolifedev.nlp.common;

import io.github.nolifedev.nlp.common.net.GameConnection;
import io.github.nolifedev.nlp.common.net.OpCodeSerialization;
import io.github.nolifedev.nlp.common.net.SocketGameConnection;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

public class CommonModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(EventBus.class).annotatedWith(Names.named("gamebus")).toInstance(
				new EventBus("Game Bus"));
		bind(EventBus.class).annotatedWith(Names.named("out")).toInstance(
				new EventBus("Outbound"));

		install(new FactoryModuleBuilder()
				.build(OpCodeSerialization.Factory.class));

		bind(GameConnection.class).to(SocketGameConnection.class);
	}

}
