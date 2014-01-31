package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.client.scene.Scene;
import io.github.nolifedev.nlp.client.scene.SceneLogin;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public class ClientModule extends AbstractModule {

	private final String host;
	private final int port;
	private final String nickname;

	public ClientModule(String host, int port, String nickname) {
		this.host = host;
		this.port = port;
		this.nickname = nickname;
	}

	@Override
	protected void configure() {
		bindConstant().annotatedWith(Names.named("host")).to(host);
		bindConstant().annotatedWith(Names.named("port")).to(port);
		bindConstant().annotatedWith(Names.named("nickname")).to(nickname);

		bind(new TypeLiteral<Class<? extends Scene>>() {
		}).annotatedWith(Names.named("init")).toInstance(SceneLogin.class);
	}
}
