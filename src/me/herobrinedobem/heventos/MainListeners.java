package me.herobrinedobem.heventos;

import java.io.File;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.herobrinedobem.heventos.api.EventoCancellType;
import me.herobrinedobem.heventos.api.events.PlayerLeaveEvent;
import me.herobrinedobem.heventos.api.events.PlayerLoseEvent;
import me.herobrinedobem.heventos.api.events.PlayerWinEvent;
import me.herobrinedobem.heventos.api.events.StopEvent;
import me.herobrinedobem.heventos.api.events.TeamWinEvent;
import me.herobrinedobem.heventos.utils.ItemStackFormat;

public class MainListeners implements Listener {

	@EventHandler
	public void onStopEvent(StopEvent e) {
		for (Player p : e.getEvento().getParticipantes()) {
			if (e.getEvento().isInventoryEmpty()) {
				p.getInventory().setHelmet(null);
				p.getInventory().setChestplate(null);
				p.getInventory().setLeggings(null);
				p.getInventory().setBoots(null);
				p.getInventory().clear();
			}
			p.teleport(e.getEvento().getSaida());
		}
		for (Player p : e.getEvento().getCamarotePlayers()) {
			p.teleport(e.getEvento().getSaida());
		}
		e.getEvento().getParticipantes().clear();
		e.getEvento().getCamarotePlayers().clear();
		e.getEvento().resetEvent();
		if (e.getCancellType() == EventoCancellType.CANCELLED
				|| e.getCancellType() == EventoCancellType.SERVER_STOPED) {
			e.getEvento().cancelEventMethod();
		} else {
			e.getEvento().stopEventMethod();
		}
	}

	@EventHandler
	public void onEventoPlayerOutEvent(PlayerLeaveEvent e) {
		if (e.isAssistindo()) {
			HEventos.getHEventos().getEventosController().getEvento().getCamarotePlayers().remove(e.getPlayer());
			e.getPlayer().teleport(HEventos.getHEventos().getEventosController().getEvento().getSaida());
			return;
		}
		PlayerLoseEvent event = new PlayerLoseEvent(e.getPlayer(),
				HEventos.getHEventos().getEventosController().getEvento());
		HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
	}

	@EventHandler
	public void onEventoPlayerLoseEvent(PlayerLoseEvent e) {
		if (e.getEvento().isInventoryEmpty()) {
			e.getPlayer().getInventory().setHelmet(null);
			e.getPlayer().getInventory().setChestplate(null);
			e.getPlayer().getInventory().setLeggings(null);
			e.getPlayer().getInventory().setBoots(null);
			e.getPlayer().getInventory().clear();
		}
		e.getEvento().getParticipantes().remove(e.getPlayer());
		e.getPlayer().teleport(e.getEvento().getSaida());
	}

	@EventHandler
	public void onTimeWinEvent(TeamWinEvent e) {
		for (Player p : e.getList()) {
			PlayerWinEvent event = new PlayerWinEvent(p, HEventos.getHEventos().getEventosController().getEvento(),
					true);
			HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
		}
		for (String s : e.getEvento().getConfig().getStringList("Mensagens.Vencedor")) {
			HEventos.getHEventos().getServer().broadcastMessage(s.replaceAll("&", "§")
					.replace("$player$", e.getNomeTime()).replace("$EventoName$", e.getEvento().getNome()));
		}
	}

