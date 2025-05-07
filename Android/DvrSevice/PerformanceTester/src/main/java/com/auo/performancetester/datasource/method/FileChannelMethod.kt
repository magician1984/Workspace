package com.auo.performancetester.datasource.method

import android.util.Log
import com.auo.dvr_core.DvrException
import com.auo.performancetester.domain.datasource.IDataSource
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.NonReadableChannelException

class FileChannelMethod : IDataSource.ICloneMethod {
    override fun clone(source: File, target: File, preallocate: Boolean, forceWrite: Boolean) {
        try {
            Log.d("FileChannelMethod", "clone: $source -> $target")

            RandomAccessFile(target, "rw").use { raf ->
                // Pre-allocate
                if (preallocate) {
                    raf.setLength(source.length())
                    raf.seek(0)
                }

                source.inputStream().channel.use { srcChannel ->
                    raf.channel.use { dstChannel ->
                        dstChannel.transferFrom(srcChannel, 0, source.length())
                        // Sync
                        if (forceWrite) {
                            dstChannel.force(true)
                        }
                    }
                }
            }
        } catch (e: IOException) {
            throw DvrException(this.javaClass.name, e.message ?: "Unknown IO Error")
        } catch (e: NonReadableChannelException) {
            throw DvrException(this.javaClass.name, e.message ?: "Non-readable channel error")
        }
    }
}

