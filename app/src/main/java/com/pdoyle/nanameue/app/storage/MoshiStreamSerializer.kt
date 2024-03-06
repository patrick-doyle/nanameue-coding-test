package com.pdoyle.nanameue.app.storage

import com.squareup.moshi.Moshi
import okio.BufferedSink
import okio.BufferedSource
import java.lang.reflect.Type

class MoshiStreamSerializer(private val moshi: Moshi) : StreamSerializer {

    @Throws(Exception::class)
    override fun <T: Any> write(data: T, bufferedSink: BufferedSink) {
        moshi.adapter<T>(data::class.java).toJson(bufferedSink, data)
    }

    @Throws(Exception::class)
    override fun <T> read(bufferedSource: BufferedSource, type: Type): T {
        @Suppress("UNCHECKED_CAST")
        return moshi.adapter<T>(type).fromJson(bufferedSource) as T
    }

}