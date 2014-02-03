package io.github.nolifedev.nlp.client.scene;

import io.github.nolifedev.nlp.client.scene.game.GamePlayerList;
import io.github.nolifedev.nlp.common.event.net.op.Op0008LeaveJoinGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.google.common.base.Optional;
import com.google.inject.Inject;

public class SceneGame extends Scene {

	private final Color bgColor = Color.darkGray;
	private BufferedImage bgStarImg;

	private final JPanel sidePanel;

	@Inject
	public SceneGame(GamePlayerList gamePlayerList) {
		sidePanel = constructSidePanel(gamePlayerList);

		try {
			bgStarImg = ImageIO.read(new URL("http://i.imgur.com/9THsqJk.png"));
		} catch (IOException e) {
			e.printStackTrace();
			bgStarImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		}
	}

	private JPanel constructSidePanel(GamePlayerList gamePlayerList) {
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout());

		ret.add(gamePlayerList, BorderLayout.CENTER);
		ret.add(constructSouthPanel(), BorderLayout.SOUTH);

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
		container.add(sidePanel, BorderLayout.EAST);
		container.revalidate();
	}

	@Override
	protected void onUnload() {
		container.remove(sidePanel);
		container.revalidate();
	}

	@Override
	public void render(Graphics2D g) {
		g.setColor(bgColor);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		g.drawImage(bgStarImg,
				canvas.getWidth() / 2 - bgStarImg.getWidth() / 2,
				canvas.getHeight() / 2 - bgStarImg.getHeight() / 2, canvas);
	}

	@Override
	public void tick(float timeSeconds) {

	}

}
