package io.github.nolifedev.nlp;

import io.github.nolifedev.nlp.client.Client;
import io.github.nolifedev.nlp.client.ClientModule;
import io.github.nolifedev.nlp.common.CommonModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ClientMain {

	public static void main(String[] args) {

		Injector injector = Guice.createInjector(new CommonModule(),
				new ClientModule("retep998.no-ip.org", 273, "DeBrony"));

		injector.getInstance(Client.class);

	}

}
