package com.rustret.worldguard.dbmodels;

import cn.nukkit.math.Vector3;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "regions")
public class RegionModel {

    @DatabaseField(generatedId = true)
    private int regionId;

    @DatabaseField(unique = true)
    private String regionName;

    @DatabaseField
    private String ownerName, ownerId;

    @DatabaseField
    private double x1, y1, z1, x2, y2, z2;

    @DatabaseField
    private boolean pvp;

    public RegionModel() { }

    public RegionModel(String regionName, String ownerName, String ownerId, Vector3[] coordinates, boolean pvp) {
        this.regionName = regionName;
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.x1 = coordinates[0].x;
        this.y1 = coordinates[0].y;
        this.z1 = coordinates[0].z;
        this.x2 = coordinates[1].x;
        this.y2 = coordinates[1].y;
        this.z2 = coordinates[1].z;
        this.pvp = pvp;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Vector3[] getCoordinates() {
        return new Vector3[] { new Vector3(x1, y1, z1), new Vector3(x2, y2, z2) };
    }

    public boolean getPvp() {
        return pvp;
    }
}