	@EventHandler
	public void onEventoPlayerWinEvent(PlayerWinEvent e) {
		if (e.getEvento().isInventoryEmpty()) {
			e.getPlayer().getInventory().setHelmet(null);
			e.getPlayer().getInventory().setChestplate(null);
			e.getPlayer().getInventory().setLeggings(null);
			e.getPlayer().getInventory().setBoots(null);
			e.getPlayer().getInventory().clear();
		}
		if (e.getEvento().isContarVitoria()) {
			HEventos.getHEventos().getDatabaseManager().addWinPoint(e.getPlayer().getName(), 1);
		}
		if (e.getEvento().getConfig().getStringList("Premios.Itens") != null) {
			for (String linha : e.getEvento().getConfig().getStringList("Premios.Itens")) {
				e.getPlayer().getInventory().addItem(new ItemStack(ItemStackFormat.getItem(linha)));
			}
		}
		if (e.getEvento().getConfig().getStringList("Premios.Comandos") != null) {
			for (String comando : e.getEvento().getConfig().getStringList("Premios.Comandos")) {
				HEventos.getHEventos().getServer().dispatchCommand(
						HEventos.getHEventos().getServer().getConsoleSender(),
						comando.replace("$player$", e.getPlayer().getName()));
			}
		}
		if (HEventos.getHEventos().getEconomy() != null) {
			HEventos.getHEventos().getEconomy().depositPlayer(e.getPlayer(),
					e.getEvento().getConfig().getDouble("Premios.Money")
							* HEventos.getHEventos().getConfig().getInt("Money_Multiplicador"));
		}
		if (!e.isTeamEvent()) {
			for (String s : e.getEvento().getConfig().getStringList("Mensagens.Vencedor")) {
				HEventos.getHEventos().getServer().broadcastMessage(s.replaceAll("&", "§")
						.replace("$player$", e.getPlayer().getName()).replace("$EventoName$", e.getEvento().getNome()));
			}
		}

		e.getEvento().getParticipantes().remove(e.getPlayer());
		e.getPlayer().teleport(e.getEvento().getSaida());
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		if (!e.getPlayer().hasPermission("heventos.admin"))
			return;
		if (!(e.getPlayer().getItemInHand().getType() == Material.IRON_AXE))
			return;
		if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().contains("Evento Spleef")) {
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				File fileEvento = new File(
						HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Eventos/spleef.yml");
				YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
				configEvento.set("Localizacoes.Chao_1",
						e.getClickedBlock().getWorld().getName() + ";" + (int) e.getClickedBlock().getLocation().getX()
								+ ";" + (int) e.getClickedBlock().getLocation().getY() + ";"
								+ (int) e.getClickedBlock().getLocation().getZ());
				try {
					configEvento.save(new File(HEventos.getHEventos().getDataFolder() + File.separator + "Eventos"
							+ File.separator + "spleef.yml"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.getPlayer().sendMessage("§4[Evento] §cLocalizacao 1 do chao do spleef setada!");
				e.setCancelled(true);
			} else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				File fileEvento = new File(
						HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Eventos/spleef.yml");
				YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
				configEvento.set("Localizacoes.Chao_2",
						e.getClickedBlock().getWorld().getName() + ";" + (int) e.getClickedBlock().getLocation().getX()
								+ ";" + (int) e.getClickedBlock().getLocation().getY() + ";"
								+ (int) e.getClickedBlock().getLocation().getZ());
				try {
					configEvento.save(new File(HEventos.getHEventos().getDataFolder() + File.separator + "Eventos"
							+ File.separator + "spleef.yml"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.getPlayer().sendMessage("§4[Evento] §cLocalizacao 2 do chao do spleef setada!");
				e.setCancelled(true);
			}
		} else if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().contains("Evento MinaMortal")) {
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				File fileEvento = new File(
						HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Eventos/minamortal.yml");
				YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
				configEvento.set("Localizacoes.Mina_1",
						e.getClickedBlock().getWorld().getName() + ";" + (int) e.getClickedBlock().getLocation().getX()
								+ ";" + (int) e.getClickedBlock().getLocation().getY() + ";"
								+ (int) e.getClickedBlock().getLocation().getZ());
				try {
					configEvento.save(new File(HEventos.getHEventos().getDataFolder() + File.separator + "Eventos"
							+ File.separator + "minamortal.yml"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.getPlayer().sendMessage("§4[Evento] §cLocalizacao 1 da mina setada!");
				e.setCancelled(true);
			} else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				File fileEvento = new File(
						HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Eventos/minamortal.yml");
				YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
				configEvento.set("Localizacoes.Mina_2",
						e.getClickedBlock().getWorld().getName() + ";" + (int) e.getClickedBlock().getLocation().getX()
								+ ";" + (int) e.getClickedBlock().getLocation().getY() + ";"
								+ (int) e.getClickedBlock().getLocation().getZ());
				try {
					configEvento.save(new File(HEventos.getHEventos().getDataFolder() + File.separator + "Eventos"
							+ File.separator + "minamortal.yml"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.getPlayer().sendMessage("§4[Evento] §cLocalizacao 2 da mina setada!");
				e.setCancelled(true);
			}
		} else if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().contains("BowSpleef")) {
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				File fileEvento = new File(
						HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Eventos/bowspleef.yml");
				YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
				configEvento.set("Localizacoes.Chao_1",
						e.getClickedBlock().getWorld().getName() + ";" + (int) e.getClickedBlock().getLocation().getX()
								+ ";" + (int) e.getClickedBlock().getLocation().getY() + ";"
								+ (int) e.getClickedBlock().getLocation().getZ());
				try {
					configEvento.save(new File(HEventos.getHEventos().getDataFolder() + File.separator + "Eventos"
							+ File.separator + "bowspleef.yml"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.getPlayer().sendMessage("§4[Evento] §cLocalizacao 1 do bowspleef setada!");
				e.setCancelled(true);
			} else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				File fileEvento = new File(
						HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Eventos/bowspleef.yml");
				YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
				configEvento.set("Localizacoes.Chao_2",
						e.getClickedBlock().getWorld().getName() + ";" + (int) e.getClickedBlock().getLocation().getX()
								+ ";" + (int) e.getClickedBlock().getLocation().getY() + ";"
								+ (int) e.getClickedBlock().getLocation().getZ());
				try {
					configEvento.save(new File(HEventos.getHEventos().getDataFolder() + File.separator + "Eventos"
							+ File.separator + "bowspleef.yml"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.getPlayer().sendMessage("§4[Evento] §cLocalizacao 2 do bowspleef setada!");
				e.setCancelled(true);
			}

		} else if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().contains("Frog")) {
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				File fileEvento = new File(
						HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Eventos/frog.yml");
				YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
				configEvento.set("Localizacoes.Chao_1",
						e.getClickedBlock().getWorld().getName() + ";" + (int) e.getClickedBlock().getLocation().getX()
								+ ";" + (int) e.getClickedBlock().getLocation().getY() + ";"
								+ (int) e.getClickedBlock().getLocation().getZ());
				try {
					configEvento.save(new File(HEventos.getHEventos().getDataFolder() + File.separator + "Eventos"
							+ File.separator + "frog.yml"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.getPlayer().sendMessage("§4[Evento] §cLocalizacao 1 do frog setada!");
				e.setCancelled(true);
			} else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				File fileEvento = new File(
						HEventos.getHEventos().getDataFolder().getAbsolutePath() + "/Eventos/frog.yml");
				YamlConfiguration configEvento = YamlConfiguration.loadConfiguration(fileEvento);
				configEvento.set("Localizacoes.Chao_2",
						e.getClickedBlock().getWorld().getName() + ";" + (int) e.getClickedBlock().getLocation().getX()
								+ ";" + (int) e.getClickedBlock().getLocation().getY() + ";"
								+ (int) e.getClickedBlock().getLocation().getZ());
				try {
					configEvento.save(new File(HEventos.getHEventos().getDataFolder() + File.separator + "Eventos"
							+ File.separator + "frog.yml"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.getPlayer().sendMessage("§4[Evento] §cLocalizacao 2 do frog setada!");
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onSignChangeEvent(SignChangeEvent e) {
		if (e.getPlayer().hasPermission("heventos.admin")) {
			if (e.getLine(0).equalsIgnoreCase("[Evento]")) {
				e.setLine(0, "§9[Evento]");
				e.getPlayer().sendMessage("§4[Evento] §cPlaca criada com sucesso!");
			}
		}
	}

}
