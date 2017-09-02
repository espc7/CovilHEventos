package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PotionSplashEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseListener;

public class MinaMortalListener extends EventoBaseListener {

	@EventHandler
	public void onPotionSplashEvent(PotionSplashEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() != null) {
			if (e.getPotion().getShooter() instanceof Player) {
				Player p = (Player) e.getPotion().getShooter();
				if (HEventos.getHEventos().getEventosController().getEvento().getCamarotePlayers().contains(p)) {
					e.setCancelled(true);
				}
			}
		}
	}
}
