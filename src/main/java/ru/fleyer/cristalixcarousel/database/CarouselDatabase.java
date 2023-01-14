package ru.fleyer.cristalixcarousel.database;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.val;
import org.bson.Document;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import ru.fleyer.cristalixcarousel.model.manager.CarouselManager;

import java.util.Date;
import java.util.Objects;

public class CarouselDatabase {
    public static CarouselDatabase INSTANCE = new CarouselDatabase();

    private MongoClient mongoClient;
    private MongoDatabase db;
    private MongoCollection<Document> collection;

    public void connect() {

        mongoClient = new MongoClient(
                CarouselManager.INSTANCE.config().getString("db.host"),
                CarouselManager.INSTANCE.config().getInt("db.port"));

        db = mongoClient.getDatabase(CarouselManager.INSTANCE.config().getString("db.database"));
        collection = db.getCollection(CarouselManager.INSTANCE.config().getString("db.collection"));
    }


    public void startRidingSession(Player player, Horse horse) {
        val doc = new Document("player", player.getUniqueId().toString())
                .append("startTime", new Date(System.currentTimeMillis()))
                .append("horseType", horse.getName())
                .append("horseName", horse.getCustomName());
        collection.insertOne(doc);
    }

    public void endRidingSession(Player player) {
        val filter = new Document("player", player.getUniqueId().toString())
                .append("endTime", new Document("$exists", false));
        FindIterable<Document> result = collection.find(filter);
        if (result.first() != null) {
            val update = new Document("$set", new Document("endTime", new Date()));
            collection.updateOne(Objects.requireNonNull(result.first()), update);
        }
    }


}
