package com.aincrad.armor;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry mapping item IDs to ArmorPiece instances.
 * ArmorPiece instances register themselves in their constructor.
 */
public final class ArmorPieceRegistry {
    private static final Map<String, ArmorPiece> REGISTRY = new HashMap<>();

    private ArmorPieceRegistry() {}

    public static void register(ArmorPiece piece) {
        REGISTRY.put(piece.getName(), piece);
    }

    public static ArmorPiece getByName(String name) {
        return REGISTRY.get(name);
    }
}
