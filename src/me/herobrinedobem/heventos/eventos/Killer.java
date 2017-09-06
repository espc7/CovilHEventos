package me.herobrinedobem.heventos.eventos;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoCancellType;
import me.herobrinedobem.heventos.api.EventoUtils;
import me.herobrinedobem.heventos.api.events.PlayerWinEvent;
import me.herobrinedobem.heventos.api.events.StopEvent;
import me.herobrinedobem.heventos.eventos.listeners.KillerListener;
import me.herobrinedobem.heventos.utils.BukkitEventHelper;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;

public class Killer extends EventoBaseAPI {

	private KillerListener listener;
	private int tempoMensagens, tempoMensagensCurrent, tempoPegarItens, tempoPvpOff;
	private int etapa;
	private List<ClanPlayer> clans = new ArrayList<ClanPlayer>();

	public Killer(YamlConfiguration config) {
		super(config);
		listener = new KillerListener();
		HEventos.getHEventos().getServer().getPluginManager().registerEvents(listener, HEventos.getHEventos());
		tempoPegarItens = config.getInt("Config.Tempo_Pegar_Itens");
		tempoMensagens = config.getInt("Config.Tempo_Entre_Avisos");
		tempoPvpOff = config.getInt("Config.Tempo_PVP_Off");
		etapa = 1;
		tempoMensagensCurrent = tempoMensagens;
	}

	@Override
	public void startEventMethod() {
		getEntrada().getWorld().setTime(17000);
		for (Player p : getParticipantes()) {
			p.teleport(EventoUtils.getLocation(getConfig(), "Localizacoes.Entrada"));
			if (HEventos.getHEventos().getSc() != null) {
				if (HEventos.getHEventos().getSc().getClanManager().getClanPlayer(p) != null) {
					HEventos.getHEventos().getSc().getClanManager().getClanPlayer(p).setFriendlyFire(true);
					clans.add(HEventos.getHEventos().getSc().getClanManager().getClanPlayer(p));
				}
			}
			for (String s1 : getConfig().getStringList("Mensagens.IniciandoEm")) {
				p.sendMessage(s1.replace("&", "§").replace("$tempo$", String.valueOf(tempoPvpOff))
						.replace("$EventoName$", getNome()));
			}
		}
	}

	@Override
	public void scheduledMethod() {
		if (getParticipantes().size() > 1) {
			if (etapa == 1) {
				if (tempoPvpOff > 0) {
					tempoPvpOff--;
					return;
				} else {
					for (Player p : getParticipantes()) {
						for (String s1 : getConfig().getStringList("Mensagens.PVPON")) {
							p.sendMessage(s1.replace("&", "§").replace("$tempo$", String.valueOf(tempoPvpOff))
									.replace("$EventoName$", getNome()));
						}
					}
					etapa = 2;
				}
			}
			if (tempoMensagensCurrent == 0) {
				for (String s : getConfig().getStringList("Mensagens.Status")) {
					HEventos.getHEventos().getServer()
							.broadcastMessage(s.replace("&", "§")
									.replace("$jogadores$", String.valueOf(getParticipantes().size()))
									.replace("$EventoName$", getNome()));
				}
				tempoMensagensCurrent = tempoMensagens;
			}
			tempoMensagensCurrent--;
		} else if (getParticipantes().size() == 1) {
			if (tempoPegarItens == 0) {
				Player player = null;
				for (Player s : getParticipantes()) {
					player = s;
				}
				PlayerWinEvent event = new PlayerWinEvent(player, this, false);
				HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
				stopEvent();
			} else if (tempoPegarItens > 0) {
				if (tempoPegarItens == getConfig().getInt("Config.Tempo_Pegar_Itens")) {
					for (Player s : getParticipantes()) {
						for (String p : getConfig().getStringList("Mensagens.Tempo_Pegar_Itens")) {
							s.sendMessage(p.replace("&", "§").replace("$EventoName$", getNome()));
						}
					}
				}
				tempoPegarItens--;
			}
		}
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
		super.stopEvent();
	}

	@Override
	public void resetEvent() {
		if (HEventos.getHEventos().getSc() != null) {
			for (ClanPlayer string : clans) {
				string.setFriendlyFire(false);
			}
		}
		clans.clear();
		super.resetEvent();
		BukkitEventHelper.unregisterEvents(listener, HEventos.getHEventos());
	}

	public int getEtapa() {
		return etapa;
	}

	public KillerListener getListener() {
		return this.listener;
	}

	public List<ClanPlayer> getClans() {
		return clans;
	}

}
