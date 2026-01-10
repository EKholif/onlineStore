package com.onlineStoreCom.tenant;

/**
 * Marker interface for entities that are globally accessible by all tenants.
 * Entities implementing this interface bypass strict Tenant Data Guard checks.
 * Use for Reference Data like Countries, States, Currencies.
 */
public interface GlobalData {
}
