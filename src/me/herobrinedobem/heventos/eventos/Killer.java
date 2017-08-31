package me.herobrinedobem.heventos.eventos;

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

public class Killer extends EventoBaseAPI {

	private KillerListener listener;
	private boolean PvpOff;
	private int tempoMensagens, tempoMensagensCurrent, tempoPegarItens, tempoPvpOff;

	public Killer(YamlConfiguration config) {
		super(config);
		listener = new KillerListener();
		HEventos.getHEventos().getServer().getPluginManager().registerEvents(listener, HEventos.getHEventos());
		tempoPegarItens = config.getInt("Config.Tempo_Pegar_Itens");
		tempoMensagens = config.getInt("Config.Tempo_Entre_Avisos");
		tempoPvpOff = config.getInt("Config.Tempo_PVP_Off");
		PvpOff = true;
		tempoMensagensCurrent = tempoMensagens;
	}

	@Override
	public void startEventMethod() {
		getEntrada().getWorld().setTime(17000);
		for (String s : getParticipantes()) {
			getPlayerByName(s).teleport(EventoUtils.getLocation(getConfig(), "Localizacoes.Entrada"));
			Player p = getPlayerByName(s);
			if (HEventos.getHEventos().getSc() != null) {
				if (HEventos.getHEventos().getSc().getClanManager().getClanPlayer(p) != null) {
					HEventos.getHEventos().getSc().getClanManager().getClanPlayer(p).setFriendlyFire(true);
				}
			} else if (HEventos.getHEventos().getCore() != null) {
				if (HEventos.getHEventos().getCore().getClanPlayerManager().getClanPlayer(p) != null) {
					HEventos.getHEventos().getCore().getClanPlayerManager().getClanPlayer(p).setFriendlyFire(true);
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
		if ((isOcorrendo()) && (!isAberto())) {
			if (getParticipantes().size() > 1) {
				if (tempoPvpOff > 0 && PvpOff) {
					tempoPvpOff--;
					return;
				} else if (tempoPvpOff == 0 && PvpOff) {
					for (String p : getParticipantes()) {
						for (String s1 : getConfig().getStringList("Mensagens.PVPON")) {
							getPlayerByName(p).sendMessage(s1.replace("&", "§")
									.replace("$tempo$", String.valueOf(tempoPvpOff)).replace("$EventoName$", getNome()));
						}
					}
					PvpOff = false;
					tempoPvpOff--;
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
					for (String s : getParticipantes()) {
						player = getPlayerByName(s);
					}
					PlayerWinEvent event = new PlayerWinEvent(player, this, false);
					HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
					stopEvent();
				} else if (tempoPegarItens > 0) {
					if (tempoPegarItens == getConfig().getInt("Config.Tempo_Pegar_Itens")) {
						for (String s : getParticipantes()) {
							for (String p : getConfig().getStringList("Mensagens.Tempo_Pegar_Itens")) {
								getPlayerByName(s).sendMessage(p.replace("&", "§").replace("$EventoName$", getNome()));
							}
						}
					}
					tempoPegarItens--;
				}
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
		super.resetEvent();
		BukkitEventHelper.unregisterEvents(listener, HEventos.getHEventos());
	}

	public boolean isPvpOff() {
		return PvpOff;
	}

	public KillerListener getListener() {
		return this.listener;
	}

}
