package me.herobrinedobem.heventos.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.herobrinedobem.heventos.api.EventoBaseAPI;

public class StartEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private EventoBaseAPI evento;
	private boolean automaticStart;
	
	public StartEvent(EventoBaseAPI evento, boolean automaticStart) {
		this.evento = evento;
		this.automaticStart = automaticStart;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
		 
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public EventoBaseAPI getEvento() {
		return evento;
	}
	
	public boolean isAutomaticStart() {
		return automaticStart;
	}
	
}
