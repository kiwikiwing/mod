package com.kiwi.dungeonkemono.item;

import com.kiwi.dungeonkemono.DungeonKemono;
import com.kiwi.dungeonkemono.item.weapons.warrior.GreatSwordItem;
import com.kiwi.dungeonkemono.item.weapons.archer.LongBowItem;
import com.kiwi.dungeonkemono.item.weapons.mage.StaffItem;
import com.kiwi.dungeonkemono.item.weapons.rogue.DaggerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 모든 커스텀 아이템 등록
 */
public class DKItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DungeonKemono.MOD_ID);

    // === 전사 무기 ===
    public static final RegistryObject<Item> GREATSWORD = ITEMS.register(
            "greatsword",
            GreatSwordItem::new
    );

    // TODO: 검+방패, 도(카타나) 추가

    // === 궁수 무기 ===
    public static final RegistryObject<Item> LONGBOW = ITEMS.register(
            "longbow",
            LongBowItem::new
    );

    // TODO: 각궁, 석궁 추가

    // === 마법사 무기 ===
    public static final RegistryObject<Item> STAFF = ITEMS.register(
            "staff",
            StaffItem::new
    );

    // TODO: 마법서 추가

    // === 도적 무기 ===
    public static final RegistryObject<Item> DAGGER = ITEMS.register(
            "dagger",
            DaggerItem::new
    );

    // TODO: 쌍검 추가
}