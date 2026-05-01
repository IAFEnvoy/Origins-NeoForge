package com.iafenvoy.origins.util.wrapper;

import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;

public interface ContainerWrapper {
    SlotAccess get(int index);

    static ContainerWrapper entity(Entity entity) {
        return new EntityInventory(entity);
    }

    static ContainerWrapper container(Container container) {
        return new StandaloneContainer(container);
    }

    class EntityInventory implements ContainerWrapper {
        private final Entity entity;

        public EntityInventory(Entity entity) {
            this.entity = entity;
        }

        @Override
        public SlotAccess get(int index) {
            return this.entity.getSlot(index);
        }
    }

    class StandaloneContainer implements ContainerWrapper {
        private final Container container;

        public StandaloneContainer(Container container) {
            this.container = container;
        }

        @Override
        public SlotAccess get(int index) {
            if (0 <= index && index < this.container.getContainerSize())
                return SlotAccess.of(() -> this.container.getItem(index), stack -> this.container.setItem(index, stack));
            else return SlotAccess.NULL;
        }
    }
}
