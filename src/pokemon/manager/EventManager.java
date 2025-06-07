package pokemon.manager;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pokemon.event.Event;
import pokemon.event.EventListener;
import pokemon.event.RegisteredListener;

public class EventManager implements Manager {

	private static final EventManager instance = new EventManager();

	// TODO Change to set of registered listener
	private Map<Class<? extends Event>, List<RegisteredListener>> listenersMap;

	private EventManager() {
		listenersMap = new HashMap<Class<? extends Event>, List<RegisteredListener>>();
	}

	/**
	 * Registers a listener (an object) as a event listener. This will scan all
	 * functions and select the ones with the EventListener annotation, having only
	 * one parameter (implementing the Event interface)
	 * 
	 * @param classListener the object to register
	 */
	@SuppressWarnings("unchecked")
	public void registerListener(Object classListener) {
		// Extract all methods of the class listener
		Method[] classMethods = classListener.getClass().getDeclaredMethods();

		// For each method that is annotated with the EventManager, register it if the
		// argument is an Event
		for (Method method : classMethods) {
			if (method.getAnnotation(EventListener.class) != null) {
				Parameter[] parameters = method.getParameters();
				if (parameters.length == 1) {
					Class<?> eventClass = parameters[0].getType();
					// Check if implements the Event interface
					if (Event.class.isAssignableFrom(eventClass)) {
						listenersMap
								.computeIfAbsent((Class<? extends Event>) eventClass,
										_ -> new ArrayList<RegisteredListener>())
								.add(new RegisteredListener(classListener, method));
					}
				}
			}
		}
	}

	/**
	 * Throws an event
	 * 
	 * @param event the event to throw
	 */
	public void throwEvent(Event event) {
		List<RegisteredListener> listeners = listenersMap.get(event.getClass());
		if (listeners == null) {
			return;
		}

		for (RegisteredListener listener : listeners) {
			// Add priority
			listener.fireChange(event);
		}
	}

	@Override
	public void initManager() {
		// TODO 
	}
	
	public static EventManager getInstance() {
		return instance;
	}

}
