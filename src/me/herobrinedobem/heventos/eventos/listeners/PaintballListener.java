package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoBaseListener;
import me.herobrinedobem.heventos.api.events.PlayerLoseEvent;
import me.herobrinedobem.heventos.eventos.Paintball;

public class PaintballListener extends EventoBaseListener {

	private EventoBaseAPI evento;

	@EventHandler
	public void onEntityDamageByEntityEventPAINTBALL(EntityDamageByEntityEvent e) {
		evento = HEventos.getHEventos().getEventosController().getEvento();
		if (evento == null)
			return;
		if (!evento.isOcorrendo())
			return;
		if (evento.isAberto())
			return;
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if (!evento.getParticipantes().contains(p))
				return;
			e.setCancelled(true);
		} else if (e.getDamager() instanceof Arrow) {
			Arrow projectile = (Arrow) e.getDamager();
			if (projectile.getShooter() instanceof Player) {
				Player atirou = (Player) projectile.getShooter();
				Player atingido = (Player) e.getEntity();
				Paintball paintball = (Paintball) evento;
				if (!(evento.getParticipantes().contains(atingido) && evento.getParticipantes().contains(atirou)))
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
	public void onPlayerLoseEventPAINTBALL(PlayerLoseEvent e) {
		evento = HEventos.getHEventos().getEventosController().getEvento();
		Paintball paintball = (Paintball) evento;
		if (paintball.getTimeVermelho().contains(e.getPlayer())) {
			paintball.getTimeVermelho().remove(e.getPlayer());
		} else if (paintball.getTimeAzul().contains(e.getPlayer())) {
			paintball.getTimeAzul().remove(e.getPlayer());
		}
		if (HEventos.getHEventos().getSc() != null) {
			paintball.getClans()
					.remove(HEventos.getHEventos().getSc().getClanManager().getClanPlayer(e.getPlayer().getName()));
			HEventos.getHEventos().getSc().getClanManager().getClanPlayer(e.getPlayer()).setFriendlyFire(false);
		}
	}

	@EventHandler
	public void onInventoryClickEventPAINTBALL(InventoryClickEvent e) {
		evento = HEventos.getHEventos().getEventosController().getEvento();
		if (evento == null)
			return;
		if (!evento.getParticipantes().contains(e.getView().getPlayer()))
			return;
		if (evento.isAberto())
			return;
		e.setCancelled(true);
	}

	private void eliminar(Paintball paintball, Player atirou, Player atingido) {
		evento = HEventos.getHEventos().getEventosController().getEvento();
		atingido.setHealth(20.0);
		PlayerLoseEvent event = new PlayerLoseEvent(atingido, evento);
		HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
		HEventos.getHEventos().getEconomy().depositPlayer(atirou.getName(),
				paintball.getConfig().getDouble("Premios.Money_PerKill"));
		atingido.sendMessage(evento.getConfig().getString("Mensagens.Eliminado").replace("&", "§")
				.replace("$player$", atirou.getName()).replace("$EventoName$", paintball.getNome()));
		atirou.sendMessage(evento.getConfig().getString("Mensagens.Eliminou").replace("&", "§")
				.replace("$player$", atingido.getName()).replace("$EventoName$", paintball.getNome()));
	}
}
