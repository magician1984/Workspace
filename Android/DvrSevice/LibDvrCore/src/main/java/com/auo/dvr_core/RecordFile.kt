package com.auo.dvr_core

import android.os.Parcel
import android.os.Parcelable

data class RecordFile(val name: String, val createTime: Long, val location: CamLocation,  val type: FileType) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readLong(),
        CamLocation.fromCode(parcel.readInt()),
        FileType.fromCode(parcel.readInt())
    )

    override fun describeContents(): Int  = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeLong(createTime)
        dest.writeInt(location.code)
        dest.writeInt(type.code)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecordFile

        if (name != other.name) return false
        if (createTime != other.createTime) return false
        if (location != other.location) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + createTime.hashCode()
        result = 31 * result + location.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<RecordFile> {
        override fun createFromParcel(parcel: Parcel): RecordFile {
            return RecordFile(parcel)
        }

        override fun newArray(size: Int): Array<RecordFile?> {
            return arrayOfNulls(size)
        }
    }
}
