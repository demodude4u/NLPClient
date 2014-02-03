package io.github.nolifedev.nlp.client.util;

import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleEntry<K, V> implements Entry<K, V> {
	private final K key;
	private V value;

	@Override
	public V setValue(V value) {
		V prevValue = this.value;
		this.value = value;
		return prevValue;
	}
}
