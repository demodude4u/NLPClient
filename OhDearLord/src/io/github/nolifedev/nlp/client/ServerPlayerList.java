package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.client.event.HaveMyPlayer;
import io.github.nolifedev.nlp.common.event.net.NetConnect;
import io.github.nolifedev.nlp.common.event.net.op.Op000BPlayersJoinedServer;
import io.github.nolifedev.nlp.common.event.net.op.Op000FPlayersLeftServer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class ServerPlayerList extends JPanel {
	private static final long serialVersionUID = -7350140641285651675L;

	private final EventBus outBus;

	private final Map<Integer, Player> mapPlayers = Maps.newLinkedHashMap();
	private Player myPlayer = null;

	private final JList<Player> listUI;

	private final TitledBorder titledBorder;

	private final MyPlayerIDLocator sessionIDLocator;

	private final EventBus gameBus;

	@Inject
	public ServerPlayerList(@Named("gamebus") EventBus gameBus,
			@Named("out") EventBus outBus, MyPlayerIDLocator sessionIDLocator,
			final Provider<ChatPanel> chatPanelProvider) {
		this.gameBus = gameBus;
		this.outBus = outBus;
		this.sessionIDLocator = sessionIDLocator;

		gameBus.register(this);

		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		titledBorder = new TitledBorder(null, "", TitledBorder.LEADING,
				TitledBorder.TOP, null, null);
		scrollPane.setViewportBorder(titledBorder);
		add(scrollPane);

		listUI = new JList<Player>();
		listUI.setForeground(Color.WHITE);
		listUI.setBackground(Color.DARK_GRAY);
		scrollPane.setViewportView(listUI);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(listUI, popupMenu);

		JButton btnPrivateMessage = new JButton("Private Message");
		btnPrivateMessage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ChatPanel chatPanel = chatPanelProvider.get();
				chatPanel.setPrivateTarget(listUI.getSelectedValue());
			}
		});
		popupMenu.add(btnPrivateMessage);

		updateListUI();
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
				listUI.clearSelection();
				listUI.setSelectedIndex(listUI.locationToIndex(e.getPoint()));
				if (!listUI.isSelectionEmpty()) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	public Map<Integer, Player> getMapPlayers() {
		return mapPlayers;
	}

	public Player getMyPlayer() {
		return myPlayer;
	}

	@Subscribe
	public void onConnect(NetConnect e) {
		mapPlayers.clear();
	}

	@Subscribe
	public void onJoined(Op000BPlayersJoinedServer e) {
		Set<Player> joinedPlayers = toPlayers(e.getPlayerIDNames());
		for (Player player : joinedPlayers) {
			mapPlayers.put(player.getID(), player);
		}
		updateListUI();
	}

	@Subscribe
	public void onLeft(Op000FPlayersLeftServer e) {
		Set<Player> leftPlayers = toPlayers(e.getPlayerIDs());
		for (Player player : leftPlayers) {
			mapPlayers.remove(player.getID());
		}
		updateListUI();
	}

	private Set<Player> toPlayers(Map<Integer, String> playerIDNames) {
		Set<Player> players = Sets.newLinkedHashSet();
		for (Entry<Integer, String> entry : playerIDNames.entrySet()) {
			Player player = new Player(entry.getKey(), entry.getValue());
			if (player.getID() == sessionIDLocator.getPlayerID()) {
				myPlayer = player;
				gameBus.post(new HaveMyPlayer(player));
			}
			players.add(player);
		}
		return players;
	}

	public Set<Player> toPlayers(Set<Integer> playerIDs) {
		Set<Player> players = Sets.newLinkedHashSet();
		for (Integer playerID : playerIDs) {
			Player player = mapPlayers.get(playerID);
			if (player != null) {
				players.add(player);
			} else {
				System.err.println("Unknown player ID! " + playerID);
			}
		}
		return players;
	}

	private void updateListUI() {
		listUI.setListData(mapPlayers.values().toArray(new Player[0]));
		titledBorder.setTitle("Players Online: " + mapPlayers.size());
		repaint();
	}

}
