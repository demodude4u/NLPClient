package io.github.nolifedev.nlp.client.scene;

import io.github.nolifedev.nlp.client.Game;
import io.github.nolifedev.nlp.client.GamePlayerList;
import io.github.nolifedev.nlp.client.util.ImageLoader;
import io.github.nolifedev.nlp.client.util.ImageLoader.LoadingImage;
import io.github.nolifedev.nlp.common.event.net.op.Op0008LeaveJoinGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SceneGame extends Scene {

	private final Color bgColor = Color.darkGray;

	private final JPanel sidePanel;

	private final LoadingImage bgStarImgLoader;

	@Inject
	public SceneGame(Game game, GamePlayerList gamePlayerList,
			ImageLoader imageLoader) {
		sidePanel = constructSidePanel(game, gamePlayerList);

		bgStarImgLoader = imageLoader.load("http://i.imgur.com/9THsqJk.png");
	}

	private JPanel constructSidePanel(Game game, GamePlayerList gamePlayerList) {
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout());

		ret.add(gamePlayerList, BorderLayout.CENTER);
		ret.add(constructSouthPanel(game), BorderLayout.SOUTH);

		return ret;
	}

	private JPanel constructSouthPanel(Game game) {
		JPanel ret = new JPanel();

		JPanel panel = new JPanel();
		ret.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel panel_1 = new JPanel();
		panel_1.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_1.setBorder(new TitledBorder(null, "Game Details",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

		JLabel lblGame = new JLabel(game.toString());
		lblGame.setFont(new Font("Tahoma", Font.BOLD, 14));
		panel_1.add(lblGame);

		JButton btnLeaveGame = new JButton("Leave Game");
		panel.add(btnLeaveGame);
		btnLeaveGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getOutBus().post(
						new Op0008LeaveJoinGame(Optional.<Integer> absent()));
			}
		});

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
		BufferedImage bgStarImg = bgStarImgLoader.getImage();
		g.drawImage(bgStarImg,
				canvas.getWidth() / 2 - bgStarImg.getWidth() / 2,
				canvas.getHeight() / 2 - bgStarImg.getHeight() / 2, canvas);
	}

	@Override
	public void tick(float timeSeconds) {

	}

}
