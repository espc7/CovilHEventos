package me.herobrinedobem.heventos.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import me.herobrinedobem.heventos.HEventos;

public class EventoBaseAPI implements EventoBaseImplements {

	private EventoType eventoType;
	private String horarioStart;
	private List<Player> participantes = new ArrayList<Player>();
	private boolean ocorrendo, aberto, parte1, vip, assistirAtivado, pvp, contarVitoria, contarParticipacao,
			inventoryEmpty;
	private int chamadas, tempo, id2, chamadascurrent, id;
	private String nome;
	private Location saida, entrada, camarote, aguarde;
	private List<Player> camarotePlayers = new ArrayList<Player>();
	private YamlConfiguration config;

	public EventoBaseAPI(YamlConfiguration config) {
		this.config = config;
		this.eventoType = EventoType.getEventoType(config.getString("Config.Evento_Type"));
		this.nome = this.config.getString("Config.Nome");
		this.chamadas = this.config.getInt("Config.Chamadas");
		this.vip = this.config.getBoolean("Config.VIP");
		this.assistirAtivado = this.config.getBoolean("Config.Assistir_Ativado");
		this.pvp = this.config.getBoolean("Config.PVP");
		this.contarParticipacao = this.config.getBoolean("Config.Contar_Participacao");
		this.contarVitoria = this.config.getBoolean("Config.Contar_Vitoria");
		this.tempo = this.config.getInt("Config.Tempo_Entre_As_Chamadas");
		this.inventoryEmpty = this.config.getBoolean("Config.Inv_Vazio");
		this.saida = EventoUtils.getLocation(config, "Localizacoes.Saida");
		this.camarote = EventoUtils.getLocation(config, "Localizacoes.Camarote");
		this.entrada = EventoUtils.getLocation(config, "Localizacoes.Entrada");
		this.aguarde = EventoUtils.getLocation(config, "Localizacoes.Aguardando");
		this.aberto = false;
		this.ocorrendo = false;
		this.parte1 = false;
		this.participantes.clear();
		this.chamadascurrent = this.chamadas;
	}

	public void run() {
		BukkitScheduler scheduler = HEventos.getHEventos().getServer().getScheduler();
		this.id = scheduler.scheduleSyncRepeatingTask(HEventos.getHEventos(), new Runnable() {
			@Override
			public void run() {
				if (!EventoBaseAPI.this.parte1) {
					EventoBaseAPI.this.startEvent();
				}
			}
		}, 0, this.tempo * 20L);

		this.id2 = scheduler.scheduleSyncRepeatingTask(HEventos.getHEventos(), new Runnable() {
			@Override
			public void run() {
				EventoBaseAPI.this.scheduledMethod();
			}
		}, 0, 20L);
	}

	@Override
	public void startEvent() {
		if (EventoBaseAPI.this.chamadascurrent >= 1) {
			EventoBaseAPI.this.chamadascurrent--;
			EventoBaseAPI.this.ocorrendo = true;
			EventoBaseAPI.this.aberto = true;
			if (EventoBaseAPI.this.vip) {
				EventoBaseAPI.this.sendMessageList("Mensagens.Aberto_VIP");
			} else {
				EventoBaseAPI.this.sendMessageList("Mensagens.Aberto");
			}
		} else if (EventoBaseAPI.this.chamadascurrent == 0) {
			if (EventoBaseAPI.this.participantes.size() > 1) {
				if (EventoBaseAPI.this.isContarParticipacao()) {
					for (Player p : EventoBaseAPI.this.participantes) {
						HEventos.getHEventos().getDatabaseManager().addParticipationPoint(p.getName(), 1);
					}
				}
				EventoBaseAPI.this.aberto = false;
				EventoBaseAPI.this.parte1 = true;
				EventoBaseAPI.this.sendMessageList("Mensagens.Iniciando");
				this.startEventMethod();
				for (Player p : EventoBaseAPI.this.camarotePlayers) {
					p.teleport(EventoBaseAPI.this.camarote);
				}
			} else {
				EventoBaseAPI.this.stopEvent();
				EventoBaseAPI.this.sendMessageList("Mensagens.Cancelado");
				HEventos.getHEventos().getServer().getScheduler().cancelTask(EventoBaseAPI.this.id);
			}
		}
	}

	@Override
	public void stopEvent() {

	}

