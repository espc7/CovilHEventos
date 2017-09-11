package me.herobrinedobem.heventos.utils;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoType;
import me.herobrinedobem.heventos.eventos.BatataQuente;
import me.herobrinedobem.heventos.eventos.BowSpleef;
import me.herobrinedobem.heventos.eventos.EventoNormal;
import me.herobrinedobem.heventos.eventos.Fight;
import me.herobrinedobem.heventos.eventos.Frog;
import me.herobrinedobem.heventos.eventos.Killer;
import me.herobrinedobem.heventos.eventos.MinaMortal;
import me.herobrinedobem.heventos.eventos.Paintball;
import me.herobrinedobem.heventos.eventos.Semaforo;
import me.herobrinedobem.heventos.eventos.Spleef;

public class EventosController {

	private EventoBaseAPI evento = null;
	private HEventos instance;
	private String filename;

	public EventosController(HEventos instance) {
		this.instance = instance;
	}

	public void setEvento(String name, EventoType type) {
		switch (type) {
		case SPLEEF:
			this.evento = new Spleef(this.getConfigFile(name));
			break;
		case FROG:
			this.evento = new Frog(this.getConfigFile(name));
			break;
		case FIGHT:
			this.evento = new Fight(this.getConfigFile(name));
			break;
		case BOW_SPLEEF:
			this.evento = new BowSpleef(this.getConfigFile(name));
			break;
		case BATATA_QUENTE:
			this.evento = new BatataQuente(this.getConfigFile(name));
			break;
		case KILLER:
			this.evento = new Killer(this.getConfigFile(name));
			break;
		case MINA_MORTAL:
			this.evento = new MinaMortal(this.getConfigFile(name));
			break;
		case PAINTBALL:
			this.evento = new Paintball(this.getConfigFile(name));
			break;
		case SEMAFORO:
			this.evento = new Semaforo(this.getConfigFile(name));
			break;
		case NORMAL:
			this.evento = new EventoNormal(this.getConfigFile(name));
			break;
		default:
			this.evento = new EventoNormal(this.getConfigFile(name));
			break;
		}
	}

	public boolean externalEvento(String name, boolean vip) {
		for (EventoBaseAPI e : HEventos.getHEventos().getExternalEventos()) {
			if (e.getNome().equalsIgnoreCase(name)) {
				e.externalPluginStart(vip);
				return true;
			}
		}
		return false;
	}

	public YamlConfiguration getConfigFile(String eventoname) {
		File fileEvento = new File(this.instance.getDataFolder().getAbsolutePath() + "/Eventos/" + eventoname + ".yml");
		this.filename = fileEvento.getName().substring(0, fileEvento.getName().length() - 4);
		return YamlConfiguration.loadConfiguration(fileEvento);
	}

	public EventoBaseAPI loadEvento(String eventoname) {
		File fileEvento = new File(this.instance.getDataFolder().getAbsolutePath() + "/Eventos/" + eventoname + ".yml");
		YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
		return new EventoBaseAPI(configEvento);
	}

	public String getFilename() {
		return filename;
	}

	public boolean hasEvento(String evento) {
		try {
			return new File(this.instance.getDataFolder().getAbsolutePath() + "/Eventos/" + evento + ".yml").exists();
		} catch (Exception e) {
			return false;
		}
	}

	public EventoBaseAPI getEvento() {
		return this.evento;
	}

	public void setEvento(EventoBaseAPI evento) {
		this.evento = evento;
	}

}
