package io.github.nolifedev.nlp.common.event.net;

import lombok.Data;

@Data
public class NetConnect implements NetEvent {
	private final String host;
	private final int port;
}
