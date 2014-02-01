package io.github.nolifedev.nlp.client.event;

import io.github.nolifedev.nlp.client.Player;
import lombok.Data;

@Data
public class HaveMyPlayer implements ClientEvent {
	private final Player player;
}
