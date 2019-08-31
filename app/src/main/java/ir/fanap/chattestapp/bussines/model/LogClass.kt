package ir.fanap.chattestapp.bussines.model

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class LogClass(val uniqueId:String,val logName: String, val log: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uniqueId)
        parcel.writeString(logName)
        parcel.writeString(log)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LogClass> {
        override fun createFromParcel(parcel: Parcel): LogClass {
            return LogClass(parcel)
        }

        override fun newArray(size: Int): Array<LogClass?> {
            return arrayOfNulls(size)
        }
    }
}