package ru.fleyer.cristalixcarousel.utils;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;

@UtilityClass
public class Utils {

    public ArmorStand spawnArmorStand(Location loc, String name, boolean visible) {
        val stand = loc.getWorld().spawn(loc, ArmorStand.class);

        stand.setVisible(visible);
        stand.setGravity(false);
        stand.setCustomName(name);

        return stand;
    }

    public Location degrees(Location loc, double radius, double dgrs) {

        val x = loc.getX() + radius * Math.cos(dgrs * 3.141592653589793D / 180.0D);
        val y = loc.getY();
        val z = loc.getZ() + radius * Math.sin(dgrs * 3.141592653589793D / 180.0D);

        return new Location(loc.getWorld(), x, y, z);
    }

    public void teleportEntity(LivingEntity entity, Location location) {

        ((CraftLivingEntity) entity).getHandle().setPositionRotation(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());

    }
}
