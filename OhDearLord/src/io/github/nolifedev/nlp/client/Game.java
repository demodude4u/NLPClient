package io.github.nolifedev.nlp.client;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = { "name" })
public class Game {
	private final int ID;
	private final String name;
}
