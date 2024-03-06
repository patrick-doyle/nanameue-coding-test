package com.pdoyle.nanameue.app.storage

/**
 * Cache managed by dagger which stores objects in memory. Used to replace
 * the different caches for each type, {eg MacrosCache}
 */
abstract class MemoryCache {

    companion object {
        @JvmStatic
        fun getDefault(): MemoryCache {
            return DefaultMemoryCache()
        }
    }

    /**
     * @return item fom the cache or null not item for the given key
     */
    abstract fun <T> get(key: String): T?

    /**
     * @return item fom the cache or the defaultValue for the given key
     */
    abstract fun <T> getOrDefault(key: String, defaultValue: T): T

    /**
     * Gets and item from the cache. If the cache does not have am item for the given key
     * the putBlock will be called and its return value will be stored in the cache and returned
     *
     * @see MutableMap.getOrPut
     *
     * @return item fom the cache or the putBlock for the given key
     */
    abstract fun <T> getOrPut(key: String, putBlock: () -> T): T

    /**
     * puts a key/value pair in the cache
     */
    abstract fun <T> put(key: String, data: T): T

    /**
     * clears the cache
     */
    abstract fun clear()

    /**
     * removes a key/value pair from the cache
     */
    abstract fun remove(key: String)

    /**
     * @return true the cache contains an key, false otherwise
     */
    abstract fun contains(key: String): Boolean
}

/**
 * Default impl of the memory cache
 */
private class DefaultMemoryCache(val sizeLimit: Int = 50) : MemoryCache() {

    private val map = object: LinkedHashMap<String, Any>() {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Any>?): Boolean {
            return size >= sizeLimit
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: String): T? {
        return map[key] as? T
    }

    override fun <T> getOrDefault(key: String, defaultValue: T): T {
        return get<T>(key) ?: defaultValue
    }

    override fun <T> getOrPut(key: String, putBlock: () -> T): T {
        val data = get<T>(key)
        return if (data != null) {
            data
        } else {
            val putData = putBlock()
            map.put(key, putData!!)
            putData
        }
    }

    override fun <T> put(key: String, data: T): T {
        map[key] = data as Any
        return data
    }

    override fun clear() {
        map.clear()
    }

    override fun remove(key: String) {
        map.remove(key)
    }

    override fun contains(key: String): Boolean {
        return map.contains(key)
    }
}