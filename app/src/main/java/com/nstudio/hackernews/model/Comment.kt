@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.nstudio.hackernews.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*

data class Comment(
    @SerializedName("by") val by: String,
    @SerializedName("id") val id: Int,
    @SerializedName("kids") val kids: List<Int>,
    @SerializedName("parent") val parent: Int,
    @SerializedName("text") val text: String,
    @SerializedName("time") val time: Long,
    @SerializedName("type") val type: String,
    @SerializedName("deleted") val deleted: Boolean,
    @SerializedName("dead") val dead: Boolean
    ) : Parcelable{

    constructor(parcel: Parcel) : this(
        by = parcel.readString(),
        id = parcel.readInt(),
        kids = intArrayOf().toCollection(ArrayList()),
        parent = parcel.readInt(),
        text = parcel.readString(),
        time = parcel.readLong(),
        type = parcel.readString(),
        deleted = parcel.readByte()==1.toByte(),
        dead = parcel.readByte()==1.toByte()
    )


    override fun writeToParcel(dest: Parcel?, flags: Int) {

        if (dest != null) {
            dest.writeString(by)
            dest.writeInt(id)
            dest.writeIntArray(kids.toIntArray())
            dest.writeInt(parent)
            dest.writeString(text)
            dest.writeLong(time)
            dest.writeString(type)
            dest.writeByte((if (deleted) 1 else 0).toByte())
            dest.writeByte((if (dead) 1 else 0).toByte())
        }

    }

    override fun describeContents(): Int {
       return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }

}
