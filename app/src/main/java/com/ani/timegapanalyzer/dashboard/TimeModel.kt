package com.ani.timegapanalyzer.dashboard

import android.os.Parcel
import android.os.Parcelable

data class TimeModel(val startTime : String?, val endTime :String?, val startDate: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeString(startDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TimeModel> {
        override fun createFromParcel(parcel: Parcel): TimeModel {
            return TimeModel(parcel)
        }

        override fun newArray(size: Int): Array<TimeModel?> {
            return arrayOfNulls(size)
        }
    }
}