package com.pdoyle.nanameue.app.storage

import java.lang.reflect.Type
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Cache interface, that stores in the app cache folders. This is wiped when
 * the user clears the cache, only store temp data here
 */
interface CoreCache {

    /**
     * Returns the cache entry for a the given key if is exists, null if not found
     */
    fun <T> getEntry(key: String, type: Type): Entry<T>?

    /**
     * Returns the data contained in the cache entry for a the given key if is exists, defaultValue if not found
     */
    fun <T> get(key: String, defaultValue: T, type: Type): T

    /**
     * Returns the data contained in the cache entry for a the given key if is exists, null if not found
     */
    fun <T> get(key: String, type: Type): T?

    /**
     * Puts an entry into the cache
     */
    fun <T> put(key: String, data: Entry<T>): Boolean

    /**
     * Puts data into the cache, this entry will never expire
     */
    fun <T> put(key: String, data: T): Boolean

    /**
     * returns true if an entry is found
     */
    operator fun contains(key: String): Boolean

    /**
     * removes and entry for a given key
     */
    fun remove(key: String)

    /**
     * Clears the whole storage
     */
    fun clear()

    /**
     * Gets all the keys in the cache
     */
    fun getKeys(): List<String>

    /**
     * Wrapper around the cached data to hold metadata like expiry, created etc
     */
    class Entry<T> internal constructor(private val data: T, private val created: Date, private val expires: Date) {

        companion object {

            /**
             * Creates an entry that will never expire and is only removed from the cache by LRU means
             */
            @JvmStatic
            fun <T> noExpire(data: T): Entry<T> {
                return Entry(
                        data,
                        Date(System.currentTimeMillis()),
                        Date(java.lang.Long.MAX_VALUE - 10 /*prevent overflow and chaos*/)
                )
            }

            /**
             * Creates an entry that will expire at a certain date
             */
            @JvmStatic
            fun <T> expiresAt(data: T, expiresAt: Date): Entry<T> {
                return Entry(data, Date(System.currentTimeMillis()), expiresAt)
            }

            /**
             * Creates an entry that will expire at the time in the future
             */
            @JvmStatic
            fun <T> expiresIn(data: T, expiresIn: Long, timeUnit: TimeUnit): Entry<T> {
                return Entry(
                        data,
                        Date(System.currentTimeMillis()),
                        Date(System.currentTimeMillis() + timeUnit.toMillis(expiresIn))
                )
            }
        }

        /**
         * The data in this entry
         */
        fun data(): T {
            return data
        }

        /**
         * The date this was written to the cache
         */
        fun created(): Date {
            return created
        }

        /**
         * The date this expires, if the entry never expires this is the max data allowed
         */
        fun expires(): Date {
            return expires
        }

        fun hasExpired(): Boolean {
            return System.currentTimeMillis() > expires.time
        }

    }
}
