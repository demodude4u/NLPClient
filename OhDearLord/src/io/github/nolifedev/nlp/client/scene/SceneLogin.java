package io.github.nolifedev.nlp.client.scene;

import io.github.nolifedev.nlp.common.event.net.op.OpNickname;

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

	private static Color interp(Color color1, Color color2, float f) {
		return new Color(interp(color1.getRed(), color2.getRed(), f), //
				interp(color1.getGreen(), color2.getGreen(), f), //
				interp(color1.getBlue(), color2.getBlue(), f), //
				interp(color1.getAlpha(), color2.getAlpha(), f));
	}

	private static float interp(float num1, float num2, float f) {
		return (num1 + (num2 - num1) * f);
	}

	private static int interp(int num1, int num2, float f) {
		return (int) (num1 + (num2 - num1) * f);
	}

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
				}
				getOutBus().post(new OpNickname(nickname));
			}
		});
		ret.add(btnLogin);

		return ret;
	}

	@Override
	protected void onLoad(SceneContainer c) {
		c.add(southPanel, BorderLayout.SOUTH);
	}

	@Override
	protected void onUnload(SceneContainer c) {
		c.remove(southPanel);
	}

	@Override
	public void render(SceneContainer c, Graphics2D g) {
		float x = bgInterpFX * c.getWidth();
		float y = bgInterpFY * c.getHeight();
		g.setPaint(new GradientPaint(new Point2D.Float(x, y), bgInterpColor1,
				new Point2D.Float(c.getWidth() - x, c.getHeight() - y),
				bgInterpColor2));
		g.fillRect(0, 0, c.getWidth(), c.getHeight());
	}

	@Override
	public void tick(SceneContainer c, float timeSeconds) {
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
		bgInterpColor1 = interp(bgInterpColor1, bgColor1, interpSpeed);
		bgInterpColor2 = interp(bgInterpColor2, bgColor2, interpSpeed);
		bgInterpFX = interp(bgInterpFX, bgFX, interpSpeed);
		bgInterpFY = interp(bgInterpFY, bgFY, interpSpeed);
	}
}
