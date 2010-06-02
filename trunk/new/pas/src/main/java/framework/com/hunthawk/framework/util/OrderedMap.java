/**
 * 
 */
package com.hunthawk.framework.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.ListOrderedMap;

/**
 * @author BruceSun
 * 
 */
public class OrderedMap<K, V> implements Map<K, V> {

	private ListOrderedMap map = new ListOrderedMap();

	public void clear() {
		map.clear();

	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	public V get(Object key) {
		return (V) map.get(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public V put(K key, V value) {
		return (V) map.put(key, value);

	}

	public void putAll(Map<? extends K, ? extends V> t) {
		map.putAll(t);

	}

	public V remove(Object key) {
		return (V) map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection<V> values() {
		return map.values();
	}

	public static void main(String[] args) {
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		// Map map = new TreeMap();
		map.put("���ݺ�����", 2);
		map.put("����������", 3);
		map.put("��Ӫ��Ա", 1);

		for (Object key : map.keySet()) {
			System.out.println(key);
		}
	}
}
