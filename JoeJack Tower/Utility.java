import java.util.Map;

public class Utility {
	public static <K, V> V getValueFromMap(Map<K, V> map, K key, V defaultValue) {
		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			map.put(key, defaultValue);
			return defaultValue;
		}
	}

	public static <K> void AddIntToMap(Map<K, Integer> map, K key, int value) {
		getValueFromMap(map, key, 0);
		map.put(key, getValueFromMap(map, key, 0) + value);
	}
}
