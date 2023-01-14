package ru.fleyer.cristalixcarousel.listeners;

import lombok.val;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import ru.fleyer.cristalixcarousel.database.CarouselDatabase;
import ru.fleyer.cristalixcarousel.model.Carousel;
import ru.fleyer.cristalixcarousel.model.manager.CarouselManager;

public class CarouselListener implements Listener {
    CarouselManager manager = CarouselManager.INSTANCE;

    @EventHandler
    public void onVehicleEnter(PlayerInteractEntityEvent event) {
        val horse = event.getRightClicked();
        val player = event.getPlayer();

        if (horse instanceof Horse) {
            if (manager.getHorseSeats().containsKey(horse)) {

                val seat = manager.getHorseSeats().get(horse);

                if (manager.getSeats().get(seat).isLocked()) {
                    event.setCancelled(true);
                    return;
                }

                event.setCancelled(true);
                if (!player.isSneaking()) {
                    CarouselDatabase.INSTANCE.startRidingSession(player, (Horse) horse);
                    seat.addPassenger(player);
                }
            }
        }
    }

    @EventHandler
    public void onHorseUnleash(EntityDismountEvent event) {
        val entity = event.getDismounted();

        if (entity instanceof ArmorStand || entity instanceof Horse) {
            CarouselDatabase.INSTANCE.endRidingSession((Player) event.getEntity());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        val armorStand = e.getRightClicked();

        if (armorStand instanceof ArmorStand) {

            if (!manager.getSeats().containsKey(armorStand)) {
                return;
            }

            Carousel ride = manager.getSeats().get(armorStand);

            if (ride.isLocked()) {
                e.setCancelled(true);
                return;
            }

            if (armorStand.getPassengers().isEmpty()) {
                armorStand.addPassenger(e.getPlayer());
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (val entity : manager.getSeats().keySet()) {
            if (entity.getLocation().getChunk().equals(event.getChunk())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldUnload(WorldUnloadEvent event) {
        for (val entity : manager.getSeats().keySet()) {
            if (entity.getLocation().getWorld().equals(event.getWorld())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        val player = event.getPlayer();
        if (player.isInsideVehicle()) {
            player.getVehicle().removePassenger(player.getPlayer());
        }
    }

    @EventHandler
    public void onJoin(PlayerQuitEvent event) {
        val player = event.getPlayer();
        if (player.isInsideVehicle()) {
            player.getVehicle().removePassenger(player);
        }
    }

}
