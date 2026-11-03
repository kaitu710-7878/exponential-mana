package com.exponentialmana.tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TextureGenerator {

    // Theme colors per tier: [Highlight, Base, Shadow, Outline]
    public static final Color[][] TIER_COLORS = new Color[][]{
            // Tier 0: Cyan / Sky Blue
            { new Color(240, 255, 255), new Color(0, 229, 255), new Color(0, 145, 234), new Color(0, 34, 68) },
            // Tier 1: Coal Charcoal
            { new Color(180, 190, 195), new Color(66, 66, 66), new Color(33, 33, 33), new Color(15, 15, 15) },
            // Tier 2: Copper Orange
            { new Color(255, 204, 188), new Color(255, 112, 67), new Color(216, 67, 21), new Color(78, 52, 46) },
            // Tier 3: Redstone Crimson
            { new Color(255, 205, 210), new Color(255, 23, 68), new Color(213, 0, 0), new Color(62, 39, 35) },
            // Tier 4: Glowstone Amber
            { new Color(255, 253, 231), new Color(255, 238, 88), new Color(255, 171, 0), new Color(78, 52, 46) },
            // Tier 5: Iron Silver
            { new Color(255, 255, 255), new Color(207, 216, 220), new Color(120, 144, 156), new Color(38, 50, 56) },
            // Tier 6: Gold Yellow
            { new Color(255, 249, 196), new Color(255, 215, 0), new Color(255, 160, 0), new Color(62, 39, 35) },
            // Tier 7: Diamond Aqua
            { new Color(255, 255, 255), new Color(128, 216, 255), new Color(0, 176, 255), new Color(1, 87, 155) },
            // Tier 8: Obsidian Purple
            { new Color(243, 229, 245), new Color(171, 71, 188), new Color(74, 20, 140), new Color(18, 0, 94) },
            // Tier 9: Emerald Green
            { new Color(232, 248, 245), new Color(0, 230, 118), new Color(0, 200, 83), new Color(27, 94, 32) },
            // Tier 10: Netherite Dark Violet
            { new Color(237, 231, 246), new Color(124, 77, 255), new Color(55, 42, 61), new Color(26, 26, 36) },
    };

    // Pure Mana colors: Opal white
    public static final Color[] PURE_COLORS = new Color[]{
            new Color(255, 255, 255), new Color(243, 229, 245), new Color(225, 190, 231), new Color(186, 104, 200)
    };

    // Infinite Mana colors: Cosmic
    public static final Color[] INFINITE_COLORS = new Color[]{
            new Color(255, 255, 255), new Color(124, 77, 255), new Color(0, 229, 255), new Color(10, 10, 20)
    };

    public static void main(String[] args) throws IOException {
        String baseDir = args.length > 0 ? args[0] : "src/main/resources/assets/exponential_mana/textures";
        File itemDir = new File(baseDir, "item");
        File blockDir = new File(baseDir, "block");
        itemDir.mkdirs();
        blockDir.mkdirs();

        System.out.println("Generating textures into: " + baseDir);

        // 1. Generate Mana Spheres (Tier 0 to 10)
        for (int t = 0; t <= 10; t++) {
            BufferedImage manaImg = createManaSphere(TIER_COLORS[t]);
            save(manaImg, new File(itemDir, "tier" + t + "_mana.png"));
        }
        save(createManaSphere(PURE_COLORS), new File(itemDir, "pure_mana.png"));
        save(createManaSphere(INFINITE_COLORS), new File(itemDir, "infinite_mana.png"));

        // 2. Generate Cores (Tier 1 to 10): Large theme sphere peeking through casing slits
        for (int t = 1; t <= 10; t++) {
            BufferedImage coreImg = createCoreWithPeekingSphere(TIER_COLORS[t]);
            save(coreImg, new File(itemDir, "tier" + t + "_core.png"));
        }

        // 3. Generate Fairy Catalysts (Tier 1 to 10, Pure)
        for (int t = 1; t <= 10; t++) {
            BufferedImage catImg = createFairyCatalyst(TIER_COLORS[t]);
            save(catImg, new File(itemDir, "tier" + t + "_fairy_catalyst.png"));
        }
        save(createFairyCatalyst(PURE_COLORS), new File(itemDir, "pure_fairy_catalyst.png"));

        // Fairy Residue
        save(createFairyResidue(), new File(itemDir, "fairy_residue.png"));

        // 4. Generate Shards
        for (int t : new int[]{1, 3, 5, 7, 9}) {
            save(createShard(TIER_COLORS[t], false), new File(itemDir, "tier" + t + "_mana_ore_shard.png"));
            save(createShard(TIER_COLORS[t], true), new File(itemDir, "tier" + t + "_fairy_catalyst_shard.png"));
            save(createMiniTable(t), new File(itemDir, "tier" + t + "_mini_crafting_table.png"));
        }

        // 5. Generate Mana Materials & Compressed Materials
        for (int t = 1; t <= 10; t++) {
            save(createMaterialIngot(TIER_COLORS[t], false), new File(itemDir, "tier" + t + "_mana_material.png"));
            save(createMaterialIngot(TIER_COLORS[t], true), new File(itemDir, "compressed_tier" + t + "_mana_material.png"));
        }

        // Additional Compressed Minerals
        save(createCompressedMineral(new Color(25, 25, 112), new Color(65, 105, 225)), new File(itemDir, "compressed_lapis.png"));
        save(createCompressedMineral(new Color(75, 0, 130), new Color(186, 85, 211)), new File(itemDir, "compressed_amethyst.png"));
        save(createCompressedMineral(new Color(180, 180, 180), new Color(245, 245, 245)), new File(itemDir, "compressed_quartz.png"));

        // Mana Symbols (0 to 10)
        for (int t = 0; t <= 10; t++) {
            save(createManaSymbol(TIER_COLORS[t]), new File(itemDir, "mana_symbol_tier" + t + "_mana.png"));
            save(createManaSymbol(TIER_COLORS[t]), new File(itemDir, "mana_symbol_tier" + t + ".png"));
        }

        // Origin Alloy, Mana Root, 4 Manas
        save(createOriginAlloy(false), new File(itemDir, "origin_alloy.png"));
        save(createOriginAlloy(true), new File(itemDir, "compressed_origin_alloy.png"));
        save(createRootOfMana(), new File(itemDir, "mana_root.png"));
        save(createKnowledgeBook(), new File(itemDir, "mana_knowledge.png"));
        save(createKnowledgeBook(), new File(itemDir, "mana_wisdom.png"));

        // 4 Root Manas
        save(createFourMana(new Color(255, 215, 0)), new File(itemDir, "star_mana.png"));
        save(createFourMana(new Color(255, 240, 100)), new File(itemDir, "uncompressed_star_mana.png"));
        save(createFourMana(new Color(0, 229, 255)), new File(itemDir, "quantum_mana.png"));
        save(createFourMana(new Color(128, 222, 234)), new File(itemDir, "uncompressed_quantum_mana.png"));
        save(createFourMana(new Color(124, 77, 255)), new File(itemDir, "abyssal_mana.png"));
        save(createFourMana(new Color(179, 157, 219)), new File(itemDir, "uncompressed_abyssal_mana.png"));
        save(createFourMana(new Color(0, 188, 212)), new File(itemDir, "marine_mana.png"));
        save(createFourMana(new Color(128, 203, 196)), new File(itemDir, "uncompressed_marine_mana.png"));

        // Origin Equipment (Helmet, Chestplate, Leggings, Boots, Sword)
        save(createOriginArmor(0), new File(itemDir, "origin_helmet.png"));
        save(createOriginArmor(1), new File(itemDir, "origin_chestplate.png"));
        save(createOriginArmor(2), new File(itemDir, "origin_leggings.png"));
        save(createOriginArmor(3), new File(itemDir, "origin_boots.png"));
        save(createOriginSword(), new File(itemDir, "origin_sword.png"));
        save(createBatchCompressedMana(), new File(itemDir, "batch_compressed_mana.png"));

        // 6. Generate Blocks
        // Active Mana Ores (0 to 10, Pure)
        for (int t = 0; t <= 10; t++) {
            save(createOreBlock(TIER_COLORS[t], false), new File(blockDir, "tier" + t + "_mana_ore.png"));
        }
        save(createOreBlock(PURE_COLORS, false), new File(blockDir, "pure_mana_ore.png"));

        // Inactive Mana Ores (0 to 10)
        for (int t = 0; t <= 10; t++) {
            save(createOreBlock(TIER_COLORS[t], true), new File(blockDir, "inactive_tier" + t + "_mana_ore.png"));
        }

        // Fairy Block & Compressed (0 to 10)
        save(createFairyBlock(0), new File(blockDir, "fairy_block.png"));
        for (int c = 1; c <= 10; c++) {
            save(createFairyBlock(c), new File(blockDir, "compressed_fairy_block_" + c + ".png"));
        }
        save(createFairyAttractorBlock(), new File(blockDir, "fairy_attractor.png"));
        save(createMachineBlock(new Color(30, 30, 45), new Color(0, 229, 255)), new File(blockDir, "batch_ore_breaker.png"));
        save(createMachineBlock(new Color(40, 50, 60), new Color(128, 222, 234)), new File(blockDir, "thawer.png"));

        // Extended Crafting Tables (1 to 10)
        for (int t = 1; t <= 10; t++) {
            save(createTableBlock(t), new File(blockDir, "tier" + t + "_extended_crafting_table.png"));
        }

        System.out.println("Texture generation complete!");
    }

    /**
     * Creates a glowing mana sphere (16x16) of the specified theme colors.
     */
    public static BufferedImage createManaSphere(Color[] pal) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Sphere coordinates (radius ~4.5 centered at 8, 8)
        int cx = 8, cy = 8;
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                if (dist <= 4.8) {
                    // Internal sphere shading
                    if (dist <= 1.5 && x <= 7 && y <= 7) {
                        img.setRGB(x, y, pal[0].getRGB()); // Bright highlight
                    } else if (x < 7 && y < 7) {
                        img.setRGB(x, y, pal[1].getRGB()); // Base light
                    } else if (dist <= 3.8) {
                        img.setRGB(x, y, pal[2].getRGB()); // Base color
                    } else {
                        img.setRGB(x, y, pal[3].getRGB()); // Dark outline
                    }
                } else if (dist <= 5.8) {
                    // Subtle outer glow/aura
                    Color aura = new Color(pal[1].getRed(), pal[1].getGreen(), pal[1].getBlue(), 70);
                    img.setRGB(x, y, aura.getRGB());
                }
            }
        }
        g.dispose();
        return img;
    }

    /**
     * Creates a Core item: Casing/casing frame with a LARGE theme sphere peeking through slits.
     */
    public static BufferedImage createCoreWithPeekingSphere(Color[] pal) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // First render large theme sphere in the center (radius 5.5)
        int cx = 8, cy = 8;
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                if (dist <= 5.5) {
                    if (dist <= 2.0 && x <= 7 && y <= 7) {
                        img.setRGB(x, y, pal[0].getRGB());
                    } else if (dist <= 3.5) {
                        img.setRGB(x, y, pal[1].getRGB());
                    } else {
                        img.setRGB(x, y, pal[2].getRGB());
                    }
                }
            }
        }

        // Now draw mechanical casing over it (dark metal casing leaving cross-shaped opening and center slit)
        Color frameDark = new Color(35, 38, 42);
        Color frameLight = new Color(75, 80, 88);
        Color frameRivet = new Color(180, 190, 200);

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                // Outer square casing (border 2-13)
                if (x >= 2 && x <= 13 && y >= 2 && y <= 13) {
                    // Slit opening: Leave a vertical slit at x in [7, 8] or horizontal slit at y in [7, 8]
                    // and circular center hole (dist <= 3.2) open so sphere peeks through!
                    double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                    boolean isSlit = (dist <= 3.0) || ((x == 7 || x == 8) && (y >= 4 && y <= 11)) || ((y == 7 || y == 8) && (x >= 4 && x <= 11));

                    if (!isSlit) {
                        // Metal armor plate covering
                        if (x == 2 || y == 2) {
                            img.setRGB(x, y, frameLight.getRGB());
                        } else if (x == 13 || y == 13) {
                            img.setRGB(x, y, frameDark.getRGB());
                        } else if ((x == 3 && y == 3) || (x == 12 && y == 3) || (x == 3 && y == 12) || (x == 12 && y == 12)) {
                            img.setRGB(x, y, frameRivet.getRGB()); // Corner rivets
                        } else {
                            img.setRGB(x, y, new Color(55, 60, 68).getRGB());
                        }
                    }
                }
            }
        }

        g.dispose();
        return img;
    }

    public static BufferedImage createFairyCatalyst(Color[] pal) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        // Glass vial with glowing catalyst crystal inside
        Color glass = new Color(200, 240, 255, 120);
        Color cap = new Color(160, 140, 100);

        // Cap at top
        for (int x = 6; x <= 9; x++) {
            img.setRGB(x, 2, cap.getRGB());
            img.setRGB(x, 3, cap.darker().getRGB());
        }
        // Glass body
        for (int y = 4; y <= 13; y++) {
            for (int x = 5; x <= 10; x++) {
                if (x == 5 || x == 10 || y == 13) {
                    img.setRGB(x, y, glass.getRGB());
                } else {
                    // Internal catalyst sphere
                    double d = Math.sqrt((x - 7.5) * (x - 7.5) + (y - 8.5) * (y - 8.5));
                    if (d <= 2.2) {
                        img.setRGB(x, y, pal[1].getRGB());
                    }
                }
            }
        }
        return img;
    }

    public static BufferedImage createFairyResidue() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color c1 = new Color(0, 230, 118);
        Color c2 = new Color(185, 246, 202);
        int[][] pts = { {7, 5}, {8, 6}, {6, 8}, {8, 8}, {9, 9}, {7, 10}, {5, 11}, {10, 11}, {8, 12} };
        for (int[] p : pts) {
            img.setRGB(p[0], p[1], c1.getRGB());
            if (p[0] + 1 < 16) img.setRGB(p[0] + 1, p[1], c2.getRGB());
        }
        return img;
    }

    public static BufferedImage createShard(Color[] pal, boolean isCatalyst) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int y = 4; y <= 11; y++) {
            for (int x = 4; x <= 11; x++) {
                if (x + y >= 11 && x + y <= 17 && Math.abs(x - y) <= 3) {
                    img.setRGB(x, y, pal[1].getRGB());
                }
            }
        }
        return img;
    }

    public static BufferedImage createMiniTable(int tier) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color wood = new Color(160, 110, 60);
        Color top = new Color(190, 140, 80);
        for (int y = 4; y <= 11; y++) {
            for (int x = 4; x <= 11; x++) {
                img.setRGB(x, y, (y == 4 || y == 5) ? top.getRGB() : wood.getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createMaterialIngot(Color[] pal, boolean compressed) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int y = 5; y <= 10; y++) {
            for (int x = 3; x <= 12; x++) {
                if (y == 5) img.setRGB(x, y, pal[0].getRGB());
                else if (y == 10 || x == 3 || x == 12) img.setRGB(x, y, pal[3].getRGB());
                else img.setRGB(x, y, compressed ? pal[0].getRGB() : pal[1].getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createCompressedMineral(Color dark, Color light) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int y = 4; y <= 11; y++) {
            for (int x = 4; x <= 11; x++) {
                img.setRGB(x, y, ((x + y) % 2 == 0) ? light.getRGB() : dark.getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createManaSymbol(Color[] pal) {
        return createManaSphere(pal);
    }

    public static BufferedImage createOriginAlloy(boolean compressed) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color c = compressed ? new Color(255, 215, 0) : new Color(186, 104, 200);
        for (int y = 4; y <= 11; y++) {
            for (int x = 4; x <= 11; x++) {
                img.setRGB(x, y, c.getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createRootOfMana() {
        return createManaSphere(INFINITE_COLORS);
    }

    public static BufferedImage createKnowledgeBook() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color cover = new Color(100, 20, 140);
        Color page = new Color(240, 230, 200);
        for (int y = 3; y <= 12; y++) {
            for (int x = 4; x <= 11; x++) {
                img.setRGB(x, y, (x == 4 || x == 11 || y == 3 || y == 12) ? cover.getRGB() : page.getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createFourMana(Color base) {
        Color[] pal = new Color[]{ Color.WHITE, base, base.darker(), base.darker().darker() };
        return createManaSphere(pal);
    }

    public static BufferedImage createOriginArmor(int type) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color gold = new Color(255, 215, 0);
        Color dark = new Color(20, 20, 30);
        for (int y = 3; y <= 12; y++) {
            for (int x = 3; x <= 12; x++) {
                img.setRGB(x, y, ((x + y) % 3 == 0) ? gold.getRGB() : dark.getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createOriginSword() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color blade = new Color(0, 229, 255);
        Color hilt = new Color(255, 215, 0);
        for (int i = 2; i <= 10; i++) {
            img.setRGB(15 - i, i, blade.getRGB());
        }
        img.setRGB(3, 12, hilt.getRGB());
        img.setRGB(2, 13, hilt.getRGB());
        return img;
    }

    public static BufferedImage createBatchCompressedMana() {
        return createManaSphere(new Color[]{ Color.WHITE, new Color(0, 229, 255), new Color(124, 77, 255), Color.BLACK });
    }

    public static BufferedImage createOreBlock(Color[] pal, boolean inactive) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color stone = inactive ? new Color(26, 26, 36) : new Color(117, 117, 117);
        Color stoneDark = inactive ? new Color(15, 15, 22) : new Color(90, 90, 90);

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                img.setRGB(x, y, ((x * 7 + y * 13) % 4 == 0) ? stoneDark.getRGB() : stone.getRGB());
            }
        }

        // Crystal ore veins
        int[][] vein = { {4, 4}, {5, 4}, {5, 5}, {10, 9}, {11, 9}, {10, 10}, {11, 10}, {4, 11}, {5, 12} };
        for (int[] p : vein) {
            img.setRGB(p[0], p[1], pal[1].getRGB());
            if (p[0] + 1 < 16) img.setRGB(p[0] + 1, p[1], pal[0].getRGB());
        }

        if (inactive) {
            // Add metal restraint band across the ore
            Color band = new Color(80, 80, 95);
            for (int x = 0; x < 16; x++) {
                img.setRGB(x, 7, band.getRGB());
                img.setRGB(x, 8, band.darker().getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createFairyBlock(int compression) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color mint = new Color(0, 230, 118, 200);
        Color inner = new Color(185, 246, 202);

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                if (x == 0 || x == 15 || y == 0 || y == 15) {
                    img.setRGB(x, y, mint.getRGB());
                } else if ((x >= 6 && x <= 9) && (y >= 6 && y <= 9)) {
                    img.setRGB(x, y, inner.getRGB());
                } else {
                    img.setRGB(x, y, new Color(0, 150, 80, 160).getRGB());
                }
            }
        }
        return img;
    }

    public static BufferedImage createFairyAttractorBlock() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color iron = new Color(140, 145, 150);
        Color core = new Color(0, 230, 118);
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                img.setRGB(x, y, (x >= 5 && x <= 10 && y >= 5 && y <= 10) ? core.getRGB() : iron.getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createMachineBlock(Color frame, Color energy) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                if (x >= 4 && x <= 11 && y >= 4 && y <= 11) {
                    img.setRGB(x, y, energy.getRGB());
                } else {
                    img.setRGB(x, y, frame.getRGB());
                }
            }
        }
        return img;
    }

    public static BufferedImage createTableBlock(int tier) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color wood = new Color(140, 95, 55);
        Color rune = (tier <= 10) ? TIER_COLORS[Math.min(10, tier)][1] : Color.WHITE;
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                if ((x == 7 || x == 8 || y == 7 || y == 8) && (x >= 3 && x <= 12 && y >= 3 && y <= 12)) {
                    img.setRGB(x, y, rune.getRGB());
                } else {
                    img.setRGB(x, y, wood.getRGB());
                }
            }
        }
        return img;
    }

    private static void save(BufferedImage img, File file) throws IOException {
        ImageIO.write(img, "PNG", file);
    }
}
