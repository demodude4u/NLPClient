package io.github.nolifedev.nlp.client.scene;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SceneLogin extends Scene {

	private final JPanel southPanel;

	@Inject
	public SceneLogin() {
		southPanel = constructSouthPanel();
	}

	private JPanel constructSouthPanel() {
		JPanel ret = new JPanel();
		ret.setBackground(Color.DARK_GRAY);

		JLabel lblConnecting = new JLabel("Connecting...");
		lblConnecting.setForeground(Color.RED);
		lblConnecting.setFont(new Font("Tahoma", Font.PLAIN, 20));
		ret.add(lblConnecting);

		{
			try {
				final URL url = new URL(
						"http://fc09.deviantart.net/fs70/f/2012/034/7/b/derpy_hooves_wip_by_anonycat-d4ojcin.gif");
				Icon icon = new ImageIcon(url);
				JLabel label = new JLabel(icon);
				label.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						try {
							Desktop.getDesktop()
									.browse(new URL(
											"http://www.deviantart.com/art/Derpy-Hooves-WIP-283078175")
											.toURI());
						} catch (IOException | URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
				});
				ret.add(label);

			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
		}

		return ret;
	}

	@Override
	protected void onLoad() {
		container.add(southPanel, BorderLayout.SOUTH);
		container.revalidate();
	}

	@Override
	protected void onUnload() {
		container.remove(southPanel);
		container.revalidate();
	}

	@Override
	public void render(Graphics2D g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	@Override
	public void tick(float timeSeconds) {
		// NOP
	}
}
