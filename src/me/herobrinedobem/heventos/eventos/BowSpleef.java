package me.herobrinedobem.heventos.eventos;

import java.util.Random;

import org.bukkit.Location;
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
import me.herobrinedobem.heventos.eventos.listeners.BowSpleefListener;
import me.herobrinedobem.heventos.utils.BukkitEventHelper;
import me.herobrinedobem.heventos.utils.Cuboid;
import me.herobrinedobem.heventos.utils.ItemStackFormat;

public class BowSpleef extends EventoBaseAPI {

	private BowSpleefListener listener;
	private Cuboid chao;
	private int tempoRegenera;

	@SuppressWarnings("deprecation")
	public BowSpleef(YamlConfiguration config) {
		super(config);
		listener = new BowSpleefListener();
		HEventos.getHEventos().getServer().getPluginManager().registerEvents(listener, HEventos.getHEventos());
		tempoRegenera = getConfig().getInt("Config.Tempo_Chao_Regenera");
		chao = new Cuboid(EventoUtils.getLocation(config, "Localizacoes.Chao_1"),
				EventoUtils.getLocation(config, "Localizacoes.Chao_2"));
		for (Block b : chao.getBlocks()) {
			b.setType(Material.getMaterial(getConfig().getInt("Config.Chao_ID")));
		}
	}

	@Override
	public void startEventMethod() {
		for (Player s : getParticipantes()) {
			for (String linha : getConfig().getStringList("Itens_Ao_Iniciar")) {
				s.getInventory().addItem(new ItemStack(ItemStackFormat.getItem(linha)));
			}
			for (Block b : chao.getBlocks()) {
				Location l = b.getLocation();
				l.setY(l.getY() + 1);
			}
			Random r = new Random();
			Location l = chao.getBlocks().get(r.nextInt(chao.getBlocks().size())).getLocation();
			l.setY(l.getY() + 1);
			s.teleport(l);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void scheduledMethod() {
		if (tempoRegenera > 0) {
			tempoRegenera--;
		} else {
			for (Block b : chao.getBlocks()) {
				b.setType(Material.getMaterial(getConfig().getInt("Config.Chao_ID")));
			}
			tempoRegenera = getConfig().getInt("Config.Tempo_Chao_Regenera");
		}
		if (getParticipantes().size() == 1) {
			PlayerWinEvent event = new PlayerWinEvent(getParticipantes().get(0), this, false);
			HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
			stopEvent();
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

	public Cuboid getChao() {
		return this.chao;
	}

}
