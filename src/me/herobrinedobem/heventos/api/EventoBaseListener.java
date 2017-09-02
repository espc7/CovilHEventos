package me.herobrinedobem.heventos.api;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.events.PlayerLeaveEvent;
import me.herobrinedobem.heventos.api.events.PlayerLoseEvent;

public class EventoBaseListener implements Listener {

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;

		// Players Participantes
		if (HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
				.contains(e.getEntity().getPlayer())) {
			for (Player p : HEventos.getHEventos().getEventosController().getEvento().getParticipantes()) {
				p.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgMorreu().replace("$player$",
						e.getEntity().getPlayer().getName()));
			}
			PlayerLoseEvent event2 = new PlayerLoseEvent(e.getEntity().getPlayer(),
					HEventos.getHEventos().getEventosController().getEvento());
			HEventos.getHEventos().getServer().getPluginManager().callEvent(event2);
			return;
		}

		// Players Camarote
		if (HEventos.getHEventos().getEventosController().getEvento().getCamarotePlayers()
				.contains(e.getEntity().getPlayer())) {
			PlayerLeaveEvent event = new PlayerLeaveEvent(e.getEntity().getPlayer(),
					HEventos.getHEventos().getEventosController().getEvento(), true);
			HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
			e.setNewTotalExp(e.getDroppedExp());
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if (HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
				.contains(e.getPlayer())) {
			PlayerLeaveEvent event2 = new PlayerLeaveEvent(e.getPlayer(),
					HEventos.getHEventos().getEventosController().getEvento(), false);
			HEventos.getHEventos().getServer().getPluginManager().callEvent(event2);
			for (Player p : HEventos.getHEventos().getEventosController().getEvento().getParticipantes()) {
				p.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgDesconect().replace("$player$",
						e.getPlayer().getName()));
			}
		}
		// Players Camarote
		if (HEventos.getHEventos().getEventosController().getEvento().getCamarotePlayers()
				.contains(e.getPlayer())) {
			PlayerLeaveEvent event = new PlayerLeaveEvent(e.getPlayer(),
					HEventos.getHEventos().getEventosController().getEvento(), true);
			HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
		}
	}

	@EventHandler
	public void onPlayerProccessCommandEvent(PlayerCommandPreprocessEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
				.contains(e.getPlayer()))
			return;
		for (String s : HEventos.getHEventos().getEventosController().getEvento().getConfig()
				.getStringList("Comandos_Liberados")) {
			if (!e.getMessage().startsWith(s)) {
				e.setCancelled(true);
			}
		}

	}
}
