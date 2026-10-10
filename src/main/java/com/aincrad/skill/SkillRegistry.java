package com.aincrad.skill;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Static registry for all skills in Aincrad.
 * <p>
 * Skills are registered by their unique ID and can be looked up via
 * {@link #get(String)} or {@link #getAll()}.
 * </p>
 */
public final class SkillRegistry {

    private static final Map<String, Skill> SKILLS = new HashMap<>();

    private SkillRegistry() {
        // Prevent instantiation
    }

    /**
     * Registers a skill. Duplicate IDs will be rejected.
     *
     * @param skill the skill to register
     * @throws IllegalArgumentException if a skill with the same ID already exists
     */
    public static void register(Skill skill) {
        String id = skill.getId();
        if (SKILLS.containsKey(id)) {
            throw new IllegalArgumentException("Skill with ID '" + id + "' is already registered");
        }
        SKILLS.put(id, skill);
    }

    /**
     * Retrieves a skill by its ID.
     *
     * @param id the skill ID
     * @return the skill, or {@code null} if not found
     */
    public static Skill get(String id) {
        return SKILLS.get(id);
    }

    /**
     * Returns all registered skills.
     *
     * @return unmodifiable collection of all skills
     */
    public static Collection<Skill> getAll() {
        return Collections.unmodifiableCollection(SKILLS.values());
    }

    /**
     * Returns the number of registered skills.
     *
     * @return the skill count
     */
    public static int size() {
        return SKILLS.size();
    }

    /**
     * Clears all registered skills (primarily for testing).
     */
    public static void clear() {
        SKILLS.clear();
    }
}
