package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.common.event.net.op.Op0005DeletedGames;
import io.github.nolifedev.nlp.common.event.net.op.Op0006CreatedGames;
import io.github.nolifedev.nlp.common.event.net.op.Op0008LeaveJoinGame;

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

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class ServerGameList extends JPanel {
	private static final long serialVersionUID = -7350140641285651675L;

	private final EventBus outBus;

	private final Map<Integer, Game> mapGames = Maps.newLinkedHashMap();

	private final JList<Game> listUI;

	private final TitledBorder titledBorder;

	@Inject
	public ServerGameList(@Named("gamebus") EventBus gameBus,
			@Named("out") final EventBus outBus) {
		this.outBus = outBus;

		gameBus.register(this);

		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		titledBorder = new TitledBorder(null, "", TitledBorder.LEADING,
				TitledBorder.TOP, null, null);
		scrollPane.setViewportBorder(titledBorder);
		add(scrollPane);

		listUI = new JList<Game>();
		listUI.setForeground(Color.WHITE);
		listUI.setBackground(Color.DARK_GRAY);
		scrollPane.setViewportView(listUI);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(listUI, popupMenu);

		JButton btnJoinGame = new JButton("Join Game");
		btnJoinGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Game game = listUI.getSelectedValue();
				if (game != null) {
					outBus.post(new Op0008LeaveJoinGame(Optional.of(game
							.getID())));
				}
			}
		});
		popupMenu.add(btnJoinGame);

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

	public Map<Integer, Game> getMapGames() {
		return mapGames;
	}

	@Subscribe
	public void onCreated(Op0006CreatedGames e) {
		Set<Game> createdGames = toGames(e.getGameIDNames());
		for (Game game : createdGames) {
			mapGames.put(game.getID(), game);
		}
		updateListUI();
	}

	@Subscribe
	public void onDeleted(Op0005DeletedGames e) {
		Set<Game> leftGames = toGames(e.getGameIDs());
		for (Game game : leftGames) {
			mapGames.remove(game.getID());
		}
		updateListUI();
	}

	private Set<Game> toGames(Map<Integer, String> gameIDNames) {
		Set<Game> games = Sets.newLinkedHashSet();
		for (Entry<Integer, String> entry : gameIDNames.entrySet()) {
			games.add(new Game(entry.getKey(), entry.getValue()));
		}
		return games;
	}

	private Set<Game> toGames(Set<Integer> gameIDs) {
		Set<Game> games = Sets.newLinkedHashSet();
		for (Integer gameID : gameIDs) {
			Game game = mapGames.get(gameID);
			if (game != null) {
				games.add(game);
			} else {
				System.err.println("Unknown game ID! " + gameID);
			}
		}
		return games;
	}

	private void updateListUI() {
		listUI.setListData(mapGames.values().toArray(new Game[0]));
		titledBorder.setTitle("Games: " + mapGames.size());
		repaint();
	}
}
