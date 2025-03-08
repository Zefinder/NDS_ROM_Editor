package pokemon.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MapBuilder<K, V> {

	private List<Map.Entry<? extends K, ? extends V>> entries;
	
	public MapBuilder() {
		entries = new ArrayList<Map.Entry<? extends K,? extends V>>();
	}
	
	public MapBuilder<K, V> put(K key, V value) {
		entries.add(new MapEntry<K, V>(key, value));
		return this;
	}
	
	public Map<K, V> build() {
		Map<K, V> map = new HashMap<K, V>();
		for (Entry<? extends K, ? extends V> entry : entries) {
			map.put(entry.getKey(), entry.getValue());
		}
		
		return Collections.unmodifiableMap(map);
	}
	
	private static final class MapEntry<K, V> implements Map.Entry<K, V> {

		private K key;
		private V value;
		
		public MapEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			return this.value = value;
		}
		
	}
	
}
