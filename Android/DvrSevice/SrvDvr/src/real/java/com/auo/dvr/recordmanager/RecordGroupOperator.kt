package com.auo.dvr.recordmanager

import android.net.Uri
import androidx.core.net.toFile
import com.auo.dvr_core.DvrException
import com.auo.dvr_core.RecordFile
import com.auo.dvr_core.RecordGroup
import java.io.File
import java.io.IOException
import java.nio.channels.NonReadableChannelException

object RecordGroupOperator {
    internal val RecordGroup.name: String
        get() = this.timestamp.toString()

    internal fun RecordGroup.moveToExternal(root: File): RecordGroup {
        val targetFolder = File(root, this.name)

        val newFiles : List<RecordFile> = buildList {
            files.forEach { recordFile ->
                val newFile = recordFile.moveTo(targetFolder)
                add(newFile)
            }
        }

        return RecordGroup(timestamp = this.timestamp, files = newFiles, type = this.type, uri = Uri.fromFile(targetFolder))
    }

    internal fun RecordGroup.moveTo(root: File, convertor: IConvertor): RecordGroup {
        val originalFolder = this.uri.toFile()
        val targetFolder = File(root, this.name)
        originalFolder.renameTo(targetFolder)
        return convertor.parse(targetFolder, this.type)
    }

    internal fun RecordGroup.delete(): RecordGroup {
        val folder = this.uri.toFile()
        folder.deleteRecursively()
        return this
    }

    private fun RecordFile.moveTo(targetFolder: File): RecordFile {
        val videoFile = uri.toFile()
        val thumbnailFile = thumbnail.toFile()

        val targetVideoFile = File(targetFolder, videoFile.name)
        val targetThumbnailFile = File(targetFolder, thumbnailFile.name)

        clone(listOf(videoFile, thumbnailFile), listOf(targetVideoFile, targetThumbnailFile))
        videoFile.delete()
        thumbnailFile.delete()

        return RecordFile(
            name = name,
            createTime = createTime,
            location = location,
            uri = Uri.fromFile(targetVideoFile),
            thumbnail = Uri.fromFile(targetThumbnailFile)
        )
    }

    private fun clone(source: List<File>, target: List<File>) {
        for (i in source.indices) {
            try {
                source[i].inputStream().channel.use { srcChannel ->
                    target[i].outputStream().channel.use { dstChannel ->
                        dstChannel.transferFrom(srcChannel, 0, srcChannel.size())
                    }
                }
            } catch (e: IOException) {
                throw DvrException("Clone file", e.message ?: "Unknown IO Error")
            } catch (e: NonReadableChannelException) {
                throw DvrException("Clone file", e.message ?: "Non-readable channel error")
            }
        }
    }
}

