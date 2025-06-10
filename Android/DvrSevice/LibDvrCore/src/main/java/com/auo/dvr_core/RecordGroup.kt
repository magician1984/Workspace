package com.auo.dvr_core

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class RecordGroup(val timestamp: Long, val files: List<RecordFile>, val type: RecordType, val uri : Uri) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.createTypedArrayList(RecordFile)!!,
        RecordType.fromCode(parcel.readInt()),
        parcel.readParcelable(Uri::class.java.classLoader, Uri::class.java) ?: Uri.EMPTY)

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(timestamp)
        dest.writeTypedList(files)
        dest.writeInt(type.code)
        dest.writeParcelable(uri, flags)
    }

    companion object CREATOR : Parcelable.Creator<RecordGroup> {
        override fun createFromParcel(parcel: Parcel): RecordGroup {
            return RecordGroup(parcel)
        }

        override fun newArray(size: Int): Array<RecordGroup?> {
            return arrayOfNulls(size)
        }
    }
}
