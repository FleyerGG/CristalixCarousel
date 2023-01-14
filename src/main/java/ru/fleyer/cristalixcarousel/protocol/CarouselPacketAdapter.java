package ru.fleyer.cristalixcarousel.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;
import ru.fleyer.cristalixcarousel.model.manager.CarouselManager;

public class CarouselPacketAdapter extends PacketAdapter {
    private CarouselManager manager;

    public CarouselPacketAdapter(Plugin plugin, PacketType[] types, CarouselManager carouselManager) {
        super(plugin, types);
        manager = carouselManager;
    }

    public void onPacketSending(PacketEvent e) {
    }

    public void onPacketReceiving(PacketEvent e) {
        if (e.getPacketType() == PacketType.Play.Client.STEER_VEHICLE
                && e.getPlayer().isInsideVehicle()
                && manager.getSeats().containsKey(e.getPlayer().getVehicle())
                && manager.getSeats().get(e.getPlayer().getVehicle()).isLocked()
                && e.getPacket().getBooleans().read(1)) {
            e.setCancelled(true);
        }
    }
}
