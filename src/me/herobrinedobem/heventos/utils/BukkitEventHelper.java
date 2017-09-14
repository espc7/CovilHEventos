package me.herobrinedobem.heventos.utils;

import java.lang.reflect.Method;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

public class BukkitEventHelper {

	@SuppressWarnings("unchecked")
	public static void unregisterEvents(Listener listener, Plugin plugin) {
		try {
			for (Method method : listener.getClass().getMethods()) {
				if (method.getAnnotation(EventHandler.class) != null) {
					BukkitEventHelper.unregisterEvent((Class<? extends Event>) method.getParameterTypes()[0], listener, plugin);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void unregisterEvent(Class<? extends Event> eventClass, Listener listener, Plugin plugin) {
		for (RegisteredListener regListener : HandlerList.getRegisteredListeners(plugin)) {
			if (regListener.getListener() == listener) {
				try {
					((HandlerList) eventClass.getMethod("getHandlerList").invoke(null)).unregister(regListener);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
