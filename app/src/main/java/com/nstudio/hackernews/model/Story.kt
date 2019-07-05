package com.nstudio.hackernews.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
data class Story(
    @SerializedName("by") val by: String,
    @SerializedName("descendants") val descendants: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("kids") val kids: List<Int>,
    @SerializedName("score") val score: Int,
    @SerializedName("time") val time: Long,
    @SerializedName("title") val title: String,
    @SerializedName("type") val type: String,
    @SerializedName("url") val url: String,
    @SerializedName("deleted") val deleted: Boolean,
    @SerializedName("dead") val dead: Boolean
): Parcelable{


    constructor(parcel: Parcel) : this(
        by = parcel.readString(),
        descendants = parcel.readInt(),
        id = parcel.readInt(),
        kids = intArrayOf().toCollection(ArrayList()),
        score = parcel.readInt(),
        time = parcel.readLong(),
        title = parcel.readString(),
        type = parcel.readString(),
        url = parcel.readString(),
        deleted = parcel.readByte()==1.toByte(),
        dead = parcel.readByte()==1.toByte()
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {

        if (dest != null) {
            dest.writeString(by)
            dest.writeInt(descendants)
            dest.writeInt(id)
            dest.writeIntArray(kids.toIntArray())
            dest.writeInt(score)
            dest.writeLong(time)
            dest.writeString(title)
            dest.writeString(type)
            dest.writeString(url)
            dest.writeByte((if (deleted) 1 else 0).toByte())
            dest.writeByte((if (dead) 1 else 0).toByte())
        }

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Story> {
        override fun createFromParcel(parcel: Parcel): Story {
            return Story(parcel)
        }

        override fun newArray(size: Int): Array<Story?> {
            return arrayOfNulls(size)
        }
    }


}
