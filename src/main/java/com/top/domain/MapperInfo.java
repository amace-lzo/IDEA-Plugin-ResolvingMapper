package com.top.domain;

import java.util.HashSet;

public class MapperInfo {
    // mapper name
    private String mapperName;
    private HashSet<String> tableSet;
    private HashSet<String> procSet;

    public String getMapperName() {
        return mapperName;
    }

    public void setMapperName(String mapperName) {
        this.mapperName = mapperName;
    }

    public HashSet<String> getTableSet() {
        return tableSet;
    }

    public void setTableSet(HashSet<String> tableSet) {
        this.tableSet = tableSet;
    }

    public HashSet<String> getProcSet() {
        return procSet;
    }

    public void setProcSet(HashSet<String> procSet) {
        this.procSet = procSet;
    }

    public int getMaxLength(){
        return Math.max(Math.max(this.procSet.size(), this.tableSet.size()), 1);
    }
}
