package me.herobrinedobem.heventos.eventos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoCancellType;
import me.herobrinedobem.heventos.api.EventoUtils;
import me.herobrinedobem.heventos.api.events.StopEvent;
import me.herobrinedobem.heventos.eventos.listeners.FrogListener;
import me.herobrinedobem.heventos.utils.BukkitEventHelper;
import me.herobrinedobem.heventos.utils.Cuboid;

public class Frog extends EventoBaseAPI {

	private FrogListener listener;
	private int tempoComecar, tempoComecarCurrent;
	private int tempoRodada, tempoTirarNeve, tempoVoltarNeve;
	private int tempoRodadaCurrent, tempoTirarNeveCurrent, tempoVoltarNeveCurrent;
	private boolean comecou, selecionou, tirou, voltou, fim;
	private Random r = new Random();
	private Location l, laVermelha;
	private World w;
	private Map<Location, String> blocos = new HashMap<>();
	private List<Location> loc = new ArrayList<>();
	private List<Location> loc2 = new ArrayList<>();
	private Cuboid cubo;
	private int y;

	@SuppressWarnings("deprecation")
	public Frog(YamlConfiguration config) {
		super(config);
		listener = new FrogListener();
		HEventos.getHEventos().getServer().getPluginManager().registerEvents(listener, HEventos.getHEventos());
		tempoComecar = config.getInt("Config.Tempo_Comecar");
		tempoComecarCurrent = tempoComecar;
		tempoRodada = config.getInt("Config.Tempo_Rodada");
		tempoRodadaCurrent = tempoRodada;
		tempoTirarNeve = config.getInt("Config.Tempo_Tirar_Neve");
		tempoTirarNeveCurrent = tempoTirarNeve;
		tempoVoltarNeve = config.getInt("Config.Tempo_Voltar_Neve");
		tempoVoltarNeveCurrent = tempoVoltarNeve;
		comecou = false;
		selecionou = false;
		tirou = false;
		voltou = false;
		fim = false;
		w = EventoUtils.getLocation(getConfig(), "Localizacoes.Chao_1").getWorld();
		y = (int) (EventoUtils.getLocation(getConfig(), "Localizacoes.Chao_1").getY() - 1.0);
		cubo = new Cuboid(EventoUtils.getLocation(getConfig(), "Localizacoes.Chao_1"),
				EventoUtils.getLocation(getConfig(), "Localizacoes.Chao_2"));
		for (Block b : cubo.getBlocks()) {
			if ((b.getType() != Material.SNOW_BLOCK) && (b.getType() != Material.AIR)) {
				String id = b.getTypeId() + ":" + b.getData();
				blocos.put(b.getLocation(), id);
				loc.add(b.getLocation());
			} else {
				b.setType(Material.SNOW_BLOCK);
			}
		}
	}

	@Override
	public void startEventMethod() {
		for (String p : getParticipantes()) {
			getPlayerByName(p).teleport(EventoUtils.getLocation(getConfig(), "Localizacoes.Entrada"));
			for (String s1 : getConfig().getStringList("Mensagens.IniciandoEm")) {
				getPlayerByName(p).sendMessage(s1.replace("&", "§").replace("$tempo$", String.valueOf(tempoComecar))
						.replace("$EventoName$", getNome()));
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void scheduledMethod() {
		if ((isOcorrendo()) && (!isAberto())) {
			if(getParticipantes().size() == 0) {
				sendMessageList("Mensagens.Sem_Vencedor");
				stopEvent();
			}
			if (tempoComecarCurrent > 0) {
				tempoComecarCurrent--;
				return;
			} else if (tempoRodadaCurrent == 0 && !comecou) {
				for (Block b : cubo.getBlocks()) {
					if (!blocos.containsKey(b.getLocation()))
						b.setType(Material.AIR);
				}
				for (String p : getParticipantes()) {
					for (String s1 : getConfig().getStringList("Mensagens.Comecou")) {
						getPlayerByName(p).sendMessage(s1.replace("&", "§").replace("$EventoName$", getNome()));
					}
				}
				comecou = true;
				tempoComecarCurrent--;
				return;
			}
			// Rodada
			if (tempoRodadaCurrent == 0 && !fim) {
				// selecionar bloco
				if (!selecionou) {
					l = loc.get(r.nextInt(loc.size()));
					loc2.add(l);
					loc.remove(l);
					l.getWorld().getBlockAt(l).setType(Material.SNOW_BLOCK);
					selecionou = true;
				}
				// selecionando la vermelha
				if (loc.isEmpty()) {
					for (String p : getParticipantes()) {
						for (String s1 : getConfig().getStringList("Mensagens.LaVermelha")) {
							getPlayerByName(p).sendMessage(s1.replace("&", "§").replace("$EventoName$", getNome()));
						}
					}
					laVermelha = loc2.get(r.nextInt(loc2.size()));
					w.getBlockAt(laVermelha).setType(Material.WOOL);
					w.getBlockAt(laVermelha).setData((byte) 14);
					fim = true;
					return;
				}
				// tirar neves
				if (tempoTirarNeveCurrent == 0 && !tirou) {
					for (Location l1 : loc2) {
						l1.getWorld().getBlockAt(l1).setType(Material.AIR);
					}
					tirou = true;
					tempoTirarNeveCurrent = tempoTirarNeve;
					return;
				} else if (!tirou) {
					tempoTirarNeveCurrent--;
					return;
				}
				// voltar neves
				if (tempoVoltarNeveCurrent == 0 && !voltou) {
					for (Location l1 : loc2) {
						l1.getWorld().getBlockAt(l1).setType(Material.SNOW_BLOCK);
					}
					voltou = true;
					tempoVoltarNeveCurrent = tempoVoltarNeve;
				} else if (!voltou) {
					tempoVoltarNeveCurrent--;
					return;
				}
				voltou = false;
				tirou = false;
				selecionou = false;
				tempoRodadaCurrent = tempoRodada;
			} else if (!fim) {
				tempoRodadaCurrent--;
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

	@SuppressWarnings("deprecation")
	@Override
	public void resetEvent() {
		for (Block b : cubo.getBlocks()) {
			if (blocos.containsKey(b.getLocation())) {
				int id = Integer.parseInt(blocos.get(b.getLocation()).split(":")[0]);
				byte data = Byte.parseByte(blocos.get(b.getLocation()).split(":")[1]);
				b.setTypeId(id);
				b.setData(data);
			} else {
				b.setType(Material.AIR);
			}
		}
		super.resetEvent();
		blocos.clear();;
		loc.clear();;
		cubo = null;
		BukkitEventHelper.unregisterEvents(listener, HEventos.getHEventos());
	}

	public double getY() {
		return this.y;
	}

	public Location getLaVermelha() {
		return laVermelha;
	}

	public boolean isFim() {
		return fim;
	}

	public boolean isComecou() {
		return comecou;
	}
	
	
}
