package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoBaseListener;
import me.herobrinedobem.heventos.api.events.PlayerLoseEvent;
import me.herobrinedobem.heventos.eventos.Killer;

public class KillerListener extends EventoBaseListener {

	EventoBaseAPI evento;
	
	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
		evento = HEventos.getHEventos().getEventosController().getEvento();
		if (evento == null)
			return;
		if (!(e.getDamager() instanceof Player))
			return;
		if (!evento.isOcorrendo())
			return;
		if (evento.isAberto())
			return;
		Player p = (Player) e.getDamager();
		Killer killer = (Killer) evento;
		if (!evento.getParticipantes().contains(p))
			return;
		if (killer.getEtapa() != 1)
			return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerQuitEventKILLER(PlayerQuitEvent e) {
		evento = HEventos.getHEventos().getEventosController().getEvento();
		if (evento == null)
			return;
		if (!evento.isOcorrendo())
			return;
		if (evento.getParticipantes()
				.contains(e.getPlayer()))
			return;
		if (evento.isAberto())
			return;
		e.getPlayer().setHealth(0.0);
	}

	@EventHandler
	public void onPotionSplashEvent(PotionSplashEvent e) {
		evento = HEventos.getHEventos().getEventosController().getEvento();
		if (evento != null) {
			if (e.getPotion().getShooter() instanceof Player) {
				Player p = (Player) e.getPotion().getShooter();
				if (evento.getCamarotePlayers()
						.contains(p)) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerLoseEventKILLER(PlayerLoseEvent e) {
		Killer killer = (Killer) evento;
		if (HEventos.getHEventos().getSc() != null) {
			killer.getClans().remove(HEventos.getHEventos().getSc().getClanManager().getClanPlayer(e.getPlayer().getName()));
			HEventos.getHEventos().getSc().getClanManager().getClanPlayer(e.getPlayer()).setFriendlyFire(false);
		}
	}
}
