package com.aincrad.enhance;

/**
 * Result of an enhancement attempt.
 */
public enum EnhancementResult {
    /** Enhancement succeeded, item level increased by 1. */
    SUCCESS,
    /** Enhancement failed, item level unchanged. */
    FAIL,
    /** Enhancement failed and the item was destroyed. */
    BREAK
}
