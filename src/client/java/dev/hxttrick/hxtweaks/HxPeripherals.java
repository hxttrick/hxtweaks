package dev.hxttrick.hxtweaks;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HxPeripherals {

    private static List<ItemStack> getPeripheralsInInventory(ClientPlayerEntity player) {
        List<ItemStack> peripherals = new ArrayList<>();

        List<ItemStack> inventory = player.getInventory().getMainStacks().subList(PlayerInventory.HOTBAR_SIZE, PlayerInventory.MAIN_SIZE);

        for (ItemStack itemStack : PERIPHERAL_ITEMS) {
            Item item = itemStack.getItem();
            boolean hasPeripheral = inventory.stream().anyMatch(stack -> stack.isOf(item));
            if (hasPeripheral) peripherals.add(itemStack);
        }

        return peripherals;
    }

    public static void renderPeripherals(DrawContext context, ClientPlayerEntity player, Consumer<PeripheralSlot> callback) {
        List<ItemStack> peripheralItems = getPeripheralsInInventory(player);
        if (peripheralItems.isEmpty()) return;

        boolean leftHanded = player.getMainArm() == Arm.LEFT;

        SlotBuilder slotBuilder = new SlotBuilder(context, peripheralItems)
                .alignToHotbar(leftHanded, 7);

        slotBuilder.build(callback);
    }

    private static final List<ItemStack> PERIPHERAL_ITEMS = List.of(
            new ItemStack(Items.CLOCK),
            new ItemStack(Items.COMPASS)
    );

    public static class PeripheralSlot {
        public static final PeripheralSlot SINGLE = new PeripheralSlot(22, 3);
        public static final PeripheralSlot START = new PeripheralSlot(21, 3);
        public static final PeripheralSlot MIDDLE = new PeripheralSlot(20, 2);
        public static final PeripheralSlot END = new PeripheralSlot(21, 2);

        public static final Identifier SPRITE = Identifier.of(HxTweaksClient.MOD_ID, "hud/peripheral_slots");
        public static final int SPRITE_HEIGHT = 22, SPRITE_WIDTH = 84;

        private static int prevU = 0;

        public int x, y;
        public final int u, v = 0;
        public final int width, height = SPRITE_HEIGHT;
        public final int itemOffsetX, itemOffsetY = 3;
        public ItemStack peripheral;

        private PeripheralSlot(int width, int itemOffset) {
            this.u = prevU;
            this.width = width;
            this.itemOffsetX = itemOffset;

            prevU += width;
        }
    }

    private static class SlotBuilder {
        private final DrawContext context;

        private final List<ItemStack> peripherals;
        private List<PeripheralSlot> slots;
        private int x, y, width;

        public SlotBuilder(DrawContext context, List<ItemStack> peripherals) {
            this.context = context;
            this.peripherals = peripherals;
            this.setSlotAmount(peripherals.size());
        }

        public void build(Consumer<PeripheralSlot> callback) {
            for (var slot : slots) {
                slot.x = x; slot.y = y;
                context.drawGuiTexture(
                        RenderPipelines.GUI_TEXTURED,
                        PeripheralSlot.SPRITE,
                        PeripheralSlot.SPRITE_WIDTH, PeripheralSlot.SPRITE_HEIGHT,
                        slot.u, slot.v,
                        slot.x, slot.y,
                        slot.width, slot.height
                );
                callback.accept(slot);
                x += slot.width;
            }
        }

        private void setSlotAmount(int amount) {
            slots = new ArrayList<>(amount);
            int start = 0, end = amount - 1;

            for (int i = 0; i < amount; i++) {
                PeripheralSlot slot;

                if (start == end)
                    slot = PeripheralSlot.SINGLE;
                else if (i == start)
                    slot = PeripheralSlot.START;
                else if (i == end)
                    slot = PeripheralSlot.END;
                else
                    slot = PeripheralSlot.MIDDLE;

                slot.peripheral = peripherals.get(i);
                slots.add(slot);
                this.width += slot.width;
            }
        }

        public SlotBuilder alignToHotbar(boolean leftHanded, int margin) {
            int offsetX = 91 + margin; // 91 is half hotbar width, taken from InGameHud$renderHotbar
            if (leftHanded) offsetX = -(offsetX + width);
            x = context.getScaledWindowWidth() / 2 + offsetX;

            y = context.getScaledWindowHeight() - PeripheralSlot.SPRITE_HEIGHT;

            return this;
        }

        public SlotBuilder setPosition(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }
    }
}
