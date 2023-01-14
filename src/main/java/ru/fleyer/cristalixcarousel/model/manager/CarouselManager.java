package ru.fleyer.cristalixcarousel.model.manager;

import gnu.trove.map.hash.THashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import ru.fleyer.cristalixcarousel.CristalixCarousel;
import ru.fleyer.cristalixcarousel.model.Carousel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CarouselManager {
    public static CarouselManager INSTANCE = new CarouselManager();

    Map<String, Carousel> carousel = new THashMap<>();
    Map<Entity, Carousel> seats = new THashMap<>();
    Map<Entity, Entity> horseSeats = new THashMap<>();

    public Carousel importRide(String name) {
        val configFile = new File(CristalixCarousel.getInstance().getDataFolder().getAbsoluteFile() + "/rides.yml");
        val config = YamlConfiguration.loadConfiguration(configFile);

        if (!config.contains("Rides." + name + ".World")) {
            return null;
        }

        val loc = new Location(Bukkit.getServer().getWorld(config.getString("Rides." + name + ".World")), config.getDouble("Rides." + name + ".x"), config.getDouble("Rides." + name + ".y"), config.getDouble("Rides." + name + ".z"));
        val radius = Float.parseFloat(config.getString("Rides." + name + ".Radius"));
        val carCount = config.getInt("Rides." + name + ".CarCount");
        val clockwise = config.getBoolean("Rides." + name + ".Clockwise");

        return new Carousel(name, loc, radius, carCount, clockwise);
    }

    public void exportRide(Carousel carousel) throws IOException {
        val configFile = new File(CristalixCarousel.getInstance().getDataFolder().getAbsoluteFile() + "/rides.yml");
        val config = YamlConfiguration.loadConfiguration(configFile);
        val name = carousel.getName();

        config.set("Rides." + name + ".World", carousel.getCenter().getWorld().getName());
        config.set("Rides." + name + ".x", carousel.getCenter().getX());
        config.set("Rides." + name + ".y", carousel.getCenter().getY());
        config.set("Rides." + name + ".z", carousel.getCenter().getZ());
        config.set("Rides." + name + ".Radius", carousel.getRadius());
        config.set("Rides." + name + ".CarCount", carousel.getCarCount());
        config.set("Rides." + name + ".Clockwise", carousel.isClockwise());

        config.save(configFile);
    }

    public void removeRide(String name) throws IOException {
        val configFile = new File(CristalixCarousel.getInstance().getDataFolder().getAbsoluteFile() + "/rides.yml");
        val config = YamlConfiguration.loadConfiguration(configFile);

        config.set("Rides." + name + ".World", null);
        config.set("Rides." + name + ".x", null);
        config.set("Rides." + name + ".y", null);
        config.set("Rides." + name + ".z", null);
        config.set("Rides." + name + ".Radius", null);
        config.set("Rides." + name + ".CarCount", null);
        config.set("Rides." + name + ".Clockwise", null);

        config.save(configFile);
    }

    public FileConfiguration config() {
        val configFile = new File(CristalixCarousel.getInstance().getDataFolder().getAbsoluteFile() + "/config.yml");
        return YamlConfiguration.loadConfiguration(configFile);
    }

    public void exportConfig() throws IOException {
        val stream = CristalixCarousel.class.getResourceAsStream("/config.yml");
        val configFile = new File(CristalixCarousel.getInstance().getDataFolder().getAbsoluteFile() + "/config.yml");

        if (configFile.exists()) {
            return;
        }
        Files.copy(Objects.requireNonNull(stream), configFile.toPath());

        Objects.requireNonNull(stream).close();
    }
}
