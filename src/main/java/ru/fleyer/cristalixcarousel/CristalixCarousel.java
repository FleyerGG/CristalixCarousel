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
    CarouselManager manager = CarouselManager.INSTANCE;
    CarouselDatabase database = CarouselDatabase.INSTANCE;
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
            manager.exportConfig();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        database.connect();
        getCommand("carousel").setExecutor(new CarouselCommand(manager));
        getServer().getPluginManager().registerEvents(new CarouselListener(manager,database), this);

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new CarouselPacketAdapter(this, new PacketType[]{Client.STEER_VEHICLE},manager));

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            manager.getCarousel().values().forEach(Carousel::update);
        }, 1L, 1L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        manager.getCarousel().values().forEach(Carousel::despawn);
    }
}
