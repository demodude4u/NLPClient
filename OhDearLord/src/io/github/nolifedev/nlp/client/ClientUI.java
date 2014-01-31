package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.client.scene.SceneContainer;
import io.github.nolifedev.nlp.common.ServiceHandler;

import java.awt.Container;
import java.awt.Dimension;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;

public class ClientUI extends AbstractScheduledService {

	private static final float SecondsPerFrame = 1f / 30f;

	private final JFrame frame;
	private final SceneContainer sceneContainer;

	@Inject
	public ClientUI(ServiceHandler serviceHandler, SceneContainer sceneContainer) {
		this.sceneContainer = sceneContainer;

		serviceHandler.add(this);

		frame = constructFrame(sceneContainer);
		frame.setVisible(true);
	}

	private JFrame constructFrame(SceneContainer sceneContainer) {
		JFrame frame = new JFrame("NoLifePony Client");
		Container container = frame.getContentPane();

		sceneContainer.setPreferredSize(new Dimension(512, 512));
		container.add(sceneContainer);

		// FIXME need proper shutdown
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.pack();
		frame.setLocationRelativeTo(null);

		return frame;
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
