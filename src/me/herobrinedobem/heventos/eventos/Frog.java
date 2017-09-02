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
import org.bukkit.entity.Player;

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
	private int etapa;
	private Random r = new Random();
	private Location l, laVermelha;
	private World w;
	private Map<Location, String> blocosIniciaisBackup = new HashMap<>();
	private List<Location> blocosInciais = new ArrayList<>();
	private List<Location> blocosRemovidos = new ArrayList<>();
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
		etapa = 1;
		w = EventoUtils.getLocation(getConfig(), "Localizacoes.Chao_1").getWorld();
		y = (int) (EventoUtils.getLocation(getConfig(), "Localizacoes.Chao_1").getY() - 1.0);
		cubo = new Cuboid(EventoUtils.getLocation(getConfig(), "Localizacoes.Chao_1"),
				EventoUtils.getLocation(getConfig(), "Localizacoes.Chao_2"));
		for (Block b : cubo.getBlocks()) {
			if ((b.getType() != Material.SNOW_BLOCK) && (b.getType() != Material.AIR)) {
				String id = b.getTypeId() + ":" + b.getData();
				blocosIniciaisBackup.put(b.getLocation(), id);
				blocosInciais.add(b.getLocation());
			} else {
				b.setType(Material.SNOW_BLOCK);
			}
		}
	}

	@Override
	public void startEventMethod() {
		for (Player p : getParticipantes()) {
			p.teleport(EventoUtils.getLocation(getConfig(), "Localizacoes.Entrada"));
			for (String s1 : getConfig().getStringList("Mensagens.IniciandoEm")) {
				p.sendMessage(s1.replace("&", "§").replace("$tempo$", String.valueOf(tempoComecar))
						.replace("$EventoName$", getNome()));
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void scheduledMethod() {
		if ((isOcorrendo()) && (!isAberto())) {
			if (getParticipantes().size() == 0) {
				sendMessageList("Mensagens.Sem_Vencedor");
				stopEvent();
			}
			// Preparar
			if (etapa == 1) {
				if (tempoComecarCurrent > 0) {
					tempoComecarCurrent--;
					return;
				}
				for (Block b : cubo.getBlocks()) {
					if (!blocosIniciaisBackup.containsKey(b.getLocation()))
						b.setType(Material.AIR);
				}
				for (Player p : getParticipantes()) {
					for (String s1 : getConfig().getStringList("Mensagens.Comecou")) {
						p.sendMessage(s1.replace("&", "§").replace("$EventoName$", getNome()));
					}
				}
				etapa = 2;
			} else if (etapa != 6) {
				// Rodada
				if (tempoRodadaCurrent > 0) {
					tempoRodadaCurrent--;
					return;
				}
				switch (etapa) {
				case 2:
					// selecionar bloco para virar neve
					l = blocosInciais.get(r.nextInt(blocosInciais.size()));
					blocosRemovidos.add(l);
					blocosInciais.remove(l);
					l.getWorld().getBlockAt(l).setType(Material.SNOW_BLOCK);
					etapa = 3;
					// selecionar lã vermelha se todos blocos foram removidos
					if (blocosInciais.isEmpty()) {
						for (Player p : getParticipantes()) {
							for (String s1 : getConfig().getStringList("Mensagens.LaVermelha")) {
								p.sendMessage(s1.replace("&", "§").replace("$EventoName$", getNome()));
							}
						}
						laVermelha = blocosRemovidos.get(r.nextInt(blocosRemovidos.size()));
						w.getBlockAt(laVermelha).setType(Material.WOOL);
						w.getBlockAt(laVermelha).setData((byte) 14);
						etapa = 6;
						return;
					}
					break;
				case 3:
					// Remover as neves
					if (tempoTirarNeveCurrent > 0) {
						tempoTirarNeveCurrent--;
						return;
					}
					for (Location l1 : blocosRemovidos) {
						l1.getWorld().getBlockAt(l1).setType(Material.AIR);
					}
					etapa = 4;
					tempoTirarNeveCurrent = tempoTirarNeve;
					break;
				case 4:
					// Recolocar as neves
					if (tempoVoltarNeveCurrent > 0) {
						tempoVoltarNeveCurrent--;
						return;
					}
					for (Location l1 : blocosRemovidos) {
						l1.getWorld().getBlockAt(l1).setType(Material.SNOW_BLOCK);
					}
					etapa = 2;
					tempoVoltarNeveCurrent = tempoVoltarNeve;
					tempoRodadaCurrent = tempoRodada;
					break;
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
	}

	@SuppressWarnings("deprecation")
	@Override
	public void resetEvent() {
		for (Block b : cubo.getBlocks()) {
			if (blocosIniciaisBackup.containsKey(b.getLocation())) {
				int id = Integer.parseInt(blocosIniciaisBackup.get(b.getLocation()).split(":")[0]);
				byte data = Byte.parseByte(blocosIniciaisBackup.get(b.getLocation()).split(":")[1]);
				b.setTypeId(id);
				b.setData(data);
			} else {
				b.setType(Material.AIR);
			}
		}
		super.resetEvent();
		blocosIniciaisBackup.clear();
		blocosInciais.clear();
		cubo = null;
		BukkitEventHelper.unregisterEvents(listener, HEventos.getHEventos());
	}

	public double getY() {
		return this.y;
	}

	public Location getLaVermelha() {
		return laVermelha;
	}

	public int getEtapa() {
		return etapa;
	}
}
