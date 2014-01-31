package io.github.nolifedev.nlp.client.scene;

import io.github.nolifedev.nlp.client.util.Maths;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import com.google.inject.Inject;

public class SceneLobby extends Scene {

	private final Random rand = new Random();

	private Color bgColor = Color.black;

	@Inject
	public SceneLobby() {
	}

	@Override
	public void render(Graphics2D g) {
		g.setColor(bgColor);
		g.fillRect(0, 0, c.getWidth(), c.getHeight());
	}

	@Override
	public void tick(float timeSeconds) {
		Color target = new Color(rand.nextInt());
		bgColor = Maths.interp(bgColor, target, 0.1f);
	}

}
