package ru.fleyer.cristalixcarousel;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fleyer.cristalixcarousel.commands.CarouselCommand;
import ru.fleyer.cristalixcarousel.database.CarouselDatabase;
import ru.fleyer.cristalixcarousel.listeners.CarouselListener;
import ru.fleyer.cristalixcarousel.model.Carousel;
import ru.fleyer.cristalixcarousel.model.manager.CarouselManager;
import ru.fleyer.cristalixcarousel.protocol.CarouselPacketAdapter;

import java.io.File;
import java.io.IOException;

public class CristalixCarousel extends JavaPlugin {
    @Getter
    public static CristalixCarousel instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        File pluginFolder = new File(getInstance().getDataFolder().getAbsolutePath());
        pluginFolder.mkdirs();
        File ridesFile = new File(pluginFolder.getAbsolutePath() + "\\rides.yml");

        try {
            ridesFile.createNewFile();
            CarouselManager.INSTANCE.exportConfig();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        CarouselDatabase.INSTANCE.connect();
        getCommand("carousel").setExecutor(new CarouselCommand());
        getServer().getPluginManager().registerEvents(new CarouselListener(), this);

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new CarouselPacketAdapter(this, new PacketType[]{Client.STEER_VEHICLE}));

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            Carousel.getCarousel().values().forEach(Carousel::update);
        }, 1L, 1L);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Carousel.getCarousel().values().forEach(Carousel::despawn);
    }
}
