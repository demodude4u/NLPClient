package io.github.nolifedev.nlp.client.scene;

import io.github.nolifedev.nlp.client.util.Maths;
import io.github.nolifedev.nlp.common.event.net.op.Op0003Nickname;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.inject.Inject;

public class SceneLogin extends Scene {

	private final JPanel southPanel;
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

	private final Random rand = new Random();

	@Inject
	public SceneLogin() {
		southPanel = constructSouthPanel();
	}

	private JPanel constructSouthPanel() {
		JPanel ret = new JPanel();

		JLabel lblNickname = new JLabel("Nickname:");
		ret.add(lblNickname);

		final JTextField txtNickname = new JTextField();
		ret.add(txtNickname);
		txtNickname.setColumns(10);

		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nickname = txtNickname.getText();
				if (nickname.isEmpty()) {
					JOptionPane.showMessageDialog(southPanel.getParent(),
							"Nickname is empty!");
					return;
				}
				getOutBus().post(new Op0003Nickname(nickname));
				c.loadScene(SceneLobby.class);
			}
		});
		ret.add(btnLogin);

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
