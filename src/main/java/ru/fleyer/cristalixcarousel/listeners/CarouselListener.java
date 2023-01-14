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

public class CarouselListener implements Listener {

    @EventHandler
    public void onVehicleEnter(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Horse) {

            if (Carousel.getHorseSeats().containsKey(e.getRightClicked())) {

                val seat = Carousel.getHorseSeats().get(e.getRightClicked());

                if (Carousel.getSeats().get(seat).isLocked()) {
                    e.setCancelled(true);
                    return;
                }

                e.setCancelled(true);
                if (!e.getPlayer().isSneaking()) {
                    CarouselDatabase.INSTANCE.startRidingSession(e.getPlayer(), (Horse) e.getRightClicked());
                    seat.addPassenger(e.getPlayer());
                }


            }
        }
    }

    @EventHandler
    public void onHorseUnleash(EntityDismountEvent event) {
        if (event.getDismounted() instanceof ArmorStand || event.getDismounted() instanceof Horse) {
            CarouselDatabase.INSTANCE.endRidingSession((Player) event.getEntity());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked() instanceof ArmorStand) {

            if (!Carousel.getSeats().containsKey(e.getRightClicked())) {
                return;
            }

            Carousel ride = Carousel.getSeats().get(e.getRightClicked());

            if (ride.isLocked()) {
                e.setCancelled(true);
                return;
            }

            if (e.getRightClicked().getPassengers().isEmpty()) {
                e.getRightClicked().addPassenger(e.getPlayer());
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent e) {
        for (val entity : Carousel.getSeats().keySet()) {
            if (entity.getLocation().getChunk().equals(e.getChunk())) {
                e.setCancelled(true);
                return;
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldUnload(WorldUnloadEvent e) {
        for (val entity : Carousel.getSeats().keySet()) {
            if (entity.getLocation().getWorld().equals(e.getWorld())) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().isInsideVehicle()) {
            e.getPlayer().getVehicle().removePassenger(e.getPlayer());
        }
    }

    @EventHandler
    public void onJoin(PlayerQuitEvent e) {
        if (e.getPlayer().isInsideVehicle()) {
            e.getPlayer().getVehicle().removePassenger(e.getPlayer());
        }
    }

}
