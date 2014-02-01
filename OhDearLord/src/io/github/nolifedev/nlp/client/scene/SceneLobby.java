package io.github.nolifedev.nlp.client.scene;

import io.github.nolifedev.nlp.client.util.Maths;
import io.github.nolifedev.nlp.common.event.net.op.Op0003Nickname;
import io.github.nolifedev.nlp.common.event.net.op.Op0007CreateJoinGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.google.inject.Inject;

public class SceneLobby extends Scene {

	private final JPanel southPanel;

	private final Random rand = new Random();

	private Color bgColor1 = Color.red;
	private Color bgColor2 = Color.green;
	private Color bgInterpColor1 = bgColor1;
	private Color bgInterpColor2 = bgColor2;
	private float bgFX = 0f;
	private float bgFY = 0f;
	private float bgInterpFX = 0f;
	private float bgInterpFY = 0f;
	private float totalSeconds = 0;
	private float lastColorChangeSecond = 0;

	@Inject
	public SceneLobby() {
		southPanel = constructSouthPanel();
	}

	private JPanel constructSouthPanel() {
		JPanel ret = new JPanel();

		final JButton btnCreateGame = new JButton("Create Game");
		btnCreateGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newGameName = JOptionPane.showInputDialog(btnCreateGame,
						"Game name:", "Create Game",
						JOptionPane.OK_CANCEL_OPTION);
				if (newGameName == null || newGameName.isEmpty()) {
					return;
				}
				getOutBus().post(new Op0007CreateJoinGame(newGameName));
			}
		});
		ret.add(btnCreateGame);

		{
			URL url;
			try {
				url = new URL(
						"http://fc09.deviantart.net/fs71/i/2011/226/0/0/dj_pon_3_8_bit_by_ace12541-d46kwob.gif");
				Icon icon = new ImageIcon(url);
				JLabel label = new JLabel(icon);
				ret.add(label);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
		}

		JButton btnChangeNickname = new JButton("Change Nickname");
		btnChangeNickname.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newName = JOptionPane.showInputDialog(btnCreateGame,
						"New nickname:", "Change Nickname",
						JOptionPane.OK_CANCEL_OPTION);
				if (newName == null || newName.isEmpty()) {
					return;
				}
				getOutBus().post(new Op0003Nickname(newName));
			}
		});
		ret.add(btnChangeNickname);

		return ret;
	}

	@Override
	protected void onLoad() {
		c.add(southPanel, BorderLayout.SOUTH);
		c.revalidate();
	}

	@Override
	protected void onUnload() {
		c.remove(southPanel);
		c.revalidate();
	}

	@Override
	public void render(Graphics2D g) {
		float x = bgInterpFX * c.getWidth();
		float y = bgInterpFY * c.getHeight();
		g.setPaint(new GradientPaint(new Point2D.Float(x, y), bgInterpColor1,
				new Point2D.Float(c.getWidth() - x, c.getHeight() - y),
				bgInterpColor2));
		g.fillRect(0, 0, c.getWidth(), c.getHeight());
	}

	@Override
	public void tick(float timeSeconds) {
		totalSeconds += timeSeconds;
		if (totalSeconds - lastColorChangeSecond > 5) {
			lastColorChangeSecond = totalSeconds;
			int rgb = rand.nextInt();
			bgColor1 = new Color(rgb);
			bgColor2 = new Color(~rgb);
			if (rand.nextBoolean()) {
				bgFX = bgFX < 0.5f ? 1f : 0f;
			} else {
				bgFY = bgFY < 0.5f ? 1f : 0f;
			}
		}

		float interpSpeed = 0.025f;
		bgInterpColor1 = Maths.interp(bgInterpColor1, bgColor1, interpSpeed);
		bgInterpColor2 = Maths.interp(bgInterpColor2, bgColor2, interpSpeed);
		bgInterpFX = Maths.interp(bgInterpFX, bgFX, interpSpeed);
		bgInterpFY = Maths.interp(bgInterpFY, bgFY, interpSpeed);
	}

}
