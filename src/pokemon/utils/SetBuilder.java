package pokemon.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetBuilder<T> {

	private List<T> entries;
	
	public SetBuilder() {
		entries = new ArrayList<T>();
	}
	
	public SetBuilder<T> put(T value) {
		entries.add(value);
		return this;
	}
	
	public Set<T> build() {
		Set<T> set = new HashSet<T>();
		for (T entry : entries) {
			set.add(entry);
		}
		
		return Collections.unmodifiableSet(set);
	}
	
}
