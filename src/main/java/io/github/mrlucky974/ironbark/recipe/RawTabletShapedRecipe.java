package io.github.mrlucky974.ironbark.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mrlucky974.ironbark.recipe.input.TabletCraftingRecipeInput;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class RawTabletShapedRecipe {
    private static final int MAX_WIDTH_AND_HEIGHT = 3;
    public static final MapCodec<RawTabletShapedRecipe> CODEC;
    public static final PacketCodec<RegistryByteBuf, RawTabletShapedRecipe> PACKET_CODEC;
    private final int width;
    private final int height;
    private final DefaultedList<Ingredient> ingredients;
    private final Optional<RawTabletShapedRecipe.Data> data;
    private final int ingredientCount;
    private final boolean symmetrical;

    public RawTabletShapedRecipe(int width, int height, DefaultedList<Ingredient> ingredients, Optional<RawTabletShapedRecipe.Data> data) {
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
        this.data = data;

        int count = 0;
        for (Ingredient ingredient : ingredients) {
            if (!ingredient.isEmpty()) {
                ++count;
            }
        }
        this.ingredientCount = count;
        this.symmetrical = Util.isSymmetrical(width, height, ingredients);
    }

    public static RawTabletShapedRecipe create(Map<Character, Ingredient> key, String... pattern) {
        return create(key, List.of(pattern));
    }

    public static RawTabletShapedRecipe create(Map<Character, Ingredient> key, List<String> pattern) {
        RawTabletShapedRecipe.Data data = new RawTabletShapedRecipe.Data(key, pattern);
        return (RawTabletShapedRecipe)fromData(data).getOrThrow();
    }

    private static DataResult<RawTabletShapedRecipe> fromData(RawTabletShapedRecipe.Data data) {
        String[] strings = data.pattern.toArray(new String[0]);
        int i = strings[0].length();
        int j = strings.length;
        DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i * j, Ingredient.EMPTY);
        CharSet charSet = new CharArraySet(data.key.keySet());

        for(int k = 0; k < strings.length; ++k) {
            String string = strings[k];

            for(int l = 0; l < string.length(); ++l) {
                char c = string.charAt(l);
                Ingredient ingredient = c == ' ' ? Ingredient.EMPTY : data.key.get(c);
                if (ingredient == null) {
                    return DataResult.error(() -> "Pattern references symbol '" + c + "' but it's not defined in the key");
                }

                charSet.remove(c);
                defaultedList.set(l + i * k, ingredient);
            }
        }

        if (!charSet.isEmpty()) {
            return DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + String.valueOf(charSet));
        } else {
            return DataResult.success(new RawTabletShapedRecipe(i, j, defaultedList, Optional.of(data)));
        }
    }

    @VisibleForTesting
    static String[] removePadding(List<String> pattern) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for(int m = 0; m < pattern.size(); ++m) {
            String string = pattern.get(m);
            i = Math.min(i, findFirstSymbol(string));
            int n = findLastSymbol(string);
            j = Math.max(j, n);
            if (n < 0) {
                if (k == m) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (pattern.size() == l) {
            return new String[0];
        } else {
            String[] strings = new String[pattern.size() - l - k];

            for(int o = 0; o < strings.length; ++o) {
                strings[o] = ((String)pattern.get(o + k)).substring(i, j + 1);
            }

            return strings;
        }
    }

    private static int findFirstSymbol(String line) {
        int i;
        for(i = 0; i < line.length() && line.charAt(i) == ' '; ++i) {
        }

        return i;
    }

    private static int findLastSymbol(String line) {
        int i;
        for(i = line.length() - 1; i >= 0 && line.charAt(i) == ' '; --i) {
        }

        return i;
    }

    public boolean matches(TabletCraftingRecipeInput input) {
        // For tablet crafting, require exact dimensions match
        if (input.getWidth() != this.width || input.getHeight() != this.height) {
            return false;
        }

        // Count non-empty stacks in input
        int nonEmptyStackCount = 0;
        for (int i = 0; i < input.getSize(); i++) {
            if (!input.getStackInSlot(i).isEmpty()) {
                nonEmptyStackCount++;
            }
        }

        if (nonEmptyStackCount != this.ingredientCount) {
            return false;
        }

        // Check exact position matching (no offset allowed)
        if (this.matchesExact(input, false)) {
            return true;
        }

        // Check mirrored orientation if not symmetrical
        if (!this.symmetrical && this.matchesExact(input, true)) {
            return true;
        }

        return false;
    }

    private boolean matchesExact(TabletCraftingRecipeInput input, boolean mirrored) {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                Ingredient ingredient;
                if (mirrored) {
                    ingredient = this.ingredients.get(this.width - x - 1 + y * this.width);
                } else {
                    ingredient = this.ingredients.get(x + y * this.width);
                }

                ItemStack inputStack = input.getStackInSlot(x, y);

                if (ingredient.isEmpty()) {
                    // Recipe expects empty space, input must be empty
                    if (!inputStack.isEmpty()) {
                        return false;
                    }
                } else {
                    // Recipe expects an item, input must match
                    if (!ingredient.test(inputStack)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void writeToBuf(RegistryByteBuf buf) {
        buf.writeVarInt(this.width);
        buf.writeVarInt(this.height);

        for(Ingredient ingredient : this.ingredients) {
            Ingredient.PACKET_CODEC.encode(buf, ingredient);
        }

    }

    private static RawTabletShapedRecipe readFromBuf(RegistryByteBuf buf) {
        int i = buf.readVarInt();
        int j = buf.readVarInt();
        DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i * j, Ingredient.EMPTY);
        defaultedList.replaceAll((ingredient) -> (Ingredient)Ingredient.PACKET_CODEC.decode(buf));
        return new RawTabletShapedRecipe(i, j, defaultedList, Optional.empty());
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public DefaultedList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    static {
        CODEC = RawTabletShapedRecipe.Data.CODEC.flatXmap(RawTabletShapedRecipe::fromData, (recipe) -> recipe.data.map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe")));
        PACKET_CODEC = PacketCodec.of(RawTabletShapedRecipe::writeToBuf, RawTabletShapedRecipe::readFromBuf);
    }

    public static record Data(Map<Character, Ingredient> key, List<String> pattern) {
        private static final Codec<List<String>> PATTERN_CODEC;
        private static final Codec<Character> KEY_ENTRY_CODEC;
        public static final MapCodec<RawTabletShapedRecipe.Data> CODEC;

        static {
            PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap((pattern) -> {
                if (pattern.size() > 3) {
                    return DataResult.error(() -> "Invalid pattern: too many rows, 3 is maximum");
                } else if (pattern.isEmpty()) {
                    return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
                } else {
                    int i = ((String)pattern.getFirst()).length();

                    for(String string : pattern) {
                        if (string.length() > 3) {
                            return DataResult.error(() -> "Invalid pattern: too many columns, 3 is maximum");
                        }

                        if (i != string.length()) {
                            return DataResult.error(() -> "Invalid pattern: each row must be the same width");
                        }
                    }

                    return DataResult.success(pattern);
                }
            }, Function.identity());
            KEY_ENTRY_CODEC = Codec.STRING.comapFlatMap((keyEntry) -> {
                if (keyEntry.length() != 1) {
                    return DataResult.error(() -> "Invalid key entry: '" + keyEntry + "' is an invalid symbol (must be 1 character only).");
                } else {
                    return " ".equals(keyEntry) ? DataResult.error(() -> "Invalid key entry: ' ' is a reserved symbol.") : DataResult.success(keyEntry.charAt(0));
                }
            }, String::valueOf);
            CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(Codecs.strictUnboundedMap(KEY_ENTRY_CODEC, Ingredient.DISALLOW_EMPTY_CODEC).fieldOf("key").forGetter((data) -> data.key), PATTERN_CODEC.fieldOf("pattern").forGetter((data) -> data.pattern)).apply(instance, RawTabletShapedRecipe.Data::new));
        }
    }
}
