package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.client.event.HaveMyPlayer;
import io.github.nolifedev.nlp.client.scene.SceneContainer;
import io.github.nolifedev.nlp.common.ServiceHandler;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
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
			@Named("out") final EventBus outBus) {
		this.sceneContainer = sceneContainer;

		serviceHandler.add(this);

		gameBus.register(this);

		frame = constructFrame(sceneContainer, serverPlayerList, serverGameList);
		frame.setVisible(true);
	}

	private JFrame constructFrame(SceneContainer sceneContainer,
			ServerPlayerList serverPlayerList, ServerGameList serverGameList) {
		JFrame frame = new JFrame("NoLifePony Client");
		Container container = frame.getContentPane();
		container.setLayout(new BorderLayout());

		sceneContainer.setPreferredSize(new Dimension(512, 512));
		container.add(sceneContainer, BorderLayout.CENTER);

		JPanel rightPanel = constructRightPanel(serverPlayerList,
				serverGameList);
		container.add(rightPanel, BorderLayout.EAST);

		// FIXME need proper shutdown
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.pack();
		frame.setLocationRelativeTo(null);

		return frame;
	}

	private JPanel constructRightPanel(ServerPlayerList serverPlayerList,
			ServerGameList serverGameList) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1));

		panel.add(serverPlayerList);
		panel.add(serverGameList);

		return panel;
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
