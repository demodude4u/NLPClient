package io.github.nolifedev.nlp.client.scene;

import io.github.nolifedev.nlp.client.event.HaveMyPlayer;
import io.github.nolifedev.nlp.common.event.net.op.Op0003Nickname;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class SceneLogin extends Scene {

	private final JPanel southPanel;

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
		ActionListener loginActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nickname = txtNickname.getText();
				if (nickname.isEmpty()) {
					JOptionPane.showMessageDialog(southPanel.getParent(),
							"Nickname is empty!");
					return;
				}
				getOutBus().post(new Op0003Nickname(nickname));
			}
		};
		txtNickname.addActionListener(loginActionListener);
		ret.add(txtNickname);
		txtNickname.setColumns(10);

		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(loginActionListener);
		ret.add(btnLogin);

		return ret;
	}

	@Subscribe
	public void onHaveMyPlayer(HaveMyPlayer e) {
		c.loadScene(SceneLobby.class);
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
		g.setColor(Color.black);
		g.fillRect(0, 0, c.getWidth(), c.getHeight());
	}

	@Override
	public void tick(float timeSeconds) {
		// NOP
	}
}
