package com.pdoyle.nanameue.app.storage

import okio.*
import okio.ByteString.Companion.encode
import okio.ByteString.Companion.encodeUtf8
import timber.log.Timber
import java.io.File
import java.lang.reflect.Type
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * Advanced fast and efficient storage that removes the need for wrappers. Different objects can be stored
 * by creating a new StreamSerializer implementations that fills the write and read methods.
 *
 *
 * This is a CoreStorage System without a limit, make sure only to store truly long term storage here.
 * On Android this is created in the Files dir.
 *
 *
 * **Version 1**
 * <pre>
 * +--------------------------------------------------+
 * | 32Bit Int - Version name                         |
 * +--------------------------------------------------+
 * | 32Bit Int - Key length                           |
 * +--------------------------------------------------+
 * | UTF8 Encoded Base64 ByteString - Key             |
 * +--------------------------------------------------+
 * | 64Bit Long - Updated at Date                     |
 * +--------------------------------------------------+
 * |                                                  |
 * |                 Data Contents                    |
 * |                                                  |
 * +--------------------------------------------------+
</pre> *
 *
 *
 */

private const val VERSION = 1
private const val ENTRY_FILE_TEMPLATE = "%s.entry"

//master lock for managing the entire directory, prevents clearing the dir from causing issues reading and writing
private val masterLock = Any()

