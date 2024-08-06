package com.anjacarchistra.kvm.stratafest.dto;
import java.util.LinkedHashSet;
import java.util.Set;

public class Result{
    public Result(Integer eventid, String eventname, Boolean prelims, LinkedHashSet<String> selectedSet, Boolean finals, LinkedHashSet<String> winners) {
        this.eventid = eventid;
        this.eventname = eventname;
        this.prelims = prelims;
        this.selectedSet = selectedSet;

        this.finals = finals;
        this.winners= winners;
    }

    public Integer getEventid() {
        return eventid;
    }

    public void setEventid(Integer eventid) {
        this.eventid = eventid;
    }

    public String getEventname() {
        return eventname;
    }

    public void setEventname(String eventname) {
        this.eventname = eventname;
    }

    public Boolean getPrelims() {
        return prelims;
    }

    public void setPrelims(Boolean prelims) {
        this.prelims = prelims;
    }

    public Boolean getFinalls() {
        return finals;
    }

    public void setFinalls(Boolean finalls) {
        this.finals = finalls;
    }

    private  Integer eventid;
    private  String eventname;
    private  Boolean prelims;

    private Boolean finals;

    public Boolean getFinals() {
        return finals;
    }

    public void setFinals(Boolean finals) {
        this.finals = finals;
    }

    public Set<String> getSelectedSet() {
        return selectedSet;
    }

    public void setSelectedSet(LinkedHashSet<String> selectedSet) {
        this.selectedSet = selectedSet;
    }

    public Set<String> getWinners() {
        return winners;
    }

    public void setWinners(LinkedHashSet<String> winners) {
        this.winners = winners;
    }

    private  LinkedHashSet<String> selectedSet;
    private  LinkedHashSet<String> winners;

    @Override
    public String toString() {
        return "Result{" +
                "eventid=" + eventid +
                ", eventname='" + eventname + '\'' +
                ", prelims=" + prelims +
                ", finals=" + finals +
                ", selectedSet=" + selectedSet +
                ", winners=" + winners +
                '}';
    }
}