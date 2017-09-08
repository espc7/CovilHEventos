package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoBaseListener;
import me.herobrinedobem.heventos.api.events.PlayerLoseEvent;
import me.herobrinedobem.heventos.api.events.PlayerWinEvent;
import me.herobrinedobem.heventos.eventos.Frog;

public class FrogListener extends EventoBaseListener {

	private EventoBaseAPI evento;

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		evento = HEventos.getHEventos().getEventosController().getEvento();
		if (evento == null)
			return;
		if (!evento.getParticipantes().contains(e.getPlayer()))
			return;
		if (!evento.isOcorrendo())
			return;
		if (evento.isAberto())
			return;
		Frog frog = (Frog) evento;
		if ((e.getPlayer().getLocation().getY() <= frog.getY()) && frog.getEtapa() != 1) {
			e.getPlayer().sendMessage(frog.getConfig().getString("Mensagens.VcFoiEliminado").replace("&", "§")
					.replace("$EventoName$", frog.getNome()));
			PlayerLoseEvent event = new PlayerLoseEvent(e.getPlayer(), evento);
			HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
			String msg = frog.getConfig().getString("Mensagens.FoiEliminado");
			for (Player p : frog.getParticipantes()) {
				p.sendMessage(msg.replace("&", "§").replace("$EventoName$", frog.getNome()).replace("$player$",
						e.getPlayer().getName()));

			}
		}
		if (frog.getEtapa() != 6)
			return;
		Block b = frog.getLaVermelha().getWorld().getBlockAt(frog.getLaVermelha());
		if (!(e.getTo().getBlock().getX() == b.getX())) {
			return;
		}
		if (!(e.getTo().getBlock().getZ() == b.getZ())) {
			return;
		}
		if (!(e.getTo().getBlock().getY() == b.getY() + 1)) {
			return;
		}
		PlayerWinEvent event = new PlayerWinEvent(e.getPlayer(), evento, false);
		HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
		frog.stopEvent();
	}
}
