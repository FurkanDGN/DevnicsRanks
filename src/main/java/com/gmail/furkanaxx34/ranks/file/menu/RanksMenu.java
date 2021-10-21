package com.gmail.furkanaxx34.ranks.file.menu;

import com.cryptomorin.xseries.XMaterial;
import com.gmail.furkanaxx34.ranks.Ranks;
import com.gmail.furkanaxx34.ranks.file.RanksConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tr.com.infumia.infumialib.paper.bukkititembuilder.ItemStackBuilder;
import tr.com.infumia.infumialib.paper.bukkititembuilder.SkullItemBuilder;
import tr.com.infumia.infumialib.paper.color.XColor;
import tr.com.infumia.infumialib.paper.element.FileElement;
import tr.com.infumia.infumialib.paper.smartinventory.*;
import tr.com.infumia.infumialib.paper.smartinventory.util.SlotPos;
import tr.com.infumia.infumialib.paper.transformer.resolvers.BukkitSnakeyaml;
import tr.com.infumia.infumialib.paper.transformer.serializers.ItemStackSerializer;
import tr.com.infumia.infumialib.replaceable.RpList;
import tr.com.infumia.infumialib.replaceable.RpString;
import tr.com.infumia.infumialib.transformer.TransformedObject;
import tr.com.infumia.infumialib.transformer.TransformerPool;
import tr.com.infumia.infumialib.transformer.annotations.Exclude;
import tr.com.infumia.infumialib.transformer.annotations.Names;
import tr.com.infumia.infumialib.transformer.annotations.Version;

import java.io.File;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Version
@Names(modifier = Names.Modifier.TO_LOWER_CASE, strategy = Names.Strategy.HYPHEN_CASE)
public class RanksMenu extends ARanksMenu {

    public static RpString title = RpString.from("&eRank System").map(XColor::colorize);

    public static ItemStack rankItem = ItemStackBuilder.from(XMaterial.SLIME_BALL)
            .setName("&e%rank_name%")
            .setLore(
                    "&7When you have",
                    "&7enough money, you'll be",
                    "&7automatically moved to this rank.", "",
                    "&ePrice: %rank_price%")
            .getItemStack();
    
    public static FileElement skull = FileElement.insert(ItemStackBuilder.from(XMaterial.PLAYER_WALL_HEAD)
            .setName("&a%player%")
            .setLore(
                    "&eYour prestige points: &a%prestige_points%"
            ).getItemStack(),
            0,
            4,
            clickEvent -> {}
    );

    @Nullable
    @Exclude
    private static TransformedObject instance;

    public static void loadConfig(final Ranks plugin) {
        if (RanksMenu.instance == null) {
            RanksMenu.instance = TransformerPool.create(new RanksMenu())
                    .withFile(new File(plugin.getDataFolder(), "menus/ranks-menu.yml"))
                    .withResolver(new BukkitSnakeyaml())
                    .withTransformPack(registry -> registry
                            .withSerializers(new ItemStackSerializer(), new FileElement.Serializer()));
        }
        RanksMenu.instance.initiate();
    }

    public static void openMenu(final Player player) {
        final String titleBuild = RanksMenu.title.build();

        InventoryProvider provider = new InventoryProvider() {
            @Override
            public void init(@NotNull InventoryContents contents) {
                List<String> ranks = RanksConfig.ranks;
                Icon[] icons = new Icon[ranks.size()];
                for (int i = 0; i < ranks.size(); i++) {
                    try {
                        String[] parts = ranks.get(i).split(": ");
                        String rankName = parts[0];
                        int price = Integer.parseInt(parts[1]);
                        double realPrice = Ranks.getInstance().getAPI().calcLevelPrice(player, price);

                        ItemStack clone = RanksMenu.rankItem.clone();
                        ItemStackBuilder content = ItemStackBuilder.from(clone)
                                .setName(RpString.from(clone.getItemMeta().getDisplayName())
                                        .regex("%rank_name%")
                                        .build(
                                                Map.entry("%rank_name%", () -> rankName)
                                        ))
                                .setLore(
                                        RpList.from(clone.getItemMeta().getLore())
                                                .regex("%rank_price%")
                                                .build(Map.entry("%rank_price%", () -> String.valueOf(realPrice)))
                                );

                        boolean passed = Ranks.getInstance().getAPI().isPassed(player, price);
                        if (passed) {
                            content.addEnchantments(Enchantment.SILK_TOUCH, 1);
                        }

                        ItemStack contentItem = content.getItemStack();
                        Icon icon = Icon.from(contentItem);
                        icons[i] = icon;
                    }catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                        Ranks.getInstance().getLogger().warning((i + 1) + ". config rank set as wrong. Please load \"config.yml\" file.");
                    }
                }
                final Pagination pagination = contents.pagination();
                pagination.setIconsPerPage(9);
                pagination.setIcons(icons);
                pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 0)));
                SkullMeta meta = (SkullMeta) skull.getItemStack().getItemMeta();
                ItemStack skullContent = SkullItemBuilder.from(meta, skull.getItemStack())
                        .setName(RpString.from(meta.getDisplayName())
                                .regex("%player%")
                                .build(Map.entry("%player%",
                                        player::getName
                                )))
                        .setLore(RpList.from(meta.getLore())
                                .regex("%prestige_points%")
                                .build(
                                        Map.entry("%prestige_points%",
                                                () -> Ranks.getInstance().getAPI().getPrestige(player.getUniqueId()))))
                        .setOwner(player.getName())
                        .getItemStack();

                skull.changeItemStack(skullContent).place(contents);
            }
        };

        openPage(player, provider, 3, "time-manage-menu", titleBuild);
    }
}
