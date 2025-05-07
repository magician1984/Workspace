package com.auo.performancetester.datasource.method

import android.util.Log
import com.auo.performancetester.domain.datasource.IDataSource
import java.io.File
import java.io.RandomAccessFile

class BufferCopyMethod(private val bufferSize: Int = DEFAULT_BUFFER_SIZE) : IDataSource.ICloneMethod {

    override fun clone(source: File, target: File, preallocate: Boolean, forceWrite: Boolean) {
        Log.d("BufferCopyMethod", "clone: $source -> $target")
        RandomAccessFile(target, "rw").use { raf->
            // Pre-allocate
            if (preallocate) {
                raf.setLength(source.length())
                raf.seek(0)
            }

            source.inputStream().use { inputStream ->
                val buffer = ByteArray(bufferSize)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    raf.write(buffer, 0, bytesRead)
                }
                // Sync
                if(forceWrite)
                    raf.fd.sync()
            }
        }
    }
}
