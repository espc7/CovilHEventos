package me.herobrinedobem.heventos.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.herobrinedobem.heventos.api.EventoBaseAPI;

public class PlayerLeaveEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private EventoBaseAPI evento;
	private boolean assistindo;
	
	public PlayerLeaveEvent(Player player, EventoBaseAPI evento, boolean assistindo) {
		this.player = player;
		this.evento = evento;
		this.assistindo = assistindo;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
		 
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public EventoBaseAPI getEvento() {
		return evento;
	}
	
	public boolean isAssistindo() {
		return assistindo;
	}

}
