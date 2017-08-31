package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseListener;
import me.herobrinedobem.heventos.eventos.Killer;

public class KillerListener extends EventoBaseListener {

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if (!(e.getDamager() instanceof Player))
			return;
		if (HEventos.getHEventos().getEventosController().getEvento().isAberto())
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().isOcorrendo())
			return;
		Player p = (Player) e.getDamager();
		Killer killer = (Killer) HEventos.getHEventos().getEventosController().getEvento();
		if (!HEventos.getHEventos().getEventosController().getEvento().getParticipantes().contains(p.getName()))
			return;
		if (!killer.isPvpOff())
			return;
		e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if (HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
				.contains(e.getPlayer().getName()))
			return;
		if (HEventos.getHEventos().getEventosController().getEvento().isAberto())
			return;
		e.getPlayer().setHealth(0.0);
	}

	@EventHandler
	public void onPotionSplashEvent(PotionSplashEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() != null) {
			if (e.getPotion().getShooter() instanceof Player) {
				Player p = (Player) e.getPotion().getShooter();
				if (HEventos.getHEventos().getEventosController().getEvento().getCamarotePlayers()
						.contains(p.getName())) {
					e.setCancelled(true);
				}
			}
		}
	}

}
