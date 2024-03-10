package top.bogey.touch_tool_pro.utils.super_user;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CmdResult implements Parcelable {
    public boolean result;
    public String info;

    public CmdResult() {}

    protected CmdResult(Parcel in) {
        result = in.readByte() != 0;
        info = in.readString();
    }

    public static final Creator<CmdResult> CREATOR = new Creator<>() {
        @Override
        public CmdResult createFromParcel(Parcel in) {
            return new CmdResult(in);
        }

        @Override
        public CmdResult[] newArray(int size) {
            return new CmdResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeByte((byte) (result ? 1 : 0));
        dest.writeString(info);
    }
}
