package com.ani.timegapanalyzer.dashboard

import android.os.Parcel
import android.os.Parcelable

data class MeetingModel(val startDate : String?, val endDate: String?, val startTime: String?
                        , val endTime: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MeetingModel> {
        override fun createFromParcel(parcel: Parcel): MeetingModel {
            return MeetingModel(parcel)
        }

        override fun newArray(size: Int): Array<MeetingModel?> {
            return arrayOfNulls(size)
        }
    }
}