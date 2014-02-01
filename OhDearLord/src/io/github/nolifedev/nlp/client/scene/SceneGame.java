package io.github.nolifedev.nlp.client.scene;

import io.github.nolifedev.nlp.client.scene.game.GamePlayerList;
import io.github.nolifedev.nlp.client.util.Maths;
import io.github.nolifedev.nlp.common.event.net.op.Op0008LeaveJoinGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.google.common.base.Optional;
import com.google.inject.Inject;

public class SceneGame extends Scene {

	private final JPanel southPanel;
	private final JPanel leftPanel;

	private final Random rand = new Random();

	private Color bgColor = Color.black;

	@Inject
	public SceneGame(GamePlayerList gamePlayerList) {
		southPanel = constructSouthPanel();
		leftPanel = constructLeftPanel(gamePlayerList);
	}

	private JPanel constructLeftPanel(GamePlayerList gamePlayerList) {
		JPanel ret = new JPanel();
		ret.setLayout(new GridLayout(1, 1));

		ret.add(gamePlayerList);

		return ret;
	}

	private JPanel constructSouthPanel() {
		JPanel ret = new JPanel();

		JButton btnLeaveGame = new JButton("Leave Game");
		btnLeaveGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getOutBus().post(
						new Op0008LeaveJoinGame(Optional.<Integer> absent()));
			}
		});
		ret.add(btnLeaveGame);

		return ret;
	}

	@Override
	protected void onLoad() {
		c.add(southPanel, BorderLayout.SOUTH);
		c.add(leftPanel, BorderLayout.WEST);
		c.revalidate();
	}

	@Override
	protected void onUnload() {
		c.remove(southPanel);
		c.remove(leftPanel);
		c.revalidate();
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
