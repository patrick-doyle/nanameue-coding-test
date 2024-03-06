package com.pdoyle.nanameue.app.storage

import androidx.annotation.VisibleForTesting
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
 * by creating a new [StreamSerializer] implementation that fills the write and read methods.
 *
 *
 * This is stored in the context.getCacheDir() by default, this can be wiped by the user without
 * affecting all the apps data.
 *
 *
 *
 *
 * The memoryCache uses LRU to limit the size. This is 10MB by default.
 *
 *
 * The memoryCache also holds the 5 most recently read items in memory to improve performance.
 *
 *
 * This is thread safe for a single instance. If two memoryCache instances are pointing to the same dir then
 * threading issues will occur.
 *
 *
 * **Version 1 entry format**
 * <pre>
 * +--------------------------------------------------+
 * | 32Bit Int - Version int                          |
 * +--------------------------------------------------+
 * | 32Bit Int - Key length                           |
 * +--------------------------------------------------+
 * | UTF8 Encoded Base64 ByteString - Key             |
 * +--------------------------------------------------+
 * | 64Bit Long - Created at Date (epoch millis)      |
 * +--------------------------------------------------+
 * | 64Bit Long - Expires at Date (epoch millis)      |
 * +--------------------------------------------------+
 * |                                                  |
 * |                 Data Contents                    |
 * |                                                  |
 * +--------------------------------------------------+
</pre> *
 */


@VisibleForTesting
internal val JOURNAL_FILE = "journal_file.journal"
private const val LOAD_FACTOR = 0.75f
private const val VERSION = 1
private const val ENTRY_FILE_TEMPLATE = "%s.entry"

