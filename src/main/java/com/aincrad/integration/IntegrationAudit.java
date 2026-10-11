package com.aincrad.integration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Audits all Fabric-Paper integration sync channels.
 * <p>
 * Verifies that each sync class is properly wired with serialize/deserialize
 * methods, collect/fetch methods, and persist/load methods.
 * </p>
 */
public final class IntegrationAudit {

    private IntegrationAudit() {}

    public static void main(String[] args) {
        System.out.println("=== Aincrad Integration Audit ===\n");

        boolean allPassed = true;

        allPassed &= auditSyncClass(PlayerDataSync.class, "PlayerData");
        allPassed &= auditSyncClass(GuildSync.class, "GuildData");
        allPassed &= auditSyncClass(DungeonSync.class, "DungeonData");
        allPassed &= auditSyncClass(EconomySync.class, "EconomyData");
        allPassed &= auditSyncClass(PartySync.class, "PartyData");
        allPassed &= auditSyncClass(QuestSync.class, "QuestProgressData");

        System.out.println("\n=== Integration Audit Summary ===");
        if (allPassed) {
            System.out.println("ALL SYNC CHANNELS PASSED");
        } else {
            System.out.println("SOME SYNC CHANNELS HAVE ISSUES — see above");
            System.exit(1);
        }
    }

    private static boolean auditSyncClass(Class<?> clazz, String expectedRecordName) {
        System.out.println("--- " + clazz.getSimpleName() + " ---");

        List<String> issues = new ArrayList<>();

        // Check the class is not null / exists
        if (clazz == null) {
            issues.add("Class not found");
            reportIssues(issues);
            return false;
        }

        // Check for record type
        boolean hasRecord = false;
        for (Class<?> inner : clazz.getDeclaredClasses()) {
            if (inner.isRecord() && inner.getSimpleName().equals(expectedRecordName)) {
                hasRecord = true;
                break;
            }
        }
        if (!hasRecord) {
            issues.add("Missing record type: " + expectedRecordName);
        }

        // Check for serialize method
        boolean hasSerialize = hasMethod(clazz, "serialize");
        if (!hasSerialize) {
            issues.add("Missing serialize() method");
        }

        // Check for deserialize method
        boolean hasDeserialize = hasMethod(clazz, "deserialize");
        if (!hasDeserialize) {
            issues.add("Missing deserialize() method");
        }

        // Check for collect method
        boolean hasCollect = hasMethod(clazz, "collect");
        if (!hasCollect) {
            issues.add("Missing collect() method");
        }

        // Check for fetchRaw method
        boolean hasFetchRaw = hasMethod(clazz, "fetchRaw");
        if (!hasFetchRaw) {
            issues.add("Missing fetchRaw() method");
        }

        // Check for persist method
        boolean hasPersist = hasMethod(clazz, "persist");
        if (!hasPersist) {
            issues.add("Missing persist() method");
        }

        // Check for loadFromManager method
        boolean hasLoadFromManager = hasMethod(clazz, "loadFromManager");
        if (!hasLoadFromManager) {
            issues.add("Missing loadFromManager() method");
        }

        // Check for load method (PlayerDataSync has load, others may not)
        boolean hasLoad = hasMethod(clazz, "load");
        if (!hasLoad) {
            issues.add("Missing load() method");
        }

        if (issues.isEmpty()) {
            System.out.println("  PASSED (all methods present)");
            System.out.println();
            return true;
        } else {
            reportIssues(issues);
            return false;
        }
    }

    private static boolean hasMethod(Class<?> clazz, String methodName) {
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(methodName) && !m.isSynthetic()) {
                return true;
            }
        }
        return false;
    }

    private static void reportIssues(List<String> issues) {
        for (String issue : issues) {
            System.out.println("  ISSUE: " + issue);
        }
        System.out.println();
    }
}
