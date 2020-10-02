package com.ani.timegapanalyzer.dashboard

import android.os.Parcel
import android.os.Parcelable

data class AdapterModel(val dateString: String?, val timeDifference: String?, val itemType: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dateString)
        parcel.writeString(timeDifference)
        parcel.writeInt(itemType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AdapterModel> {
        override fun createFromParcel(parcel: Parcel): AdapterModel {
            return AdapterModel(parcel)
        }

        override fun newArray(size: Int): Array<AdapterModel?> {
            return arrayOfNulls(size)
        }
    }
}