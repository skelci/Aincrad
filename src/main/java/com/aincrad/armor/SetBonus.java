package com.aincrad.armor;

import com.aincrad.combat.stat.StatType;

public record SetBonus(int piecesRequired, StatType stat, double bonus, String description) {}
