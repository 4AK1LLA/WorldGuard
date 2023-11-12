package com.rustret.rg;

import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.Plugin;
import com.github.davidmoten.rtreemulti.Entry;
import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rustret.rg.objects.Region;
import com.rustret.rg.objects.Selection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Storage {
    private final Plugin plugin;
    private final String filename = "regions.json";
    public final Map<UUID, Selection> selections = new HashMap<>();
    public final Map<String, Region> regions = new HashMap<>();
    public final Map<UUID, List<Region>> players = new HashMap<>();
    public RTree<Region, Rectangle> rtree = RTree.create(3);

    public Storage(Plugin plugin) {
        this.plugin = plugin;
    }

    public void removeSelection(UUID id) {
        selections.remove(id);
    }

    public int getNumberOfRegions(UUID id) {
        return players.getOrDefault(id, Collections.emptyList()).size();
    }

    /**
     * Creates region if its coordinates do not intersect other regions
     *
     * @return <tt>true</tt> if region was created and added to regions,
     * players and rtree.
     * <p><tt>false</tt> if region was not created because of intersecting
     * with another region.
     */
    public boolean checkIntersectAndAddRegion(String name, UUID id, Selection s) {
        Rectangle rectangle = mapRectangle(s.pos1, s.pos2);
        int level = s.pos1.level.getId();

        Iterable<Entry<Region, Rectangle>>  results = rtree.search(rectangle);
        for (Entry<Region, Rectangle> entry : results) {
            if (entry.value().level == level) {
                return false;
            }
        }

        Region region = new Region(name, id, s.pos1, s.pos2, level);
        regions.put(name, region);
        players.computeIfAbsent(region.owner, k -> new ArrayList<>())
                .add(region);
        rtree = rtree.add(region, rectangle);
        return true;
    }

    public void removeRegion(Region region) {
        regions.remove(region.name);
        players.get(region.owner)
                .removeIf(rg -> rg == region);
        rtree = rtree.delete(region, mapRectangle(region.min, region.max));
    }

    private Rectangle mapRectangle(Vector3 min, Vector3 max) {
        return Rectangle.create(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public int load() {
        plugin.saveResource(filename);
        File file = new File(plugin.getDataFolder(), filename);

        String json = "";
        try (Scanner scanner = new Scanner(file)) {
            scanner.useDelimiter("\\A");
            json = scanner.hasNext() ? scanner.next() : "";
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        JsonArray array = new Gson()
                .fromJson(json, JsonArray.class);

        array.forEach(rgElement -> {
            JsonObject rgObj = rgElement.getAsJsonObject();

            String name = rgObj.get("name").getAsString();
            UUID owner = UUID.fromString(rgObj.get("owner").getAsString());
            boolean pvp = rgObj.get("pvp").getAsBoolean();
            boolean secret = rgObj.get("secret").getAsBoolean();
            boolean mobs = rgObj.get("mobs").getAsBoolean();
            int level = rgObj.get("level").getAsInt();

            List<UUID> members = new ArrayList<>();
            rgObj.get("members")
                    .getAsJsonArray()
                    .forEach(id -> members.add(UUID.fromString(id.getAsString())));

            Vector3 min = mapVector3(rgObj.getAsJsonObject("min"));
            Vector3 max = mapVector3(rgObj.getAsJsonObject("max"));

            Region region = new Region(name, owner, min, max, level, members, pvp, secret, mobs);
            regions.put(name, region);
            players.computeIfAbsent(owner, k -> new ArrayList<>())
                    .add(region);
            rtree = rtree.add(region, mapRectangle(min, max));
        });

        return regions.size();
    }

    private Vector3 mapVector3(JsonObject o) {
        int x = o.get("x").getAsInt();
        int y = o.get("y").getAsInt();
        int z = o.get("z").getAsInt();

        return new Vector3(x, y, z);
    }

    public void unload() {
        File file = new File(plugin.getDataFolder(), filename);

        JsonArray data = new JsonArray();
        for (Region region : regions.values()) {
            JsonObject rgObj = new JsonObject();
            rgObj.addProperty("name", region.name);
            rgObj.addProperty("owner", region.owner.toString());
            rgObj.addProperty("pvp", region.pvp);
            rgObj.addProperty("secret", region.secret);
            rgObj.addProperty("mobs", region.mobs);
            rgObj.addProperty("level", region.level);

            JsonArray memberArr = new JsonArray();
            region.members.forEach(id -> memberArr.add(id.toString()));
            rgObj.add("members", memberArr);
            rgObj.add("min", mapJson(region.min));
            rgObj.add("max", mapJson(region.max));

            data.add(rgObj);
        }

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(gson.toJson(data));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonObject mapJson(Vector3 pos) {
        JsonObject o = new JsonObject();
        o.addProperty("x", (int) pos.x);
        o.addProperty("y", (int) pos.y);
        o.addProperty("z", (int) pos.z);

        return o;
    }

    public Region intersectedRegion(Position pos) {
        for (Entry<Region, Rectangle> e : rtree.search(Point.create(pos.x, pos.y, pos.z))) {
            if (e.value().level == pos.level.getId()) return e.value();
        }
        return null;
    }
}
