package com.rustret.worldguard.dbmodels;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.rustret.worldguard.coordinates.Coord;

@DatabaseTable(tableName = "regions")
public class RegionModel {

    @DatabaseField(generatedId = true)
    private int regionId;

    @DatabaseField
    private String regionName;

    @DatabaseField
    private String ownerName;

    @DatabaseField
    private String ownerId;

    @DatabaseField
    private int x1;

    @DatabaseField
    private int y1;

    @DatabaseField
    private int z1;

    @DatabaseField
    private int x2;

    @DatabaseField
    private int y2;

    @DatabaseField
    private int z2;

    public RegionModel() { }

    public RegionModel(String regionName, String ownerName, String ownerId, Coord pos1, Coord pos2) {
        this.regionName = regionName;
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.x1 = pos1.x;
        this.y1 = pos1.y;
        this.z1 = pos1.z;
        this.x2 = pos2.x;
        this.y2 = pos2.y;
        this.z2 = pos2.z;
    }

    public String getOwnerId() {
        return ownerId;
    }
}
