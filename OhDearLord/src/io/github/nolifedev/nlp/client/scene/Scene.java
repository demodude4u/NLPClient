package io.github.nolifedev.nlp.client.scene;

import java.awt.Graphics2D;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public abstract class Scene {

	private EventBus gameBus;
	private EventBus outBus;
	protected SceneContainer c;

	public EventBus getGameBus() {
		return gameBus;
	}

	public EventBus getOutBus() {
		return outBus;
	}

	protected void onLoad() {

	}

	protected void onUnload() {
	}

	public abstract void render(Graphics2D g);

	@Inject
	public void setGameBus(@Named("gamebus") EventBus gameBus) {
		this.gameBus = gameBus;
	}

	@Inject
	public void setOutboundBus(@Named("out") EventBus outBus) {
		this.outBus = outBus;
	}

	@Inject
	public void setSceneContainer(SceneContainer c) {
		this.c = c;
	}

	public abstract void tick(float timeSeconds);

}
