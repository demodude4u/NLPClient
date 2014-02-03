package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.client.event.HaveMyPlayer;
import io.github.nolifedev.nlp.client.scene.SceneContainer;
import io.github.nolifedev.nlp.common.ServiceHandler;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class ClientUI extends AbstractScheduledService {

	private static final float SecondsPerFrame = 1f / 30f;

	private final JFrame frame;
	private final SceneContainer sceneContainer;

	@Inject
	public ClientUI(ServiceHandler serviceHandler,
			SceneContainer sceneContainer, ServerPlayerList serverPlayerList,
			ServerGameList serverGameList,
			@Named("gamebus") final EventBus gameBus,
			@Named("out") final EventBus outBus, ChatPanel chatPanel) {
		this.sceneContainer = sceneContainer;

		serviceHandler.add(this);

		gameBus.register(this);

		frame = constructFrame(sceneContainer, serverPlayerList, chatPanel);
		// FIXME need proper shutdown
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private JPanel constructBottomPanel(ServerPlayerList serverPlayerList,
			ChatPanel chatPanel) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		panel.add(chatPanel, BorderLayout.CENTER);
		panel.add(serverPlayerList, BorderLayout.EAST);

		return panel;
	}

	private JFrame constructFrame(SceneContainer sceneContainer,
			ServerPlayerList serverPlayerList, ChatPanel chatPanel) {
		JFrame frame = new JFrame("NoLifePony Client");
		Container container = frame.getContentPane();
		container.setLayout(new BorderLayout());

		sceneContainer.setPreferredSize(new Dimension(1024, 512));
		container.add(sceneContainer, BorderLayout.CENTER);

		JPanel bottomPanel = constructBottomPanel(serverPlayerList, chatPanel);
		container.add(bottomPanel, BorderLayout.SOUTH);

		return frame;
	}

	@Subscribe
	public void onHaveMyPlayer(HaveMyPlayer e) {
		frame.setTitle("NoLifePony -- " + e.getPlayer());
	}

	@Override
	protected void runOneIteration() throws Exception {
		sceneContainer.tickScene(SecondsPerFrame);
		sceneContainer.renderScene();
	}

	@Override
	protected Scheduler scheduler() {
		return Scheduler.newFixedRateSchedule(1000,
				(long) (SecondsPerFrame * 1000f), TimeUnit.MILLISECONDS);
	}

}
