package com.pdoyle.nanameue.app.storage

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

interface ActivityCache {

    /**
     * @return item fom the cache or null not item for the given key
     */
    fun <T> get(key: String): T?

    /**
     * @return item fom the cache or the defaultValue for the given key
     */
    fun <T> getOrDefault(key: String, defaultValue: T): T

    /**
     * Gets and item from the cache. If the cache does not have am item for the given key
     * the putBlock will be called and its return value will be stored in the cache and returned
     *
     * @see MutableMap.getOrPut
     *
     * @return item fom the cache or the putBlock for the given key
     */
    fun <T> getOrPut(key: String, putBlock: () -> T): T

    /**
     * puts a key/value pair in the cache
     */
    fun <T> put(key: String, data: T?): T?

    /**
     * clears the cache
     */
    fun clear()

    /**
     * removes a key/value pair from the cache
     */
    fun remove(key: String)

    /**
     * @return true the cache contains an key, false otherwise
     */
    fun contains(key: String): Boolean
}

/**
 * Memory cache that is limited to an activity and will be destroyed when that activity is
 * this will be preserved across config changes. Can hold multiple pieces of data unlike livedata
 */
class DefaultActivityCache : Fragment(), ActivityCache {

    companion object {

        private const val FRAGMENT_TAG = "default_activity_cache"

        /**
         * @return true the cache for this activity, will be cleared when the user
         * leaves the activity
         */
        @JvmStatic
        fun get(activity: FragmentActivity): DefaultActivityCache {
            val fragmentManager = activity.supportFragmentManager
            val fragment: Fragment? = fragmentManager.findFragmentByTag(FRAGMENT_TAG) ?: DefaultActivityCache()
            return if (fragment != null && fragment is DefaultActivityCache) {
                fragment
            } else {
                val newFragment = DefaultActivityCache()
                fragmentManager.beginTransaction().add(newFragment, FRAGMENT_TAG).commit()
                newFragment
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    private val map: MutableMap<String, Any?> = HashMap()

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
            map[key] = putData
            putData
        }
    }

    override fun <T> put(key: String, data: T?): T? {
        map[key] = data as Any?
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