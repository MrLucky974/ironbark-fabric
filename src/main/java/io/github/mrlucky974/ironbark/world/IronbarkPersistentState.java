package io.github.mrlucky974.ironbark.world;

import com.mojang.serialization.Codec;
import io.github.mrlucky974.ironbark.Ironbark;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class IronbarkPersistentState extends PersistentState {
    private static final Type<IronbarkPersistentState> type = new Type<>(
            IronbarkPersistentState::createNew,
            IronbarkPersistentState::createFromNbt,
            null
    );

    public HashMap<UUID, PlayerData> players = new HashMap<>();

    public static IronbarkPersistentState getServerState(MinecraftServer server) {
        ServerWorld serverWorld = server.getWorld(World.OVERWORLD);
        assert serverWorld != null;

        IronbarkPersistentState state = serverWorld.getPersistentStateManager().getOrCreate(type, Ironbark.MOD_ID);
        state.markDirty();

        return state;
    }

    public static PlayerData getPlayerState(LivingEntity player) {
        IronbarkPersistentState serverState = getServerState(Objects.requireNonNull(player.getServer()));
        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());
    }

    public static IronbarkPersistentState createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        IronbarkPersistentState state = new IronbarkPersistentState();

        NbtCompound playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            PlayerData playerData = new PlayerData();

            playerData.coins = playersNbt.getCompound(key).getInt("coins");

            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }

    public static IronbarkPersistentState createNew() {
        IronbarkPersistentState state = new IronbarkPersistentState();
        state.players = new HashMap<>();
        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();

            playerNbt.putInt("coins", playerData.coins);

            playersNbt.put(uuid.toString(), playerNbt);
        });

        nbt.put("players", playersNbt);
        return nbt;
    }
}