	@Override
	public void resetEvent() {
		this.nome = this.config.getString("Config.Nome");
		this.chamadas = this.config.getInt("Config.Chamadas");
		this.vip = this.config.getBoolean("Config.VIP");
		this.assistirAtivado = this.config.getBoolean("Config.Assistir_Ativado");
		this.pvp = this.config.getBoolean("Config.PVP");
		this.contarParticipacao = this.config.getBoolean("Config.Contar_Participacao");
		this.contarVitoria = this.config.getBoolean("Config.Contar_Vitoria");
		this.tempo = this.config.getInt("Config.Tempo_Entre_As_Chamadas");
		this.saida = EventoUtils.getLocation(config, "Localizacoes.Saida");
		this.camarote = EventoUtils.getLocation(config, "Localizacoes.Camarote");
		this.entrada = EventoUtils.getLocation(config, "Localizacoes.Entrada");
		this.aguarde = EventoUtils.getLocation(config, "Localizacoes.Aguardando");
		this.inventoryEmpty = this.config.getBoolean("Config.Inv_Vazio");
		this.aberto = false;
		this.ocorrendo = false;
		this.parte1 = false;
		this.chamadascurrent = this.chamadas;
		HEventos.getHEventos().getEventosController().setEvento(null);
		HEventos.getHEventos().getServer().getScheduler().cancelTask(this.id);
		HEventos.getHEventos().getServer().getScheduler().cancelTask(this.id2);
	}

	@Override
	public void externalPluginStart() {
		if (HEventos.getHEventos().getEventosController().getEvento() == null) {
			HEventos.getHEventos().getEventosController().setEvento(this);
			HEventos.getHEventos().getEventosController().getEvento().setVip(isVip());
			HEventos.getHEventos().getEventosController().getEvento().run();
		}
	}

	@Override
	public void cancelEventMethod() {
	}

	@Override
	public void startEventMethod() {
	}

	@Override
	public void scheduledMethod() {
	}

	@Override
	public void stopEventMethod() {
	}

	public void sendMessageList(String list) {
		for (String s : this.config.getStringList(list)) {
			HEventos.getHEventos().getServer().broadcastMessage(s.replace("&", "§").replace("$EventoName$", getNome()));
		}
	}

	public int getId() {
		return this.id;
	}

	public EventoType getEventoType() {
		return this.eventoType;
	}

	public boolean isOcorrendo() {
		return this.ocorrendo;
	}

	public void setOcorrendo(boolean ocorrendo) {
		this.ocorrendo = ocorrendo;
	}

	public boolean isAberto() {
		return this.aberto;
	}

	public void setAberto(boolean aberto) {
		this.aberto = aberto;
	}

	public boolean isParte1() {
		return this.parte1;
	}

	public void setParte1(boolean parte1) {
		this.parte1 = parte1;
	}

	public boolean isVip() {
		return this.vip;
	}

	public void setVip(boolean vip) {
		this.vip = vip;
	}

	public boolean isAssistirAtivado() {
		return this.assistirAtivado;
	}

	public void setAssistirAtivado(boolean assistirAtivado) {
		this.assistirAtivado = assistirAtivado;
	}

	public boolean isPvp() {
		return this.pvp;
	}

	public void setPvp(boolean pvp) {
		this.pvp = pvp;
	}

	public boolean isContarVitoria() {
		return this.contarVitoria;
	}

	public void setContarVitoria(boolean contarVitoria) {
		this.contarVitoria = contarVitoria;
	}

	public boolean isContarParticipacao() {
		return this.contarParticipacao;
	}

	public void setContarParticipacao(boolean contarParticipacao) {
		this.contarParticipacao = contarParticipacao;
	}

	public int getChamadas() {
		return this.chamadas;
	}

	public void setChamadas(int chamadas) {
		this.chamadas = chamadas;
	}

	public int getTempo() {
		return this.tempo;
	}

	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	public void setId(int id) {
	}

	public int getId2() {
		return this.id2;
	}

	public void setId2(int id2) {
		this.id2 = id2;
	}

	public int getChamadascurrent() {
		return this.chamadascurrent;
	}

	public void setChamadascurrent(int chamadascurrent) {
		this.chamadascurrent = chamadascurrent;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Location getSaida() {
		return this.saida;
	}

	public void setSaida(Location saida) {
		this.saida = saida;
	}

	public Location getEntrada() {
		return this.entrada;
	}

	public void setEntrada(Location entrada) {
		this.entrada = entrada;
	}

	public Location getCamarote() {
		return this.camarote;
	}

	public void setCamarote(Location camarote) {
		this.camarote = camarote;
	}

	public Location getAguarde() {
		return this.aguarde;
	}

	public void setAguarde(Location aguarde) {
		this.aguarde = aguarde;
	}

	public YamlConfiguration getConfig() {
		return this.config;
	}

	public void setConfig(YamlConfiguration config) {
		this.config = config;
	}

	public List<Player> getParticipantes() {
		return this.participantes;
	}

	public List<Player> getCamarotePlayers() {
		return this.camarotePlayers;
	}

	public String getHorarioStart() {
		return horarioStart;
	}

	public void setHorarioStart(String horarioStart) {
		this.horarioStart = horarioStart;
	}

	public boolean isInventoryEmpty() {
		return this.inventoryEmpty;
	}
}
