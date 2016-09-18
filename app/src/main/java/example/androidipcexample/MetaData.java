package example.androidipcexample;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class MetaData implements Parcelable, Serializable {

    public final int width;

    public final int height;

    public MetaData(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    protected MetaData(Parcel in) {
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Parcelable.Creator<MetaData> CREATOR = new Parcelable.Creator<MetaData>() {
        @Override
        public MetaData createFromParcel(Parcel source) {
            return new MetaData(source);
        }

        @Override
        public MetaData[] newArray(int size) {
            return new MetaData[size];
        }
    };
}
