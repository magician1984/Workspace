package com.auo.dvr_core

import android.os.Parcel
import android.os.Parcelable

data class DvrConfigure(val duration : RecordDuration, val resolution: RecordResolution) : Parcelable{
    constructor(parcel: Parcel) : this(RecordDuration.entries[parcel.readInt()], RecordResolution.entries[parcel.readInt()])

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(duration.ordinal)
        dest.writeInt(resolution.ordinal)
    }

    companion object CREATOR : Parcelable.Creator<DvrConfigure> {
        override fun createFromParcel(parcel: Parcel): DvrConfigure {
            return DvrConfigure(parcel)
        }

        override fun newArray(size: Int): Array<DvrConfigure?> {
            return arrayOfNulls(size)
        }
    }
}
