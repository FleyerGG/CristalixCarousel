package ru.fleyer.cristalixcarousel.commands;

import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.fleyer.cristalixcarousel.model.Carousel;
import ru.fleyer.cristalixcarousel.model.manager.CarouselManager;

import java.io.IOException;

public class CarouselCommand implements CommandExecutor {
    CarouselManager manager = CarouselManager.INSTANCE;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cТолько игрок может использовать эту команду!");
            return false;
        }

        val player = (Player) sender;

        if (!player.hasPermission("cristalix.carousel")) {
            player.sendMessage("§cУ вас нет прав для использования этой команды!");
            return false;
        }

        Carousel carousel;

        if (args.length < 1) {
            help(player);
            return false;
        }

        switch (args[0]) {
            case "info": {
                help(player);
                return true;
            }

            case "create": {
                if (args.length < 5) {
                    player.sendMessage("§7Используйте: §a/carousel create " +
                            "<название> " +
                            "<радиус>" +
                            " <кол-во лошадей> " +
                            "<кручение по часовой стрелке (true/false)>");
                    return false;
                }

                carousel = new Carousel(args[1],
                        player.getLocation(),
                        Float.parseFloat(args[2]),
                        Integer.parseInt(args[3]),
                        Boolean.parseBoolean(args[4]));

                try {
                    manager.exportRide(carousel);
                } catch (IOException exception) {
                    player.sendMessage("§cОшибка: что-то пошло не так при создании карусели. Ошибка входа в консоль!");
                    exception.printStackTrace();
                    return true;
                }

                player.sendMessage("§aКарусель " + args[1] + " успешно создана!");
                return true;
            }
            case "speed": {
                if (args.length < 3) {
                    player.sendMessage("§7Используйте: §a/carousel speed <скорость>");
                    return false;
                }

                if (isCarouselSpawned(args[1], player)) {
                    return true;
                }

                val speed = Float.parseFloat(args[2]);
                if (speed < 0.0F || speed > 50.0F) {
                    player.sendMessage("§cСкорость должна быть от 0 до 50!");
                    return true;
                }

                (manager.getCarousel().get(args[1])).setSpeed(speed);
                player.sendMessage("§aСкорость карусели " + args[1] + " установлена на " + speed + "!");
                return true;
            }
            case "spawn": {
                if (args.length < 2) {
                    player.sendMessage("§7Используйте: §a/carousel spawn <название>");
                    return false;
                }

                if (manager.getCarousel().containsKey(args[1]) && (manager.getCarousel().get(args[1])).isSpawned()) {
                    player.sendMessage("§cКарусель " + args[1] + " уже заспавнена!");
                    return true;
                }

                carousel = manager.importRide(args[1]);
                if (carousel == null) {
                    player.sendMessage("§cОшибка: Не удалось загрузить аттракцион, " +
                            "возможно, этого аттракциона не существует?");
                    return true;
                }

                carousel.spawn();
                player.sendMessage("§aКарусель " + args[1] + " успешно заспавнена!");
                return true;
            }
            case "despawn": {
                if (args.length < 2) {
                    player.sendMessage("§7Используйте: §a/carousel despawn <название>");
                    return false;
                }

                if (this.isCarouselSpawned(args[1], player)) {
                    return true;
                }

                (manager.getCarousel().get(args[1])).despawn();
                player.sendMessage("§aКарусель " + args[1] + " успешно деспавнена!");
                return true;
            }
            case "remove": {
                if (args.length < 2) {
                    player.sendMessage("§7Используйте: §a/carousel remove <название>");
                    return false;
                }

                if (manager.getCarousel().containsKey(args[1])) {
                    carousel = manager.getCarousel().get(args[1]);
                    carousel.despawn();
                    manager.getCarousel().remove(args[1]);
                }

                try {
                    manager.removeRide(args[1]);
                } catch (IOException exception) {
                    player.sendMessage("§cОшибка: что-то пошло не так с удалением аттракциона." +
                            " Ошибка входа в консоль!");
                    exception.printStackTrace();
                    return true;
                }

                player.sendMessage("§aКарусель " + args[1] + " успешно удалена!");
                return true;
            }
            default: {
                help(player);
            }
        }
        return true;
    }

    private boolean isCarouselSpawned(String name, Player player) {
        if (!manager.getCarousel().containsKey(name)) {
            player.sendMessage("§cКарусель " + name + " не существует!");
            return true;
        } else {
            return false;
        }
    }

    private void help(Player player) {
        for (val str : manager.config().getStringList("help")) {
            player.sendMessage(str);
        }
    }
}
