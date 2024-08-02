package com.anjacarchistra.kvm.stratafest.dto;
import android.os.Parcel;
import android.os.Parcelable;

public class Participant implements Parcelable {
    @Override
    public String toString() {
        return "Participant{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    private String name;
    private String email;

    public Participant(String name, String email) {
        this.name = name;
        this.email = email;
    }

    protected Participant(Parcel in) {
        name = in.readString();
        email = in.readString();
    }

    public static final Creator<Participant> CREATOR = new Creator<Participant>() {
        @Override
        public Participant createFromParcel(Parcel in) {
            return new Participant(in);
        }

        @Override
        public Participant[] newArray(int size) {
            return new Participant[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
    }

}
