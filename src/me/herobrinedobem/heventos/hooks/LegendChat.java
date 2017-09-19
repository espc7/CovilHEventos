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

	private Map<String, String> eventosUnicoVencedor = new HashMap<>();
	private Map<String, List<String>> eventosMultiVencedor = new HashMap<>();
	private Map<String, String> tags = new HashMap<>();

	public LegendChat() {
		carregarTags();
		carregarVencedores();
	}

	public void carregarVencedores() {
		File fileEvento = new File(HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Tags.yml");
		YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
		for (String filename : tags.keySet()) {
			if (configEvento.isString("Vencedores." + filename))
				eventosUnicoVencedor.put(filename, configEvento.getString("Vencedores." + filename));
			else {
				eventosMultiVencedor.put(filename, configEvento.getStringList("Vencedores." + filename));
			}
		}
	}

	public void carregarTags() {
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

	public boolean procurarListString(String e) {
		for (String fileName : eventosMultiVencedor.keySet()) {
			if (eventosMultiVencedor.get(fileName).contains(e)) {
				return true;
			}
		}
		return false;
	}

	@EventHandler
	private void onTimeWinEvent(TeamWinEvent e) {
		String fileName = HEventos.getHEventos().getEventosController().getFilename();
		List<String> players = new ArrayList<>();
		if (tags.containsKey(fileName)) {
			File fileEvento = new File(HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Tags.yml");
			YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
			for (Player player : e.getList()) {
				players.add(player.getPlayer().getName());
			}
			players.add("");
			eventosMultiVencedor.clear();
			eventosMultiVencedor.put(fileName, players);
			configEvento.set("Vencedores." + fileName, players);
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
		if (tags.containsKey(fileName) && !eventosMultiVencedor.containsKey(fileName)) {
			File fileEvento = new File(HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Tags.yml");
			YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
			configEvento.set("Vencedores." + fileName, e.getPlayer().getName());
			try {
				configEvento.save(fileEvento);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			eventosUnicoVencedor.put(fileName, e.getPlayer().getName());
		}
	}

	@EventHandler
	private void onChat(ChatMessageEvent e) {
		if (e.getTags().contains("heventos")) {
			boolean achou = procurarListString(e.getSender().getPlayer().getName());
			if (!eventosUnicoVencedor.containsValue(e.getSender().getPlayer().getName()) && !achou)
				return;
			StringBuilder sBuilder = new StringBuilder();
			if (eventosUnicoVencedor.containsValue(e.getSender().getPlayer().getName())) {
				for (Entry<String, String> es : eventosUnicoVencedor.entrySet()) {
					if (e.getSender().getName().equalsIgnoreCase(es.getValue())) {
						sBuilder.append(tags.get(es.getKey()));
					}
				}
			}
			if (achou) {
				for (String fileName : eventosMultiVencedor.keySet()) {
					if (eventosMultiVencedor.get(fileName).contains(e.getSender().getName())) {
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
