package com.auo.dvr_core

import android.os.Parcel
import android.os.Parcelable

data class DvrState(val isAvailable: Boolean, val errorType: ErrorType) : Parcelable {
    enum class ErrorType {
        None,
        FlashDriveNotAvailable,
        InternalError
    }

    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        ErrorType.entries[parcel.readInt()]
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeByte(if (isAvailable) 1 else 0)
        p0.writeInt(errorType.ordinal)
    }

    companion object CREATOR : Parcelable.Creator<DvrState> {
        override fun createFromParcel(parcel: Parcel): DvrState {
            return DvrState(parcel)
        }

        override fun newArray(size: Int): Array<DvrState?> {
            return arrayOfNulls(size)
        }
    }

}