internal class DefaultCoreCache(private val dir: File, //Lru memoryCache for memory disk objects, evicts the least recently read entry
                                private val memoryCache: MemoryCache,
                                private val defaultSerializer: StreamSerializer,
                                private val maxSizeBytes: Int) : CoreCache {

    //Locks for each file, ReadWriteLock allows multiple reads and only one writing at a time
    private val locks = Collections.synchronizedMap(HashMap<String, ReadWriteLock>())
    //master lock for managing the entire directory, prevents clearing the dir from causing issues reading and writing
    private val masterLock = Any()
    private val journalFile: File
    private var loadedJournal = false

    //Lru tracker for disk objects.
    private var journal: MutableList<String> = Collections.synchronizedList(LinkedList())

    init {
        if (!dir.exists() || !dir.isDirectory) {
            throw IllegalArgumentException("The dir passed to storage must be a directory and must exist!!!")
        }
        journalFile = File(dir, JOURNAL_FILE)
    }

    /**
     * Returns an entry for the given key.
     *
     * @return the entry if found, null if missing.
     */
    override fun <T> getEntry(key: String, type: Type): CoreCache.Entry<T>? {
        loadJournalIfNeeded()
        appendJournalForKey(key)

        return when {
            memoryCache.contains(key) -> { //Check memory memoryCache for entry
                memoryCache.get<CoreCache.Entry<T>>(key)?.let { data ->
                    if (data.hasExpired()) {
                        //entry expired remove it from memoryCache and disk
                        remove(key)
                        null
                    } else {
                        //not expired return entry
                        data
                    }
                } ?: run {
                    null
                }
            }
            contains(key) -> { //Check if there is an entry on disk
                readEntry<T>(key, type)?.let { entry ->
                    //Entry found, add it to the memory memoryCache
                    memoryCache.put(key, entry)
                    entry
                } ?: run {
                    null
                }
            }
            else -> {
                //no entry, make sure the memory memoryCache is removed
                memoryCache.remove(key)
                null
            }
        }
    }

    override fun <T> get(key: String, defaultValue: T, type: Type): T {
        val entry = getEntry<T>(key, type)
        return if (entry != null) entry.data() else defaultValue
    }

    override fun <T> get(key: String, type: Type): T? {
        val entry = getEntry<T>(key, type)
        return entry?.data()
    }

    override fun <T> put(key: String, data: CoreCache.Entry<T>): Boolean {
        loadJournalIfNeeded()
        memoryCache.put(key, data)
        val didWrite = writeDiskEntry(key, data)
        var didUpdateJournal = false
        if (didWrite) {
            //update journal
            didUpdateJournal = appendJournalForKey(key)
        }
        //trim entries to size
        trim()
        return didWrite && didUpdateJournal
    }

    override fun <T> put(key: String, data: T): Boolean {
        return put(key, CoreCache.Entry.noExpire(data))
    }

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
        loadJournalIfNeeded()
        memoryCache.remove(key)
        journal.removeAll(listOf(key))

        try {
            inWriteLock(key) {
                getEntryFile(key).delete()
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        writeJournal(journalFile, journal, false)
    }

    /**
     * Deletes the entire memoryCache and stats fresh
     */
    override fun clear() {
        journal.clear()
        memoryCache.clear()
        if (dir.exists()) {
            synchronized(masterLock) {
                deleteFileAndLog(journalFile)
                dir.listFiles()?.forEach { deleteFileAndLog(it) }
            }
        }
        locks.clear()
    }

    override fun getKeys(): List<String> {
        synchronized(masterLock) {
            val entries = dir.listFiles()
            val keys = ArrayList<String>()
            entries?.forEach {
                try {
                    val entryHeader = EntryHeader.read(it.source().buffer())
                    keys.add(entryHeader.key)
                } catch (e: Exception) {
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

    private fun currentStorageDiskUsage(): Long {
        synchronized(masterLock) {
            var currentSize: Long = 0
            //Measure current memoryCache size
            dir.listFiles()?.let { fileList ->
                for (i in fileList.indices) {
                    if (fileList[i].name != journalFile.name) {
                        currentSize += fileList[i].length()
                    }
                }
            }
            return currentSize
        }
    }

    /**
     * Trims the memoryCache until its less than 75% of the max size.
     */
    private fun trim() {

        fun sizeStatsLog(currentSize: Long, maxSizeBytes: Long): String {
            return String.format(
                    Locale.US,
                    "size [%.1fKB], max - [%.1fKB], load factor - [%.1fKB]",
                    currentSize.toDouble() / 1000,
                    maxSizeBytes.toDouble() / 1000,
                    (maxSizeBytes * LOAD_FACTOR).toDouble() / 1000
            )
        }

        //trim memoryCache to 75% of max size to prevent trashing the disk
        val maxLoadSize = (maxSizeBytes * LOAD_FACTOR).toLong()

        //measure memoryCache size
        var currentSize = currentStorageDiskUsage()
        if (currentSize <= maxSizeBytes) {
            Timber.d("Skipping unneeded trim %s ", sizeStatsLog(currentSize, maxSizeBytes.toLong()))
            return
        }

       Timber.d("Starting trim")
       Timber.d("Before trim %s ", sizeStatsLog(currentSize, maxSizeBytes.toLong()))

        //Remove expired entries
       Timber.d("----------------------------------------------------------------")
       Timber.d("Starting Expired trim")

        val expiredKeys = HashSet<String>()
        //synchronized on master lock, whole cache is affected
        synchronized(masterLock) {
            //trim expired expired entries
            val files = dir.listFiles() ?: emptyArray()
            for (i in files.indices) {
                val file = files[i]
                if (file.name != journalFile.name) {
                    try {
                        val entryHeader = EntryHeader.read(file.source().buffer())
                        if (entryHeader.hasExpired()) {
                            expiredKeys.add(entryHeader.key)
                            deleteFileAndLog(file)
                        }
                    } catch (e: Exception) {
                        //Error trimming expired file
                        deleteFileAndLog(file)
                    }
                }
            }
        }

        //if expired files were removed, remove the files from the journal
        //and remeasure the files to see if further trimming is needed
        if (expiredKeys.isNotEmpty()) {
            journal.removeAll(expiredKeys)
            writeJournal(journalFile, journal, false)
            currentSize = currentStorageDiskUsage()
        }

       Timber.d( "After expired trim  %s ", sizeStatsLog(currentSize, maxSizeBytes.toLong()))
       Timber.d( "----------------------------------------------------------------")
        if (currentSize <= maxLoadSize) {
           Timber.d( "Skipping LRU trim %s ", sizeStatsLog(currentSize, maxSizeBytes.toLong()))
           Timber.d( "----------------------------------------------------------------")
           Timber.d( "Finished trim")
            return
        }

        //Trim LRU files
       Timber.d( "Starting LRU trim")
        run {
            /*
             * Use a LinkedHashSet to preserve order and remove duplicates
             * Entries are appended to the bottom of the journal as entries are created
             * so these need to be removed from the top of the list to preserve LRU order
             */
            val journalSize = journal.size
            val orderedList = ArrayList<String>(journal.size)
            run {
                for (i in 0 until journalSize) {
                    val key = journal[journalSize - 1 - i]
                    if (!orderedList.contains(key)) {
                        orderedList.add(key)
                    }
                }
                orderedList.reverse()
            }

            //synchronized on master lock, whole memoryCache is affected
            synchronized(masterLock) {
                //Loop until the memoryCache is empty or current size is smaller than maxSize
                //Work from the top of the list as the new entries are removed downwards
                val removedKeys = LinkedHashSet<String>()
                for (key in orderedList) {
                    val file = getEntryFile(key)
                    val fileSize = file.length()
                   Timber.d(
                            "Trimming file for key - [%s], size - [%.1fKB]",
                            key,
                            fileSize.toDouble() / 1000
                    )
                    if (file.delete()) {
                        //each removed entry is added to the deleted list
                        removedKeys.add(key)
                        currentSize -= fileSize
                        if (currentSize <= maxLoadSize) {
                            //storage is small enough, dont ned to trim anymore
                            break
                        }

                    }
                }

                //Remove any entries from the memory memoryCache
                for (removedKey in removedKeys) {
                    memoryCache.remove(removedKey)
                }
                //Remove all deleted keys
                journal.removeAll(removedKeys)
            }
        }

       Timber.d( "After LRU trim %s ", sizeStatsLog(currentSize, maxSizeBytes.toLong()))
       Timber.d( "----------------------------------------------------------------")

        //journal has been trimmed, overwrite with a fresh journal
        writeJournal(journalFile, journal, false)
       Timber.d( "Finished trim")

    }

    private fun appendJournalForKey(key: String): Boolean {
        journal.remove(key)
        journal.add(key)
        return writeJournal(journalFile, listOf(key), true)
    }

    private fun loadJournalIfNeeded() {
        if (!loadedJournal) {
            journal.clear()
            journal = readJournal(journalFile).toMutableList()
            loadedJournal = true
        }
    }

    private fun <T> readEntry(key: String, type: Type): CoreCache.Entry<T>? {
        try {
            return inWriteLock(key) {
                getEntryFile(key).source().buffer().use {
                    val entryHeader = EntryHeader.read(it)
                    if (entryHeader.hasExpired()) {
                        //expired entry, remove from disk
                        remove(key)
                        return@inWriteLock null
                    }
                    CoreCache.Entry(
                        defaultSerializer.read(it, type),
                        entryHeader.created,
                        entryHeader.expires
                    )
                }
            }
        } catch (e: Exception) {
            //on error reading remove the entry
            Timber.w(e, "Error reading entry for key - %s, deleting entry...", key)
            remove(key)
            return null
        }
    }

    private fun <T> writeDiskEntry(key: String, entry: CoreCache.Entry<T>): Boolean {
        try {
            if (entry.hasExpired()) {
                Timber.d("Cant write empty entry!")
                return false
            }
            inWriteLock(key) {
                getEntryFile(key).sink().buffer().use {
                    val entryHeader = EntryHeader(VERSION, key, Date(System.currentTimeMillis()), entry.expires())
                    entryHeader.write(it) //Write entryHeader
                    defaultSerializer.write(entry.data() as Any, it) //write entry
                    it.flush()
                    memoryCache.put(key, entry)
                }
                null
            }
            return true
        } catch (e: Exception) {
            remove(key)
            //on error writing remove the gate
            Timber.w(e, "Error writing gate for key - %s", key)
            return false
        }
    }

    private fun writeJournal(journalFile: File, lines: Collection<String>, append: Boolean): Boolean {
        try {
            return inWriteLock(JOURNAL_FILE) {
                val sink: BufferedSink = if (append) {
                    journalFile.appendingSink().buffer()
                } else {
                    journalFile.sink().buffer()
                }
                sink.use {
                    for (entry in lines) {
                        sink.writeUtf8(entry)
                        sink.writeUtf8("\n")
                    }
                    sink.flush()
                }
                true
            }
        } catch (e: Exception) {
            Timber.w(e,"Error writing journal, clearing memoryCache")
            clear()
            return false
        }
    }

    private fun readJournal(journalFile: File): List<String> {
        try {
            return inReadLock<List<String>>(JOURNAL_FILE) {
                try {
                    if (journalFile.exists()) {
                        val journal = LinkedList<String>()
                        journalFile.source().buffer().use {
                            var line: String? = it.readUtf8Line()
                            while (line != null) {
                                journal.add(line)
                                line = it.readUtf8Line()
                            }
                        }
                        return@inReadLock journal
                    } else {
                        return@inReadLock LinkedList<String>()
                    }
                } catch (e: Exception) {
                    Timber.w(e,"Error reading journal, clearing cache")
                    clear()
                    return@inReadLock LinkedList<String>()
                }
            }
        } catch (e: Exception) {
            clear()
            return LinkedList()
        }
    }

    private fun rwLockForKey(key: String): ReadWriteLock {
        return locks.getOrPut(key, { ReentrantReadWriteLock() })
    }

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

    /**
     * Header for entries, this contains metadata such as the version of the memoryCache and the
     * date created.
     */
    class EntryHeader(val version: Int, val key: String, val created: Date, val expires: Date) {

        //Write the entryHeader to disk, do NOT close the stream
        @Throws(Exception::class)
        fun write(sink: BufferedSink) {
            sink.writeInt(version)

            //write key value
            val keyData = key.encodeUtf8()
            sink.writeInt(keyData.size)
            sink.write(keyData)

            sink.writeLong(created.time)
            sink.writeLong(expires.time)
            sink.flush()
        }

        fun hasExpired(): Boolean {
            return System.currentTimeMillis() > expires.time
        }

        companion object {

            fun read(source: BufferedSource): EntryHeader {
                //first 4 bytes are always the version (32bit int)
                //switch on the version to enable reading of older entries
                //more versions can be added of the header changes
                when (val version = source.readInt()) {
                    1 -> {
                        //read back key value
                        val keyStringLength = source.readInt()
                        val byteStringKey = source.readByteString(keyStringLength.toLong())
                        val key = byteStringKey.utf8()

                        val created = Date(source.readLong())
                        val expires = Date(source.readLong())
                        return EntryHeader(version, key, created, expires)
                    }
                    else -> throw IllegalArgumentException(
                            "Error reading file entryHeader, verson - [$version] not recognised")
                }
            }
        }
    }

}

private fun deleteFileAndLog(file: File?) {
    if (file != null && file.exists()) {
        file.delete()
        Timber.d("deleted file %s", file.name)
    }
}
