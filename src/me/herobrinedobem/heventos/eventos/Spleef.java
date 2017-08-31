package me.herobrinedobem.heventos.eventos;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoCancellType;
import me.herobrinedobem.heventos.api.EventoUtils;
import me.herobrinedobem.heventos.api.events.PlayerWinEvent;
import me.herobrinedobem.heventos.api.events.StopEvent;
import me.herobrinedobem.heventos.eventos.listeners.SpleefListener;
import me.herobrinedobem.heventos.utils.BukkitEventHelper;
import me.herobrinedobem.heventos.utils.Cuboid;
import me.herobrinedobem.heventos.utils.ItemStackFormat;

public class Spleef extends EventoBaseAPI {

	private SpleefListener listener;
	private boolean podeQuebrar;
	private boolean regenerarChao;
	private Cuboid cubo;
	private int tempoChaoRegenera, tempoChaoRegeneraCurrent, tempoComecar, tempoComecarCurrent;
	private int y;

	@SuppressWarnings("deprecation")
	public Spleef(YamlConfiguration config) {
		super(config);
		listener = new SpleefListener();
		HEventos.getHEventos().getServer().getPluginManager().registerEvents(listener, HEventos.getHEventos());
		regenerarChao = config.getBoolean("Config.Regenerar_Chao");
		tempoChaoRegenera = config.getInt("Config.Tempo_Chao_Regenera");
		tempoComecar = config.getInt("Config.Tempo_Comecar");
		podeQuebrar = false;
		tempoComecarCurrent = tempoComecar;
		y = (int) (EventoUtils.getLocation(getConfig(), "Localizacoes.Chao_1").getY() - 1.0);
		cubo = new Cuboid(EventoUtils.getLocation(getConfig(), "Localizacoes.Chao_1"),
				EventoUtils.getLocation(getConfig(), "Localizacoes.Chao_2"));
		for (Block b : cubo.getBlocks()) {
			b.setType(Material.getMaterial(getConfig().getInt("Config.Chao_ID")));
		}
	}

	@Override
	public void startEventMethod() {
		for (String p : getParticipantes()) {
			for (String linha : getConfig().getStringList("Itens_Ao_Iniciar")) {
				getPlayerByName(p).getPlayer().getInventory().addItem(new ItemStack(ItemStackFormat.getItem(linha)));
			}
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
		if (isOcorrendo() && !isAberto()) {
			if (tempoComecarCurrent == 0 && !podeQuebrar) {
				for (String p : getParticipantes()) {
					for (String s1 : getConfig().getStringList("Mensagens.Pode_Quebrar")) {
						getPlayerByName(p).sendMessage(s1.replace("&", "§")
								.replace("$tempo$", String.valueOf(tempoComecar)).replace("$EventoName$", getNome()));
					}
				}
				podeQuebrar = true;
			} else if(!podeQuebrar){
				tempoComecarCurrent--;
			}
			if (regenerarChao) {
				if (tempoChaoRegeneraCurrent == 0) {
					for (Block b : cubo.getBlocks()) {
						b.setType(Material.getMaterial(getConfig().getInt("Config.Chao_ID")));
					}
					tempoChaoRegeneraCurrent = tempoChaoRegenera;
				} else {
					tempoChaoRegeneraCurrent--;
				}
			}
			if (getParticipantes().size() == 1) {
				Player player = null;
				for (String s : getParticipantes()) {
					player = getPlayerByName(s);
				}
				PlayerWinEvent event = new PlayerWinEvent(player, this, false);
				HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
				for (Block b : cubo.getBlocks()) {
					b.setType(Material.getMaterial(7));
				}
				stopEvent();
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
		BukkitEventHelper.unregisterEvents(listener, HEventos.getHEventos());
	}

	public boolean isPodeQuebrar() {
		return this.podeQuebrar;
	}

	public double getY() {
		return this.y;
	}

	public int getTempoComecarCurrent() {
		return this.tempoComecarCurrent;
	}
}
