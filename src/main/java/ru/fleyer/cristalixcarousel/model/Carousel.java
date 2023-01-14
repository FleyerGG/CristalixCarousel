package ru.fleyer.cristalixcarousel.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Horse;
import ru.fleyer.cristalixcarousel.model.manager.CarouselManager;
import ru.fleyer.cristalixcarousel.utils.Utils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Carousel {
    final CarouselManager manager = CarouselManager.INSTANCE;
    final List<ArmorStand> armorStands = new ArrayList<>();
    final List<Horse> horses = new ArrayList<>();
    final String name;
    final Location center;
    final float radius;
    final int carCount;
    final boolean clockwise;
    boolean spawned = false, locked = false;
    float speed = 0.0F, degrees = 0.0F;

    public Carousel(String name, Location center, float radius, int carCount, boolean clockwise) {
        this.name = name;
        this.center = center;
        this.radius = radius;
        this.carCount = carCount;
        this.clockwise = clockwise;
        manager.getCarousel().put(name, this);
    }

    public void spawn() {
        spawned = true;
        for (int i = 0; i < this.carCount; ++i) {
            val degrees = (float) (360 / this.carCount * i);
            val location = Utils.degrees(this.center, this.radius, degrees);
            var height = Math.cos(Math.toRadians(degrees * 4.0F)) * ((double) CarouselManager.INSTANCE.config().getInt("max-height") / 2.0F);
            location.setYaw(degrees);

            if (!this.clockwise) {
                location.setYaw(degrees + 180.0F);
            }

            if (this.carCount % 2 != 0 && i % 2 == 0) {
                height = 0.0D - height;
            }

            val standLoc = location.clone();

            location.add(0.0D, height, 0.0D);
            standLoc.subtract(0.0D, 0.25D, 0.0D);

            val car = Utils.spawnArmorStand(standLoc, "Cristalix_" + this.getName(), false);
            val horse = location.getWorld().spawn(location, Horse.class);

            horse.setGravity(false);
            horse.setAdult();
            horse.setInvulnerable(true);
            horse.setSilent(true);
            horse.setAI(false);

            manager.getSeats().put(car, this);
            manager.getHorseSeats().put(horse, car);

            armorStands.add(car);
            horses.add(horse);
        }
    }

    public void despawn() {
        spawned = false;
        speed = 0.0F;

        armorStands.forEach(armorStand -> {
            armorStand.remove();
            manager.getSeats().remove(armorStand);
        });

        horses.forEach(horse -> {
            horse.remove();
            manager.getHorseSeats().remove(horse);
        });

        horses.clear();
        armorStands.clear();
    }

    public void update() {
        if (this.speed == 0.0F) {
            return;
        }

        for (var i = 0; i < this.carCount; ++i) {
            val startAngle = (float) (360 / this.carCount * i);
            var finalAngle = this.degrees + startAngle;
            var height = Math.cos(Math.toRadians(finalAngle * 4.0F)) * (double) (CarouselManager.INSTANCE.config().getInt("max-height") / 2.0F);

            if (this.carCount % 2 != 0 && i % 2 == 0) {
                height = 0.0D - height;
            }

            val center = this.center.clone().add(0.0D, height, 0.0D);
            val loc = Utils.degrees(center, this.radius, finalAngle);

            if (!this.clockwise) {
                finalAngle += 180.0F;
            }

            loc.setYaw(finalAngle);

            val car = this.armorStands.get(i);
            val standLoc = loc.clone();
            val horse = this.horses.get(i);

            standLoc.subtract(0.0D, 0.25D, 0.0D);
            Utils.teleportEntity(car, standLoc);

            horse.teleport(loc);
        }

        this.degrees += this.clockwise ? this.speed : -this.speed;
    }
}
