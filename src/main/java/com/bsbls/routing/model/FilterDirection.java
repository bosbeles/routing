package com.bsbls.routing.model;

import java.io.Serializable;

public class FilterDirection implements Serializable {

    public enum FilterDirectionType {TX, RX, ROUTING}

    private FilterDirectionType filterDirectionType;
    private int from;
    private int to;

    public FilterDirection(FilterDirectionType filterDirectionType, int from, int to) {
        this.filterDirectionType = filterDirectionType;
        this.from = from;
        this.to = to;
    }

    public FilterDirectionType getFilterDirectionType() {
        return filterDirectionType;
    }

    public void setFilterDirectionType(FilterDirectionType filterDirectionType) {
        this.filterDirectionType = filterDirectionType;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }
}
