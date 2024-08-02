package com.anjacarchistra.kvm.stratafest.util;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;
import com.anjacarchistra.kvm.stratafest.dto.Participant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventParticipantsWrapper implements Parcelable {
    private HashMap<String, List<Participant>> eventParticipantsMap;

    public EventParticipantsWrapper(HashMap<String, List<Participant>> map) {
        this.eventParticipantsMap = map;
    }

    protected EventParticipantsWrapper(Parcel in) {
        // Read the data from Parcel
        ArrayList<Pair<String, ArrayList<Participant>>> list = new ArrayList<>();
        in.readList(list, Pair.class.getClassLoader());
        eventParticipantsMap = new HashMap<>();
        for (Pair<String, ArrayList<Participant>> pair : list) {
            eventParticipantsMap.put(pair.first, pair.second);
        }
    }

    public static final Creator<EventParticipantsWrapper> CREATOR = new Creator<EventParticipantsWrapper>() {
        @Override
        public EventParticipantsWrapper createFromParcel(Parcel in) {
            return new EventParticipantsWrapper(in);
        }

        @Override
        public EventParticipantsWrapper[] newArray(int size) {
            return new EventParticipantsWrapper[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write the data to Parcel
        ArrayList<Pair<String, ArrayList<Participant>>> list = new ArrayList<>();
        for (HashMap.Entry<String, List<Participant>> entry : eventParticipantsMap.entrySet()) {
            list.add(new Pair<>(entry.getKey(), new ArrayList<>(entry.getValue())));
        }
        dest.writeList(list);
    }

    public HashMap<String, List<Participant>> getEventParticipantsMap() {
        return eventParticipantsMap;
    }
}