internal class DefaultCoreStorage(private val dir: File,
                                  private val memoryCache: MemoryCache,
                                  private val defaultSerializer: StreamSerializer
) : CoreStorage {

    //Locks for each file, ReadWriteLock allows multiple reads and only one writing at a time
    private val locks = Collections.synchronizedMap(HashMap<String, ReadWriteLock>())

    init {
        if (!dir.exists() || !dir.isDirectory) {
            throw IllegalArgumentException("The dir passed to storage must be a directory and must exist!!!")
        }
    }

    /**
     * Returns an entry for the given key.
     *
     * @return the entry if found, null if missing.
     */
    override fun <T> get(key: String, type: Type): T? {

        when {
            //Check memory memoryCache for entry
            memoryCache.contains(key) -> {
                @Suppress("UNCHECKED_CAST")
                return memoryCache.get<Any>(key) as T?
            }
            //Check if there is an entry on disk
            contains(key) -> {
                //only lock for that entry, allow multiaccess to disk
                val entry = readEntry<T>(key, type)
                return if (entry != null) {
                    //Entry found, add it to the memory memoryCache
                    memoryCache.put(key, entry)
                    entry
                } else {
                    null
                }
            }
            else -> {
                //no entry, make sure any entry remains is removed
                remove(key)
                return null
            }
        }
    }

    override fun <T> getWithMigration(key: String, type: Type, migration: CoreStorage.Migration<T>): T? {
        val data = get<T>(key, type)
        if (data != null) {
            return data
        }

        val migrated = migration.get()
        return if (migrated != null) {
            put(key, migrated)
            migration.remove()
            migrated
        } else {
            get<T>(key, type)
        }
    }

    override fun <T> get(key: String, defaultData: T, type: Type): T {
        val entry = get<T>(key, type)
        return entry ?: defaultData
    }

    /**
     * Put an entry into the memoryCache.
     *
     * @param key  the key to store
     * @param data entry to store
     * @return true if the entry was written
     */
    override fun <T> put(key: String, data: T): Boolean {
        memoryCache.put(key, data)
        return writeDiskEntry(key, data)
    }


    /**
     * Checks if an entry is present
     *
     * @return true if the entry is present
     */
    override fun contains(key: String): Boolean {
        return try {
            memoryCache.contains(key) || inReadLock(key) { getEntryFile(key).exists() }
        } catch (e: Exception) {
            false
        }

    }

    /**
     * Remove an entry from the storage
     */
    override fun remove(key: String) {
        try {
            inWriteLock<Any>(key) {
                getEntryFile(key).delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        locks.remove(key)
        memoryCache.remove(key)
    }

    /**
     * Deletes the entire memoryCache and stats fresh
     */
    override fun clear() {
        synchronized(masterLock) {
            dir.listFiles()?.forEach {
                deleteFileAndLog(it)
            }
        }
        memoryCache.clear()
        locks.clear()
    }

    override fun getKeys(): List<String> {
        synchronized(masterLock) {
            val entries = dir.listFiles()
            val keys = ArrayList<String>()
            entries?.forEach {
                try {
                    val header = StorageHeader.read(it.source().buffer())
                    keys.add(header.key)
                } catch (e : Exception) {
                    deleteFileAndLog(it)
                }
            }
            return keys
        }
    }

    //Gets the file for the key, key is base64 url encoded to prevent any
    //illegal chars sneaking in
    private fun getEntryFile(key: String): File {
        val byteString = key.encode(Charset.forName("UTF-8"))
        val entryName = byteString.base64Url()
        return File(dir, String.format(Locale.US, ENTRY_FILE_TEMPLATE, entryName))
    }

    private fun rwLockForKey(key: String): ReadWriteLock {
        return locks.getOrPut(key) { ReentrantReadWriteLock() }
    }

    @Throws(Exception::class)
    private fun <T> inReadLock(key: String, func: () -> T): T {
        synchronized(masterLock) {
            var lock: Lock? = null
            try {
                val rwLock = rwLockForKey(key)
                lock = rwLock.readLock()
                lock.lock()
                return func()
            } finally {
                lock?.unlock()
            }
        }
    }

    @Throws(Exception::class)
    private fun <T> inWriteLock(key: String, func: () -> T): T {
        synchronized(masterLock) {
            var lock: Lock? = null
            try {
                val rwLock = rwLockForKey(key)
                lock = rwLock.writeLock()
                lock.lock()
                return func()
            } finally {
                lock?.unlock()
            }
        }
    }

    private fun <T> readEntry(key: String, type: Type): T? {
        return try {
            inReadLock(key) {
                getEntryFile(key).source().buffer().use { closable ->
                    StorageHeader.read(closable) //Read storageHeader
                    defaultSerializer.read<T>(closable, type) //read entry
                }
            }
        } catch (e: Exception) {
            //on error reading remove the entry
            Timber.w(e,"Error reading entry for key - %s, deleting entry...", key)
            remove(key)
            null
        }

    }

    private fun <T> writeDiskEntry(key: String, entry: T): Boolean {
        try {
            return inReadLock(key) {
                getEntryFile(key).sink().buffer().use { closable ->
                    val storageHeader = StorageHeader(VERSION, key, Date(System.currentTimeMillis()))
                    storageHeader.write(closable) //Write storageHeader
                    defaultSerializer.write(entry as Any, closable) //write entry
                    closable.flush()
                }
                true
            }
        } catch (e: Exception) {
            //on error writing remove the entry
            remove(key)
            Timber.w(e,"Error writing entry for key - %s", key)
            return false
        }

    }

    /**
     * Header for entries, this contains metadata such as the version of the memoryCache and the
     * date created.
     */
    internal class StorageHeader(val version: Int, val key: String, val created: Date) {

        //Write the entryHeader to disk, do NOT close the stream
        @Throws(Exception::class)
        fun write(sink: BufferedSink) {
            sink.writeInt(version)

            //write key value
            val keyData = key.encodeUtf8()
            sink.writeInt(keyData.size)
            sink.write(keyData)
            sink.writeLong(created.time)
            sink.flush()
        }

        companion object {

            @Throws(Exception::class)
            fun read(source: BufferedSource): StorageHeader {
                //first 4 bytes are always the version (32bit int)
                when (val version = source.readInt()) {
                    1 -> {
                        //read back key value
                        val keyStringLength = source.readInt()
                        val byteStringKey = source.readByteString(keyStringLength.toLong())
                        val key = byteStringKey.utf8()

                        val created = Date(source.readLong())
                        return StorageHeader(version, key, created)
                    }
                    else -> throw IllegalArgumentException(
                            "Error reading file storageHeader, verson - [$version] not recognised")
                }
            }
        }
    }
}

private fun deleteFileAndLog(file: File?) {
    if (file != null && file.exists()) {
        file.delete()
        Timber.w("deleted file %s", file.name)
    }
}
