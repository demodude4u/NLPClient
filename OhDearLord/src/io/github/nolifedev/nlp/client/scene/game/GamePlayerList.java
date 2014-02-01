package io.github.nolifedev.nlp.client.scene.game;

import io.github.nolifedev.nlp.client.Game;
import io.github.nolifedev.nlp.client.Player;
import io.github.nolifedev.nlp.client.ServerPlayerList;
import io.github.nolifedev.nlp.common.event.net.op.Op000CPlayersJoinedGame;
import io.github.nolifedev.nlp.common.event.net.op.Op000DPlayersLeftGame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class GamePlayerList extends JPanel {
	private static final long serialVersionUID = -7350140641285651675L;

	private final EventBus outBus;

	private final Set<Player> players = Sets.newLinkedHashSet();

	private final JList<Player> listUI;
	private final TitledBorder titledBorder;

	private final ServerPlayerList serverPlayerList;

	@Inject
	public GamePlayerList(Game game, @Named("gamebus") EventBus gameBus,
			@Named("out") EventBus outBus, ServerPlayerList serverPlayerList) {
		this.outBus = outBus;
		this.serverPlayerList = serverPlayerList;

		gameBus.register(this);

		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		titledBorder = new TitledBorder(null, "", TitledBorder.LEADING,
				TitledBorder.TOP, null, null);
		scrollPane.setViewportBorder(titledBorder);
		add(scrollPane);

		listUI = new JList<Player>();
		scrollPane.setViewportView(listUI);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(listUI, popupMenu);

		JButton btnNop = new JButton("NOP");
		popupMenu.add(btnNop);

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

	@Subscribe
	public void onJoined(Op000CPlayersJoinedGame e) {
		Set<Player> joinedPlayers = serverPlayerList
				.toPlayers(e.getPlayerIDs());
		players.addAll(joinedPlayers);
		updateListUI();
	}

	@Subscribe
	public void onLeft(Op000DPlayersLeftGame e) {
		Set<Player> leftPlayers = serverPlayerList.toPlayers(e.getPlayerIDs());
		players.removeAll(leftPlayers);
		updateListUI();
	}

	private void updateListUI() {
		listUI.setListData(players.toArray(new Player[0]));
		titledBorder.setTitle("Players In Game: " + players.size());
		repaint();
	}
}
