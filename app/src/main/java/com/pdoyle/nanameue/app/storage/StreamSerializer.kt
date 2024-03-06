package com.pdoyle.nanameue.app.storage

import okio.BufferedSink
import okio.BufferedSource
import java.lang.reflect.Type

/**
 * Used for Serialising data. Okio stream based.
 */
interface StreamSerializer {

    /**
     * Write the data to the BufferedSink. Call flush to push data down to the storage
     */
    @Throws(Exception::class)
    fun <T: Any> write(data: T, bufferedSink: BufferedSink)

    /**
     * Read the data from the BufferedSource
     */
    @Throws(Exception::class)
    fun <T> read(bufferedSource: BufferedSource, type: Type): T
}
