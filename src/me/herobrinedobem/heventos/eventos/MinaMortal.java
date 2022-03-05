package me.herobrinedobem.heventos.eventos;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoCancellType;
import me.herobrinedobem.heventos.api.EventoUtils;
import me.herobrinedobem.heventos.api.events.StopEvent;
import me.herobrinedobem.heventos.eventos.listeners.MinaMortalListener;
import me.herobrinedobem.heventos.utils.BukkitEventHelper;
import me.herobrinedobem.heventos.utils.Cuboid;

public class MinaMortal extends EventoBaseAPI {

	private MinaMortalListener listener;
	private int tempoDeEvento, tempoDeEventoCurrent, tempoMensagens, tempoMensagensCurrent;

	@SuppressWarnings("deprecation")
	public MinaMortal(YamlConfiguration config) {
		super(config);
		listener = new MinaMortalListener();
		HEventos.getHEventos().getServer().getPluginManager().registerEvents(listener, HEventos.getHEventos());
		tempoDeEvento = config.getInt("Config.Evento_Tempo_Minutos") * 60;
		tempoMensagens = config.getInt("Config.Mensagens_Tempo_Minutos") * 60;
		tempoDeEventoCurrent = tempoDeEvento;
		tempoMensagensCurrent = tempoMensagens;
		Cuboid cubo = new Cuboid(EventoUtils.getLocation(getConfig(), "Localizacoes.Mina_1"),
				EventoUtils.getLocation(getConfig(), "Localizacoes.Mina_2"));
		ArrayList<String> blocosConfig = new ArrayList<>();
		for (String s : getConfig().getString("Config.Minerios").split(";")) {
			blocosConfig.add(s);
		}
		for (Block b : cubo.getBlocks()) {
			Random r = new Random();
			if (r.nextInt(100) <= getConfig().getInt("Config.Porcentagem_De_Minerios")) {
				String bloco = blocosConfig.get(r.nextInt(blocosConfig.size()));
				b.setType(Material.getMaterial(bloco));
			} else {
				b.setType(Material.STONE);
			}
		}
	}

	@Override
	public void startEventMethod() {
		for (Player p : getParticipantes()) {
			p.teleport(EventoUtils.getLocation(getConfig(), "Localizacoes.Entrada"));
		}
	}

	@Override
	public void scheduledMethod() {
		if (getParticipantes().size() >= 1) {
			if (tempoDeEventoCurrent > 0) {
				tempoDeEventoCurrent--;
				if (tempoMensagensCurrent == 0) {
					for (String s : getConfig().getStringList("Mensagens.Status")) {
						HEventos.getHEventos().getServer().broadcastMessage(s.replace("&", "§")
								.replace("$tempo$", tempoDeEventoCurrent + "").replace("$EventoName$", getNome()));
					}
					tempoMensagensCurrent = tempoMensagens;
				} else {
					tempoMensagensCurrent--;
				}
			} else {
				stopEvent();
			}
		} else {
			stopEvent();
		}
	}

	@Override
	public void stopEventMethod() {
		sendMessageList("Mensagens.Finalizado");
	}

	@Override
	public void cancelEventMethod() {
		sendMessageList("Mensagens.Cancelado");
	}

	@Override
	public void stopEvent() {
		StopEvent event = new StopEvent(HEventos.getHEventos().getEventosController().getEvento(),
				EventoCancellType.FINISHED);
		HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
	}

	@Override
	public void resetEvent() {
		super.resetEvent();
		BukkitEventHelper.unregisterEvents(listener, HEventos.getHEventos());
	}
}
