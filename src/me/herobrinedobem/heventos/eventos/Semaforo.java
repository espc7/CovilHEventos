package me.herobrinedobem.heventos.eventos;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoCancellType;
import me.herobrinedobem.heventos.api.EventoUtils;
import me.herobrinedobem.heventos.api.events.StopEvent;
import me.herobrinedobem.heventos.eventos.listeners.SemaforoListener;
import me.herobrinedobem.heventos.utils.BukkitEventHelper;

public class Semaforo extends EventoBaseAPI {

	private SemaforoListener listener;
	private int tempoTroca;
	private int tempoAmarelo;
	private int tempoVermelho;
	private int tempoTrocaCurrent;
	private boolean podeAndar;

	public Semaforo(YamlConfiguration config) {
		super(config);
		listener = new SemaforoListener();
		HEventos.getHEventos().getServer().getPluginManager().registerEvents(listener, HEventos.getHEventos());
		tempoTroca = config.getInt("Config.Tempo_Rodada");
		tempoTrocaCurrent = config.getInt("Config.Tempo_Rodada");
		tempoAmarelo = config.getInt("Config.Anuncio_Amarelo");
		tempoVermelho = config.getInt("Config.Anuncio_Vermelho");
		podeAndar = true;
	}

	@Override
	public void startEventMethod() {
		for (Player p : getParticipantes()) {
			p.teleport(EventoUtils.getLocation(getConfig(), "Localizacoes.Entrada"));
			for (String msg : getConfig().getStringList("Mensagens.Verde")) {
				p.sendMessage(msg.replace("&", "§").replace("$EventoName$", getNome()));
			}
		}
	}

	@Override
	public void scheduledMethod() {
		if ((isOcorrendo() == true) && (isAberto() == false)) {
			if (tempoTrocaCurrent == 0) {
				podeAndar = true;
				for (Player p : getParticipantes()) {
					for (String msg : getConfig().getStringList("Mensagens.Verde")) {
						p.sendMessage(msg.replace("&", "§").replace("$EventoName$", getNome()));
					}
				}
				tempoTrocaCurrent = tempoTroca;
			} else if (tempoTrocaCurrent == tempoAmarelo) {
				for (Player p : getParticipantes()) {
					for (String msg : getConfig().getStringList("Mensagens.Amarelo")) {
						p.sendMessage(msg.replace("&", "§").replace("$EventoName$", getNome()));
					}
				}
				tempoTrocaCurrent--;
			} else if (tempoTrocaCurrent == tempoVermelho) {
				for (Player p : getParticipantes()) {
					for (String msg : getConfig().getStringList("Mensagens.Vermelho")) {
						p.sendMessage(msg.replace("&", "§").replace("$EventoName$", getNome()));
					}
				}
				podeAndar = false;
				tempoTrocaCurrent--;
			} else {
				tempoTrocaCurrent--;
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
	}

	@Override
	public void resetEvent() {
		super.resetEvent();
		tempoTroca = this.getConfig().getInt("Config.Tempo_Rodada");
		tempoTrocaCurrent = this.getConfig().getInt("Config.Tempo_Rodada");
		podeAndar = true;
		BukkitEventHelper.unregisterEvents(listener, HEventos.getHEventos());
	}

	public boolean isPodeAndar() {
		return this.podeAndar;
	}

}
