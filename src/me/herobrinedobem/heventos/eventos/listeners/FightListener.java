package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseListener;
import me.herobrinedobem.heventos.api.events.PlayerLoseEvent;
import me.herobrinedobem.heventos.eventos.Fight;

public class FightListener extends EventoBaseListener {

	@EventHandler
	public void onEntityDamageByEntityEventFIGHT(EntityDamageByEntityEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if (!(e.getDamager() instanceof Player))
			return;
		if (HEventos.getHEventos().getEventosController().getEvento().isAberto())
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().isOcorrendo())
			return;
		Player p = (Player) e.getDamager();
		Fight fight = (Fight) HEventos.getHEventos().getEventosController().getEvento();
		if (!HEventos.getHEventos().getEventosController().getEvento().getParticipantes().contains(p))
			return;
		if (fight.getLutador1() == e.getEntity() || fight.getLutador2() == e.getEntity()) {
			return;
		}
		if (fight.getLutador1() == e.getDamager() || fight.getLutador2() == e.getDamager()) {
			return;
		}
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerQuitEventFIGHT(PlayerQuitEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().getParticipantes().contains(e.getPlayer()))
			return;
		if (HEventos.getHEventos().getEventosController().getEvento().isAberto())
			return;
		Fight fight = (Fight) HEventos.getHEventos().getEventosController().getEvento();
		if (fight.getLutador1() != e.getPlayer() || fight.getLutador2() != e.getPlayer()) {
			return;
		}
		if (fight.getLutador1() == e.getPlayer()) {
			PlayerLoseEvent event = new PlayerLoseEvent(e.getPlayer(),
					HEventos.getHEventos().getEventosController().getEvento());
			HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
			fight.venceuLuta(fight.getLutador2());
			fight.taskLuta().cancel();
		} else if (fight.getLutador2() == e.getPlayer()) {
			PlayerLoseEvent event = new PlayerLoseEvent(e.getPlayer(), fight);
			HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
			fight.venceuLuta(fight.getLutador1());
			fight.taskLuta().cancel();
		}
		fight.setLutaOcorrendo(false);
	}

	@EventHandler
	public void onPlayerDeathEventFIGHT(PlayerDeathEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().getParticipantes().contains(e.getEntity()))
			return;
		if (HEventos.getHEventos().getEventosController().getEvento().isAberto())
			return;
		Fight fight = (Fight) HEventos.getHEventos().getEventosController().getEvento();
		if (fight.getLutador1() == e.getEntity().getPlayer() || fight.getLutador2() == e.getEntity().getPlayer()) {
			fight.venceuLuta(e.getEntity().getKiller());
			fight.taskLuta().cancel();
			fight.setLutaOcorrendo(false);
		}
	}

	@EventHandler
	public void onPlayerLoseEventFIGHT(PlayerLoseEvent e) {
		Fight fight = (Fight) HEventos.getHEventos().getEventosController().getEvento();
		if (fight.getPrimeiraRodada().contains(e.getPlayer())) {
			fight.getPrimeiraRodada().remove(e.getPlayer());
		} else if (fight.getSegundaRodada().contains(e.getPlayer())) {
			fight.getSegundaRodada().remove(e.getPlayer());
		}
		if (HEventos.getHEventos().getSc() != null) {
			fight.getClans().remove(HEventos.getHEventos().getSc().getClanManager().getClanPlayer(e.getPlayer().getName()));
			HEventos.getHEventos().getSc().getClanManager().getClanPlayer(e.getPlayer()).setFriendlyFire(false);
		}
	}
}
