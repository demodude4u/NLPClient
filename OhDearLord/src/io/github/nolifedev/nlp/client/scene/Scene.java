package io.github.nolifedev.nlp.client.scene;

import io.github.nolifedev.nlp.common.event.net.op.OpPing;
import io.github.nolifedev.nlp.common.event.net.op.OpPong;

import java.awt.Graphics2D;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public abstract class Scene {

	private EventBus gameBus;
	private EventBus outBus;

	protected void onLoad(SceneContainer c) {

	}

	@Subscribe
	public void onPing(OpPing event) {
		getOutBus().post(new OpPong(event.getId()));
	}

	protected void onUnload(SceneContainer c) {
	}

	public abstract void render(SceneContainer c, Graphics2D g);

	@Inject
	public void setGameBus(@Named("gamebus") EventBus gameBus) {
		this.gameBus = gameBus;
		this.gameBus.register(this);
	}

	@Inject
	public void setOutboundBus(@Named("gamebus") EventBus outBus) {
		this.outBus = outBus;
	}

	public EventBus getOutBus() {
		return outBus;
	}

	public EventBus getGameBus() {
		return gameBus;
	}

	public abstract void tick(SceneContainer c, float timeSeconds);
}
