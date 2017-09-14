package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PotionSplashEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoBaseListener;

public class MinaMortalListener extends EventoBaseListener {

	private EventoBaseAPI evento;
	
	@EventHandler
	public void onPotionSplashEventMINA(PotionSplashEvent e) {
		evento = HEventos.getHEventos().getEventosController().getEvento();
		if (evento != null) {
			if (e.getPotion().getShooter() instanceof Player) {
				Player p = (Player) e.getPotion().getShooter();
				if (evento.getCamarotePlayers().contains(p)) {
					e.setCancelled(true);
				}
			}
		}
	}
}
