package com.pdoyle.nanameue.app.storage

import java.lang.reflect.Type

/**
 * Storage interface, that stores in the app data folders. This is not wiped when
 * the user clears the cache
 */
interface CoreStorage {

    /**
     * get the stored data, Returns null if no data present
     * Uses the default [StreamSerializer] from the constructor
     */
    fun <T> get(key: String, type: Type): T?

    /**
     * get the stored data, Returns null if no data present
     * Uses the default [StreamSerializer] from the constructor
     */
    fun <T> getWithMigration(key: String, type: Type, migration: Migration<T>): T?

    /**
     * get the stored data, Returns default if not present
     * Uses the default [StreamSerializer] from the constructor
     */
    fun <T> get(key: String, defaultData: T, type: Type): T

    /**
     * put data into storage
     * Uses the default [StreamSerializer] from the constructor
     */
    fun <T> put(key: String, data: T): Boolean

    /**
     * returns true if an entry is found
     */
    fun contains(key: String): Boolean

    /**
     * removes and entry for a given key
     */
    fun remove(key: String)

    /**
     * Clears the whole storage
     */
    fun clear()

    /**
     * Gets all the keys in the storage
     */
    fun getKeys(): List<String>

    interface Migration<T> {

        fun get(): T?

        fun remove()
    }
}
