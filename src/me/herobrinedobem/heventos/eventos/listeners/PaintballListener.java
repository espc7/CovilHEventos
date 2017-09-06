package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseListener;
import me.herobrinedobem.heventos.api.events.PlayerLoseEvent;
import me.herobrinedobem.heventos.eventos.Paintball;

public class PaintballListener extends EventoBaseListener {

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if ((HEventos.getHEventos().getEventosController().getEvento().isAberto()))
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().isOcorrendo())
			return;
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if (!HEventos.getHEventos().getEventosController().getEvento().getParticipantes().contains(p))
				return;
			e.setCancelled(true);
		} else if (e.getDamager() instanceof Arrow) {
			Arrow projectile = (Arrow) e.getDamager();
			if (projectile.getShooter() instanceof Player) {
				Player atirou = (Player) projectile.getShooter();
				Player atingido = (Player) e.getEntity();
				Paintball paintball = (Paintball) HEventos.getHEventos().getEventosController().getEvento();
				if (!(HEventos.getHEventos().getEventosController().getEvento().getParticipantes().contains(atingido)
						&& HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
								.contains(atirou)))
					return;
				if (paintball.getTimeAzul().contains(atirou)) {
					if (!paintball.getTimeVermelho().contains(atingido)) {
						e.setCancelled(true);
						return;
					}
					eliminar(paintball, atirou, atingido);
					for (Player p1 : paintball.getParticipantes()) {
						p1.sendMessage(paintball.getConfig().getString("Mensagens.FoiEliminado").replace("&", "§")
								.replace("$atirador$", ChatColor.DARK_BLUE + atirou.getName())
								.replace("$eliminado$", ChatColor.DARK_RED + atingido.getName())
								.replace("$EventoName$", paintball.getNome()));
					}
				} else if (paintball.getTimeVermelho().contains(atirou)) {
					if (!paintball.getTimeAzul().contains(atingido)) {
						e.setCancelled(true);
						return;
					}
					eliminar(paintball, atirou, atingido);
					for (Player p1 : paintball.getParticipantes()) {
						p1.sendMessage(paintball.getConfig().getString("Mensagens.FoiEliminado").replace("&", "§")
								.replace("$atirador$", ChatColor.DARK_RED + atirou.getName())
								.replace("$eliminado$", ChatColor.DARK_BLUE + atingido.getName())
								.replace("$EventoName$", paintball.getNome()));
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerLoseEvent(PlayerLoseEvent e) {
		Paintball paintball = (Paintball) HEventos.getHEventos().getEventosController().getEvento();
		if (paintball.getTimeVermelho().contains(e.getPlayer())) {
			paintball.getTimeVermelho().remove(e.getPlayer());
		} else if (paintball.getTimeAzul().contains(e.getPlayer())) {
			paintball.getTimeAzul().remove(e.getPlayer());
		}
		if (HEventos.getHEventos().getSc() != null) {
			paintball.getClans().remove(HEventos.getHEventos().getSc().getClanManager().getClanPlayer(e.getPlayer().getName()));
			HEventos.getHEventos().getSc().getClanManager().getClanPlayer(e.getPlayer()).setFriendlyFire(false);
		}
	}
	
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if (HEventos.getHEventos().getEventosController().getEvento().isAberto())
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
				.contains(e.getView().getPlayer()))
			return;
		e.setCancelled(true);
	}

	public void eliminar(Paintball paintball, Player atirou, Player atingido) {
		atingido.setHealth(20.0);
		PlayerLoseEvent event = new PlayerLoseEvent(atingido,
				HEventos.getHEventos().getEventosController().getEvento());
		HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
		HEventos.getHEventos().getEconomy().depositPlayer(atirou.getName(),
				paintball.getConfig().getDouble("Premios.Money_PerKill"));
		atingido.sendMessage(HEventos.getHEventos().getEventosController().getEvento().getConfig()
				.getString("Mensagens.Eliminado").replace("&", "§").replace("$player$", atirou.getName())
				.replace("$EventoName$", paintball.getNome()));
		atirou.sendMessage(HEventos.getHEventos().getEventosController().getEvento().getConfig()
				.getString("Mensagens.Eliminou").replace("&", "§").replace("$player$", atingido.getName())
				.replace("$EventoName$", paintball.getNome()));
	}
}
