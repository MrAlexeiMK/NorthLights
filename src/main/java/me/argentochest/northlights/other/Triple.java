package me.argentochest.northlights.other;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Triple<T, V, K> {
    private T first;
    private V second;
    private K third;
}
