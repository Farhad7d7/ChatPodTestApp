package ir.fanap.chattestapp.bussines.model

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class LogClass(val uniqueId: String, val logName: String, var log: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    var shoinglog: String = ""

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uniqueId)
        parcel.writeString(logName)
        parcel.writeString(log)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getJSon(): String {
        return log.replace("\"type\"", "<font color='#03a9f4'>type</font>")
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