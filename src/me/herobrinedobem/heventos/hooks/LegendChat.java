package me.herobrinedobem.heventos.hooks;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.events.PlayerWinEvent;
import me.herobrinedobem.heventos.api.events.TeamWinEvent;

public class LegendChat implements Listener {

	private Map<String, String> tags;
	private Map<String, String> eventos = new HashMap<>();
	private List<String> paintball = new ArrayList<>();
	private String paintballString;

	public LegendChat() {
		carregarTags();
		carregarVencedores();
	}

	public void carregarVencedores() {
		File fileEvento = new File(HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Tags.yml");
		YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
		for (String filename : tags.keySet()) {
			if (!configEvento.isList("Vencedores." + filename))
				eventos.put(filename, configEvento.getString("Vencedores." + filename));
			else {
				paintballString = filename;
				paintball.clear();
				paintball.addAll(configEvento.getStringList("Vencedores." + filename));
			}
		}
	}

	public void carregarTags() {
		tags = new HashMap<>();
		Path dir = Paths.get(HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Eventos");
		File fileEvento = new File(HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Tags.yml");
		YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path path : stream) {
				if (path.getFileName().toString().contains(".yml")) {
					if (configEvento.contains("Tags."
							+ path.getFileName().toString().substring(0, path.getFileName().toString().length() - 4))) {
						tags.put(path.getFileName().toString().substring(0, path.getFileName().toString().length() - 4),
								configEvento.getString("Tags." + path.getFileName().toString().substring(0,
										path.getFileName().toString().length() - 4)));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	private void onTimeWinEvent(TeamWinEvent e) {
		String fileName = HEventos.getHEventos().getEventosController().getFilename();
		if (tags.containsKey(fileName)) {
			this.paintballString = fileName;
			paintball.clear();
			File fileEvento = new File(HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Tags.yml");
			YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
			for (Player player : e.getList()) {
				paintball.add(player.getName());
			}
			configEvento.set("Vencedores." + fileName, paintball);
			try {
				configEvento.save(fileEvento);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@EventHandler
	private void onEventoPlayerWinEvent(PlayerWinEvent e) {
		String fileName = HEventos.getHEventos().getEventosController().getFilename();
		if (tags.containsKey(fileName)) {
			File fileEvento = new File(HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Tags.yml");
			YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
			configEvento.set("Vencedores." + fileName, e.getPlayer().getName());
			try {
				configEvento.save(fileEvento);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			eventos.put(fileName, e.getPlayer().getName());
		}
	}

	@EventHandler
	private void onChat(ChatMessageEvent e) {
		String playerName = e.getSender().getName();
		if (!eventos.containsValue(playerName) && !paintball.contains(playerName))
			return;
		if (e.getTags().contains("heventos")) {
			StringBuilder sBuilder = new StringBuilder();
			if (eventos.containsValue(playerName)) {
				for (Entry<String, String> es : eventos.entrySet()) {
					if (e.getSender() == HEventos.getHEventos().getServer().getPlayer(es.getValue())) {
						sBuilder.append(tags.get(es.getKey()));
					}
				}
			}
			if (paintball.contains(playerName)) {
				sBuilder.append(tags.get(paintballString));
			}
			if (sBuilder != null) {
				e.setTagValue("heventos", sBuilder.toString());
			}
		}
	}
}
