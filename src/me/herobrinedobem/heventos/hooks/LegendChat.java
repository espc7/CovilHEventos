package me.herobrinedobem.heventos.hooks;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	private Map<String, Object> eventos = new HashMap<>();
	private Set<String> nomeEventosList = new HashSet<>();

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
				eventos.put(filename, configEvento.getStringList("Vencedores." + filename));
				nomeEventosList.add(filename);
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

	public boolean procurarListString(Player e) {
		for (String fileName : nomeEventosList) {
			if (eventos.get(fileName).toString().contains(e.getName() + "]")
					|| eventos.get(fileName).toString().contains(e.getName() + ",")) {
				return true;
			}
		}
		return false;
	}

	@EventHandler
	private void onTimeWinEvent(TeamWinEvent e) {
		String fileName = HEventos.getHEventos().getEventosController().getFilename();
		if (tags.containsKey(fileName)) {
			File fileEvento = new File(HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Tags.yml");
			YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
			eventos.put(fileName, e.getList());
			configEvento.set("Vencedores." + fileName, e.getList());
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
		if (e.getTags().contains("heventos")) {
			String playerName = e.getSender().getName();
			boolean achou = procurarListString(e.getSender());
			if (!eventos.containsValue(playerName) && !achou)
				return;
			StringBuilder sBuilder = new StringBuilder();
			if (eventos.containsValue(playerName)) {
				for (Entry<String, Object> es : eventos.entrySet()) {
					if (es.getValue() instanceof String) {
						if (e.getSender() == HEventos.getHEventos().getServer().getPlayer((String) es.getValue())) {
							sBuilder.append(tags.get(es.getKey()));
						}
					}
				}
			}
			if (achou) {
				for (String fileName : nomeEventosList) {
					if (eventos.get(fileName).toString().contains(e.getSender().getName() + "]")
							|| eventos.get(fileName).toString().contains(e.getSender().getName() + ",")) {
						sBuilder.append(tags.get(fileName));
					}
				}
			}
			if (sBuilder != null) {
				e.setTagValue("heventos", sBuilder.toString());
			}
		}
	}
}
