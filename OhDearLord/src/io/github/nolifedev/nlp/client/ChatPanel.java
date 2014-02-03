package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.common.event.net.op.Op000AMakeGlobalChatMessage;
import io.github.nolifedev.nlp.common.event.net.op.Op000EGlobalChatMessages;
import io.github.nolifedev.nlp.common.event.net.op.Op0010MakeGameChatMessage;
import io.github.nolifedev.nlp.common.event.net.op.Op0011GameChatMessages;
import io.github.nolifedev.nlp.common.event.net.op.Op0012MakePrivateChatMessage;
import io.github.nolifedev.nlp.common.event.net.op.Op0013PrivateChatMessages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class ChatPanel extends JPanel {
	private interface ChatMode {
		public void selected();

		public boolean send(String message);
	}

	private static final long serialVersionUID = -7391652869233264884L;

	private final JTextField textField;

	private final JEditorPane editorPane;
	private JPopupMenu popupMenu;
	private JButton btnClear;

	private JPanel panel;
	private JComboBox<ChatMode> comboBox;

	private Player target = null;

	private final ServerPlayerList serverPlayerList;

	@Inject
	public ChatPanel(@Named("gamebus") EventBus gameBus,
			@Named("out") final EventBus outBus,
			final MyGameLocator myGameLocator,
			final ServerPlayerList serverPlayerList,
			final MyPlayerLocator myPlayerLocator) {
		this.serverPlayerList = serverPlayerList;

		gameBus.register(this);

		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, BorderLayout.CENTER);

		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setForeground(Color.ORANGE);
		editorPane.setBackground(Color.BLACK);
		scrollPane.setViewportView(editorPane);

		popupMenu = new JPopupMenu();
		addPopup(editorPane, popupMenu);

		btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearMessages();
			}
		});
		popupMenu.add(btnClear);

		panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		comboBox = new JComboBox<>();
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = comboBox.getSelectedIndex();
				if (selectedIndex != -1) {
					ChatMode chatMode = comboBox.getItemAt(selectedIndex);
					chatMode.selected();
				}
			}
		});
		comboBox.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				// NOP
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// NOP
			}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				target = null;
			}
		});
		comboBox.addItem(new ChatMode() {
			@Override
			public void selected() {
			}

			@Override
			public boolean send(String message) {
				outBus.post(new Op000AMakeGlobalChatMessage(message));
				return true;
			}

			@Override
			public String toString() {
				return "Global";
			}
		});
		comboBox.addItem(new ChatMode() {
			@Override
			public void selected() {
				if (!myGameLocator.isInGame()) {
					JOptionPane.showMessageDialog(ChatPanel.this,
							"You are not currently in a game!");
					comboBox.setSelectedIndex(0);
					return;
				}
			}

			@Override
			public boolean send(String message) {
				if (!myGameLocator.isInGame()) {
					JOptionPane.showMessageDialog(ChatPanel.this,
							"You are not currently in a game!");
					return false;
				}
				outBus.post(new Op0010MakeGameChatMessage(message));
				return true;
			}

			@Override
			public String toString() {
				return "Game";
			}
		});

		comboBox.addItem(new ChatMode() {
			@Override
			public void selected() {
				if (target != null) {
					return;
				}

				Set<Player> players = Sets.newLinkedHashSet(serverPlayerList
						.getMapPlayers().values());
				players.remove(myPlayerLocator.getPlayer());

				if (players.isEmpty()) {
					JOptionPane.showMessageDialog(ChatPanel.this,
							"There are no other players online to message!");
					comboBox.setSelectedIndex(0);
					return;
				}

				Player chosenPlayer = (Player) JOptionPane.showInputDialog(
						ChatPanel.this, "Private message to:",
						"Pick Private Message Target",
						JOptionPane.QUESTION_MESSAGE, null, players.toArray(),
						players.iterator().next());
				if (chosenPlayer == null) {
					comboBox.setSelectedIndex(0);
					return;
				}

				target = chosenPlayer;
			}

			@Override
			public boolean send(String message) {
				if (target == null) {
					return false;
				}

				if (serverPlayerList.getMapPlayers().get(target.getID()) == null) {
					JOptionPane.showMessageDialog(ChatPanel.this, target
							+ " is not online anymore!");
					return false;
				}

				outBus.post(new Op0012MakePrivateChatMessage(target.getID(),
						message));
				return true;
			}

			@Override
			public String toString() {
				return target == null ? "Pony" : target.toString();
			}
		});
		panel.add(comboBox, BorderLayout.WEST);

		textField = new JTextField();
		panel.add(textField);
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = textField.getText();
				if (message.isEmpty()) {
					return;
				}
				int selectedIndex = comboBox.getSelectedIndex();
				if (selectedIndex == -1) {
					return;
				}
				comboBox.getItemAt(selectedIndex).send(message);
				textField.setText("");
			}
		});
		textField.setForeground(Color.WHITE);
		textField.setBackground(Color.DARK_GRAY);
		textField.setColumns(10);
	}

	private void addFormattedMessage(String type, Player player, String message) {
		addMessage("[" + type + "] "
				+ (player == null ? "SERVER" : player.toString()) + ": "
				+ message);
	}

	private void addFormattedMessageHTML(String htmlColor, String type,
			Player player, String message) {
		addMessage("<font color="
				+ htmlColor
				+ ">"
				+ "<b>["
				+ type
				+ "] </b>"
				+ "<u>"
				+ (player == null ? "<font color=red><b>SERVER<b></font>"
						: player.toString()) + "</u>: " + message
				+ "</font><br>");
	}

	public synchronized void addMessage(String message) {
		editorPane.setText(editorPane.getText() + message + "\n");// XXX Can be
																	// better
	}

	private void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	public synchronized void clearMessages() {
		editorPane.setText("");
	}

	@Subscribe
	public void onGameMessages(Op0011GameChatMessages e) {
		for (Entry<Integer, String> entry : e.getPlayerIDMessages()) {
			Player player = serverPlayerList.getMapPlayers()
					.get(entry.getKey());
			addFormattedMessage("GAME", player, entry.getValue());
		}
	}

	@Subscribe
	public void onGlobalMessages(Op000EGlobalChatMessages e) {
		for (Entry<Integer, String> entry : e.getPlayerIDMessages()) {
			Player player = serverPlayerList.getMapPlayers()
					.get(entry.getKey());
			addFormattedMessage("GLOBAL", player, entry.getValue());
		}
	}

	@Subscribe
	public void onPrivateMessages(Op0013PrivateChatMessages e) {
		for (Entry<Integer, String> entry : e.getPlayerIDMessages()) {
			Player player = serverPlayerList.getMapPlayers()
					.get(entry.getKey());
			addFormattedMessage("PRIVATE", player, entry.getValue());
		}
	}

	public void setPrivateTarget(Player player) {
		target = player;
		textField.requestFocus();
		textField.selectAll();
	}
}
