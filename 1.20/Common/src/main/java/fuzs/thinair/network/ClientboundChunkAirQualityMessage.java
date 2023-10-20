package fuzs.thinair.network;

import fuzs.puzzleslib.api.network.v3.ClientMessageListener;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;

public record ClientboundChunkAirQualityMessage(ChunkPos chunkPos, CompoundTag compoundTag) implements ClientboundMessage<ClientboundChunkAirQualityMessage> {

    @Override
    public ClientMessageListener<ClientboundChunkAirQualityMessage> getHandler() {
        return new ClientMessageListener<>() {

            @Override
            public void handle(ClientboundChunkAirQualityMessage message, Minecraft client, ClientPacketListener handler, LocalPlayer player, ClientLevel level) {
                if (level.getChunkSource().hasChunk(message.chunkPos.x, message.chunkPos.z)) {
                    ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.maybeGet(level.getChunkSource().getChunkNow(message.chunkPos.x, message.chunkPos.z)).ifPresent(t -> {
                        t.read(message.compoundTag);
                    });
                }
            }
        };
    }
}
