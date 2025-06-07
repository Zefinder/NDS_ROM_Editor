package pokemon.manager;

import java.util.Set;

import pokemon.utils.SetBuilder;

public class SuperManager implements Manager {
	
	private static final Set<Manager> HIGH_PRIO_MANAGERS = new SetBuilder<Manager>()
			.put(LogsManager.getInstance())
			.build();
	private static final Set<Manager> MEDIUM_PRIO_MANAGERS = new SetBuilder<Manager>()
			.put(EventManager.getInstance())
			.build();
	private static final Set<Manager> LOW_PRIO_MANAGERS = new SetBuilder<Manager>()
			.put(OpenedResourceManager.getInstance())
			.build();
	
	private static final SuperManager instance = new SuperManager();
	
	private SuperManager() {
	}
	
	private void printManagerInitialized(String managerName) {
		System.out.println("Manager %s has been initialized!".formatted(managerName));
	}
	
	private void initManager(Set<Manager> managers) {
		for (Manager manager : managers) {
			manager.initManager();
			printManagerInitialized(manager.getClass().getSimpleName());
		}
	}
	
	@Override
	public void initManager() {
		initManager(HIGH_PRIO_MANAGERS);
		initManager(MEDIUM_PRIO_MANAGERS);
		initManager(LOW_PRIO_MANAGERS);
	}
	
	public static SuperManager getInstance() {
		return instance;
	}
	
}
