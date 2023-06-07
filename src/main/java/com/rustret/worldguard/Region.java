package com.rustret.worldguard;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "regions")
public class Region {

    @DatabaseField(generatedId = true)
    private int regionId;

    @DatabaseField
    private String regionName;

    @DatabaseField
    private String ownerName;

    public Region() { }
}
