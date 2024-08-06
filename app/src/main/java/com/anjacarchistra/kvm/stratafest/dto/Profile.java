package  com.anjacarchistra.kvm.stratafest.dto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Profile {
    public List<Integer> getLotid() {
        return lotid;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<Integer> getEventid() {
        return eventid;
    }

    public List<String> getTime() {
        return time;
    }

    public List<String> getVenue() {
        return venue;
    }

    public String getCollegename() {
        return collegename;
    }

    public int getCollegeid() {
        return collegeid;
    }

    public int getDeptid() {
        return deptid;
    }

    public String getDeptname() {
        return deptname;
    }

    public String getEventvenue() {
        return eventvenue;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "lotid='" + lotid + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", eventid=" + eventid +
                ", time=" + time +
                ", venue=" + venue +
                ", collegename='" + collegename + '\'' +
                ", collegeid=" + collegeid +
                ", deptid=" + deptid +
                ", deptname='" + deptname + '\'' +
                ", eventvenue='" + eventvenue + '\'' +
                '}';
    }

    private List<Integer> lotid;
    private String name;
    private String password;
    private List<Integer> eventid;
    private List<String> time;
    private List<String> venue;
    private String collegename;
    private int collegeid; // This seems to be unused based on the JSON
    private int deptid;    // This seems to be unused based on the JSON
    private String deptname;
    private String eventvenue;

    public Profile(String lotid, String name, String password, String eventid, String time, String venue, String collegename, String deptname) {
        this.lotid = Arrays.stream(lotid.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        this.name = name;
        this.password = password;
        this.collegename = collegename;
        this.deptname = deptname;

        // Convert comma-separated strings to lists
        this.eventid = Arrays.stream(eventid.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        this.time = Arrays.stream(time.split(","))
                .collect(Collectors.toList());
        this.venue = Arrays.stream(venue.split(","))
                .collect(Collectors.toList());
    }

    // Getters and Setters
}
