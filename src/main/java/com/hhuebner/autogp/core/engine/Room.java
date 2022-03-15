package com.hhuebner.autogp.core.engine;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.util.Unit;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Room {

    public String name;
    public RoomType type;
    public double size;
    public List<FurnitureItem> furniture;
    public int generationPriority;

    public Room() {}

    //DEBUG
    @JsonCreator
    public Room(@JsonProperty("name") String name,
                @JsonProperty("type") RoomType type,
                @JsonProperty("size") double size,
                @JsonProperty("furniture") List<FurnitureItem> furniture,
                @JsonProperty("generationPriority") int generationPriority) {
        this.type = type;
        this.size = size;
        this.name = type.name;
        this.furniture = FXCollections.observableArrayList(furniture);
        this.generationPriority = generationPriority;
    }

    public String getName() {
        return this.name;
    }

    public String getSize() {
        return String.valueOf(this.size);
    }

    public String getType() {
        return this.type.toString();
    }

    public static class Builder {
        public RoomType type = null;
        public List<FurnitureItem> furniture = new ArrayList<>();
        public double size = 0.0;
        public Unit unit = Unit.METRES;
        public String name = "";
        public int generationPriority = 0;

        public Builder setType(RoomType type) {
            this.type = type;
            this.furniture = new ArrayList<>(type.defaultFurniture);
            return this;
        }

        public Builder setSize(double size) {
            this.size = size;
            return this;
        }

        public Builder setUnit(Unit unit) {
            this.unit = unit;
            return this;
        }
        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPriority(int generationPriority) {
            this.generationPriority = generationPriority;
            return this;
        }

        public Builder addFurnitureItem(FurnitureItem fi) {
            this.furniture.add(fi);
            return this;
        }

        public boolean canBuild() {
            return type != null;
        }

        public Room build() {
            if(name.equals(""))
                name = this.type.name;

            return new Room(name, type, size, furniture, generationPriority);
        }
    }

    public static class RoomSerializer extends JsonSerializer<ObservableList<FurnitureItem>> {

        public RoomSerializer() {
            super();
        }

        @Override
        public void serialize(ObservableList<FurnitureItem> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartArray();
            for(FurnitureItem fi : value)
                gen.writeObject(fi);

            gen.writeEndArray();
        }

        @Override
        public void serializeWithType(ObservableList<FurnitureItem> value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
            this.serialize(value, gen, serializers);
        }
    }

    public static class RoomDeserializer extends JsonDeserializer<ObservableList<FurnitureItem>> {

        public RoomDeserializer() {
            super();
        }

        @Override
        public ObservableList<FurnitureItem> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            List<FurnitureItem> list = new ArrayList<>();
            p.readValuesAs(FurnitureItem.class).forEachRemaining(f -> list.add(f));
            return FXCollections.observableArrayList(list);
        }

        @Override
        public Object deserializeWithType(JsonParser p, DeserializationContext ctx, TypeDeserializer typeDeserializer) throws IOException {
            return this.deserialize(p, ctx);
        }
    }

}
