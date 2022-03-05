
package me.herobrinedobem.heventos.eventos;

import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoCancellType;
import me.herobrinedobem.heventos.api.EventoUtils;
import me.herobrinedobem.heventos.api.events.PlayerLoseEvent;
import me.herobrinedobem.heventos.api.events.PlayerWinEvent;
import me.herobrinedobem.heventos.api.events.StopEvent;
import me.herobrinedobem.heventos.eventos.listeners.BatataQuenteListener;
import me.herobrinedobem.heventos.utils.BukkitEventHelper;

public class BatataQuente extends EventoBaseAPI {

	private BatataQuenteListener listener;
	private int tempoBatataCurrent, tempoBatata;
	private Player playerComBatata;
	private List<Integer> tempoAviso;

	public BatataQuente(YamlConfiguration config) {
		super(config);
		listener = new BatataQuenteListener();
		HEventos.getHEventos().getServer().getPluginManager().registerEvents(listener, HEventos.getHEventos());
		tempoBatata = config.getInt("Config.Tempo_Batata_Explodir");
		tempoAviso = config.getIntegerList("Config.Tempo_Batata_Avisos");
		playerComBatata = null;
		tempoBatataCurrent = tempoBatata;
	}

	@Override
	public void startEventMethod() {
		for (Player s : getParticipantes()) {
			s.teleport(EventoUtils.getLocation(getConfig(), "Localizacoes.Entrada"));
		}
		Random r = new Random();
		playerComBatata = getParticipantes().get(r.nextInt(getParticipantes().size()));
		playerComBatata.getInventory().addItem(new ItemStack(Material.POTATO, 1));
		for (Player sa : getParticipantes()) {
			sa.setFoodLevel(20);
			for (String s : getConfig().getStringList("Mensagens.Esta_Com_Batata")) {
				sa.sendMessage(s.replace("&", "§").replace("$player$", playerComBatata.getName())
						.replace("$EventoName$", getNome()));
			}
		}
	}

	@Override
	public void scheduledMethod() {
		if (playerComBatata != null) {
			if (getParticipantes().size() == 1) {
				Player player = getParticipantes().get(0);
				PlayerWinEvent event = new PlayerWinEvent(player, this, false);
				HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
				stopEvent();
			} else {
				if (tempoBatataCurrent == 0) {
					playerComBatata.getInventory().removeItem(new ItemStack(Material.POTATO, 1));
					PlayerLoseEvent event = new PlayerLoseEvent(playerComBatata,
							HEventos.getHEventos().getEventosController().getEvento());
					HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
					playerComBatata.sendMessage(getConfig().getString("Mensagens.VcFoiEliminado").replace("&", "§")
							.replace("$EventoName$", getNome()));
					String msg = getConfig().getString("Mensagens.FoiEliminado");
					for (Player p : getParticipantes()) {
						p.sendMessage(msg.replace("&", "§").replace("$EventoName$", getNome()).replace("$player$",
								playerComBatata.getName()));

					}
					if (getParticipantes().size() > 1) {
						Random r = new Random();
						playerComBatata = getParticipantes().get(r.nextInt(getParticipantes().size()));
						for (Player p : getParticipantes()) {
							for (String s : getConfig().getStringList("Mensagens.Esta_Com_Batata")) {
								playerComBatata.getInventory().addItem(new ItemStack(Material.POTATO, 1));
								p.sendMessage(s.replace("&", "§").replace("$player$", playerComBatata.getName())
										.replace("$EventoName$", getNome()));
							}
						}
					} else {
						return;
					}
					tempoBatataCurrent = tempoBatata;
				} else if (tempoBatataCurrent > 0)
					tempoBatataCurrent--;
				Location loc = playerComBatata.getLocation();
				Firework firework = playerComBatata.getWorld().spawn(loc, Firework.class);
				FireworkMeta data = firework.getFireworkMeta();
				data.addEffects(FireworkEffect.builder().withColor(Color.RED).with(Type.BALL).build());
				data.setPower(2);
				firework.setFireworkMeta(data);
				if (!tempoAviso.contains(tempoBatataCurrent)) {
					return;
				}
				for (Player p : getParticipantes()) {
					p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
				}
				for (Player p : getParticipantes()) {
					p.sendMessage(getConfig().getString("Mensagens.Tempo").replace("&", "§")
							.replace("$tempo$", tempoBatataCurrent + "").replace("$EventoName$", getNome()));
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

	public Player getPlayerComBatata() {
		return this.playerComBatata;
	}

	public void setPlayerComBatata(Player playerComBatata) {
		this.playerComBatata = playerComBatata;
	}
}
