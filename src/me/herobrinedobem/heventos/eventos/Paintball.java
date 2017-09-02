package me.herobrinedobem.heventos.eventos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoCancellType;
import me.herobrinedobem.heventos.api.EventoUtils;
import me.herobrinedobem.heventos.api.events.StopEvent;
import me.herobrinedobem.heventos.api.events.TeamWinEvent;
import me.herobrinedobem.heventos.eventos.listeners.PaintballListener;
import me.herobrinedobem.heventos.utils.BukkitEventHelper;

public class Paintball extends EventoBaseAPI {

	private PaintballListener listener;
	private List<Player> timeAzul = new ArrayList<Player>();
	private List<Player> timeVermelho = new ArrayList<Player>();
	private Set<String> clans = new HashSet<String>();

	public Paintball(YamlConfiguration config) {
		super(config);
		listener = new PaintballListener();
		HEventos.getHEventos().getServer().getPluginManager().registerEvents(listener, HEventos.getHEventos());
	}

	@Override
	public void startEventMethod() {
		Collections.shuffle(getParticipantes());
		for (int i = 0; i < getParticipantes().size(); ++i) {
			Player b = getParticipantes().get(i);
			if (i % 2 == 0) {
				getTimeAzul().add(b);
			} else {
				getTimeVermelho().add(b);
			}
		}
		for (Player p : getParticipantes()) {
			darKit(p);
			Location Locvermelho = EventoUtils.getLocation(getConfig(), "Localizacoes.Pos_1");
			Location Locazul = EventoUtils.getLocation(getConfig(), "Localizacoes.Pos_2");
			Locazul.setY(Locazul.getY() + 1);
			Locvermelho.setY(Locvermelho.getY() + 1);
			if (timeAzul.contains(p)) {
				p.teleport(Locazul);
			} else if (timeVermelho.contains(p)) {
				p.teleport(Locvermelho);
			}
			if (HEventos.getHEventos().getSc() != null) {
				if (HEventos.getHEventos().getSc().getClanManager().getClanPlayer(p) != null) {
					HEventos.getHEventos().getSc().getClanManager().getClanPlayer(p)
							.setFriendlyFire(true);
					clans.add(HEventos.getHEventos().getSc().getClanManager().getClanPlayer(p).getClan().getTag());
				}
			} else if (HEventos.getHEventos().getCore() != null) {
				if (HEventos.getHEventos().getCore().getClanPlayerManager().getClanPlayer(p) != null) {
					HEventos.getHEventos().getCore().getClanPlayerManager().getClanPlayer(p)
							.setFriendlyFire(true);
					clans.add(HEventos.getHEventos().getSc().getClanManager().getClanPlayer(p).getClan().getTag());
				}
			}
			p.setHealth(20.0);
		}
	}

	@Override
	public void scheduledMethod() {
		if ((isOcorrendo() == true) && (isAberto() == false)) {
			if (this.timeVermelho.isEmpty()) {
				String time = "Azul";
				TeamWinEvent event1 = new TeamWinEvent(time, this, getTimeAzul());
				HEventos.getHEventos().getServer().getPluginManager().callEvent(event1);
				getTimeAzul().clear();
				getTimeVermelho().clear();
				stopEvent();
			} else if (this.timeAzul.isEmpty()) {
				String time = "Vermelho";
				TeamWinEvent event1 = new TeamWinEvent(time, this, getTimeVermelho());
				HEventos.getHEventos().getServer().getPluginManager().callEvent(event1);
				getTimeAzul().clear();
				getTimeVermelho().clear();
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
		for (String string : clans) {
			if (HEventos.getHEventos().getSc() != null) {
				HEventos.getHEventos().getSc().getClanManager().getClan(string).setFriendlyFire(false);
			} else if (HEventos.getHEventos().getCore() != null) {
				HEventos.getHEventos().getCore().getClanManager().getClan(string).setFriendlyFire(false);
			}
		}
		clans.clear();
		super.resetEvent();
		BukkitEventHelper.unregisterEvents(listener, HEventos.getHEventos());
	}

	private void darKit(Player player) {
		ItemStack arco = new ItemStack(Material.BOW, 1);
		arco.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		if (getTimeAzul().contains(player)) {
			player.getInventory().addItem(arco);
			player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
			ItemStack lhelmet = new ItemStack(Material.LEATHER_HELMET, 1);
			LeatherArmorMeta lam = (LeatherArmorMeta) lhelmet.getItemMeta();
			lam.setColor(Color.BLUE);
			lhelmet.setItemMeta(lam);
			ItemStack lChest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
			LeatherArmorMeta lcm = (LeatherArmorMeta) lChest.getItemMeta();
			lcm.setColor(Color.BLUE);
			lChest.setItemMeta(lcm);
			ItemStack lLegg = new ItemStack(Material.LEATHER_LEGGINGS, 1);
			LeatherArmorMeta llg = (LeatherArmorMeta) lLegg.getItemMeta();
			llg.setColor(Color.BLUE);
			lLegg.setItemMeta(llg);
			ItemStack lBoots = new ItemStack(Material.LEATHER_BOOTS, 1);
			LeatherArmorMeta lbo = (LeatherArmorMeta) lBoots.getItemMeta();
			lbo.setColor(Color.BLUE);
			lBoots.setItemMeta(lbo);
			player.getInventory().setHelmet(lhelmet);
			player.getInventory().setChestplate(lChest);
			player.getInventory().setLeggings(lLegg);
			player.getInventory().setBoots(lBoots);
			player.updateInventory();
		} else {
			player.getInventory().addItem(arco);
			player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
			ItemStack lhelmet = new ItemStack(Material.LEATHER_HELMET, 1);
			LeatherArmorMeta lam = (LeatherArmorMeta) lhelmet.getItemMeta();
			lam.setColor(Color.RED);
			lhelmet.setItemMeta(lam);
			ItemStack lChest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
			LeatherArmorMeta lcm = (LeatherArmorMeta) lChest.getItemMeta();
			lcm.setColor(Color.RED);
			lChest.setItemMeta(lcm);
			ItemStack lLegg = new ItemStack(Material.LEATHER_LEGGINGS, 1);
			LeatherArmorMeta llg = (LeatherArmorMeta) lLegg.getItemMeta();
			llg.setColor(Color.RED);
			lLegg.setItemMeta(llg);
			ItemStack lBoots = new ItemStack(Material.LEATHER_BOOTS, 1);
			LeatherArmorMeta lbo = (LeatherArmorMeta) lBoots.getItemMeta();
			lbo.setColor(Color.RED);
			lBoots.setItemMeta(lbo);
			player.getInventory().setHelmet(lhelmet);
			player.getInventory().setChestplate(lChest);
			player.getInventory().setLeggings(lLegg);
			player.getInventory().setBoots(lBoots);
			player.updateInventory();
		}
	}

	public List<Player> getTimeAzul() {
		return this.timeAzul;
	}

	public List<Player> getTimeVermelho() {
		return this.timeVermelho;
	}
}
