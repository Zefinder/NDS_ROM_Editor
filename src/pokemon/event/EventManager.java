package pokemon.event;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {

	private static final EventManager instance = new EventManager();

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
					Class<?>[] interfaces = parameters[0].getType().getInterfaces();
					// Check if one is the Event interface
					for (Class<?> interfaze : interfaces) {
						if (Event.class.isAssignableFrom(interfaze)) {
							listenersMap
									.computeIfAbsent((Class<? extends Event>) parameters[0].getType(),
											key -> new ArrayList<RegisteredListener>())
									.add(new RegisteredListener(classListener, method));
							break;
						}
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
			listener.fireChange(event);
		}
	}

	public static EventManager getInstance() {
		return instance;
	}

}
