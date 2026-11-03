package com.exponentialmana.tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PreviewTextureGenerator {

    public static final Color[][] TIER_PALETTES = new Color[][]{
            { new Color(245, 255, 255), new Color(140, 235, 255), new Color(60, 190, 230), new Color(20, 120, 170) },
            { new Color(40, 40, 44), new Color(22, 22, 25), new Color(12, 12, 14), new Color(210, 40, 10) },
            { new Color(255, 204, 188), new Color(255, 112, 67), new Color(216, 67, 21), new Color(78, 52, 46) },
            { new Color(255, 205, 210), new Color(255, 23, 68), new Color(213, 0, 0), new Color(62, 39, 35) },
            { new Color(255, 253, 231), new Color(255, 238, 88), new Color(255, 171, 0), new Color(78, 52, 46) },
            { new Color(255, 255, 255), new Color(207, 216, 220), new Color(120, 144, 156), new Color(38, 50, 56) },
            { new Color(255, 249, 196), new Color(255, 215, 0), new Color(255, 160, 0), new Color(62, 39, 35) },
            { new Color(255, 255, 255), new Color(40, 240, 255), new Color(0, 160, 220), new Color(0, 70, 130) },
            { new Color(255, 160, 255), new Color(213, 0, 249), new Color(80, 15, 110), new Color(25, 10, 35) },
            { new Color(232, 248, 245), new Color(0, 230, 118), new Color(0, 200, 83), new Color(27, 94, 32) },
            { new Color(255, 230, 120), new Color(255, 190, 0), new Color(75, 60, 65), new Color(35, 30, 35) }
    };

    static final Color C_DEEP_BORDER = new Color(0, 105, 55);
    static final Color C_FRAME       = new Color(0, 145, 80);
    static final Color C_BG_DARK     = new Color(0, 175, 95);
    static final Color C_BG_MID      = new Color(0, 205, 115);
    static final Color C_BG_LIGHT    = new Color(30, 225, 135);
    static final Color C_MINT_GLOW   = new Color(110, 255, 185);
    static final Color C_MINT_BRIGHT = new Color(175, 255, 220);
    static final Color C_WHITE_CORE  = new Color(250, 255, 252);

    public static void main(String[] args) throws IOException {
        File previewDir = new File("tools/preview");
        previewDir.mkdirs();

        // New cache-busting directory 'textures_preview_v5'
        File brainPreviewDir = new File("C:/Users/yutak/.gemini/antigravity/brain/f3f9836b-6575-48ad-a44b-e506f4128e8d/textures_preview_v5");
        brainPreviewDir.mkdirs();

        // 1. Fairy Blocks (0 to 10) with even x even pixel alignment matching 16x16
        for (int c = 0; c <= 10; c++) {
            BufferedImage fairyBlock = createEvenPixelFairyBlock(c);
            String name = (c == 0) ? "fairy_block" : "compressed_fairy_block_" + c;
            saveBoth(fairyBlock, previewDir, brainPreviewDir, name);
        }

        // 2. Fairy Attractors (1 to 10) with thick black casing and perfectly aligned 8x8 even fairy core
        for (int c = 1; c <= 10; c++) {
            BufferedImage attractor = createEvenPixelFairyAttractor(c);
            saveBoth(attractor, previewDir, brainPreviewDir, "compressed_fairy_attractor_" + c);
        }

        // 3. Cores (Tier 1 to 10)
        for (int t = 1; t <= 10; t++) {
            BufferedImage core = createHarmonizedCore(t);
            saveBoth(core, previewDir, brainPreviewDir, "tier" + t + "_core");
        }

        // 4. Fairy Catalysts (Tier 1 to 10)
        for (int t = 1; t <= 10; t++) {
            BufferedImage catalyst = createHarmonizedCatalyst(t);
            saveBoth(catalyst, previewDir, brainPreviewDir, "tier" + t + "_fairy_catalyst");
        }

        System.out.println("Generated all textures with even-pixel alignment for preview_v5 successfully.");
    }

    private static void saveBoth(BufferedImage img, File previewDir, File brainDir, String name) throws IOException {
        ImageIO.write(img, "png", new File(previewDir, name + ".png"));
        BufferedImage x4 = scale4x(img);
        ImageIO.write(x4, "png", new File(brainDir, name + "_v5_x4.png"));
    }

    private static BufferedImage scale4x(BufferedImage src) {
        BufferedImage dst = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dst.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(src, 0, 0, 64, 64, null);
        g.dispose();
        return dst;
    }

    /**
     * Fairy Block with EVEN x EVEN pixel cluster alignment.
     * Center is precisely at (7..8, 7..8).
     */
    public static BufferedImage createEvenPixelFairyBlock(int tier) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                if (x == 0 || x == 15 || y == 0 || y == 15) {
                    img.setRGB(x, y, C_DEEP_BORDER.getRGB());
                } else if (x == 1 || x == 14 || y == 1 || y == 14) {
                    img.setRGB(x, y, C_FRAME.getRGB());
                } else {
                    int hash = (x * 37 + y * 17 + tier * 13) % 7;
                    if (hash == 0) img.setRGB(x, y, C_BG_DARK.getRGB());
                    else if (hash == 1 || hash == 2) img.setRGB(x, y, C_BG_LIGHT.getRGB());
                    else img.setRGB(x, y, C_BG_MID.getRGB());
                }
            }
        }

        drawEvenFairyCluster(img, tier);
        return img;
    }

    /**
     * Fairy Attractor with thick solid black casing (4px around) and EVEN x EVEN fairy core in center (4..11, 4..11).
     * Center is precisely at (7..8, 7..8) matching the fairy block!
     */
    public static BufferedImage createEvenPixelFairyAttractor(int tier) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        Color blackOuter = new Color(12, 12, 14);
        Color blackBody  = new Color(22, 22, 26);
        Color blackBevel = new Color(34, 34, 40);
        Color rivet      = new Color(44, 44, 50);

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                boolean inCenter = (x >= 4 && x <= 11 && y >= 4 && y <= 11);
                if (!inCenter) {
                    if (x == 0 || x == 15 || y == 0 || y == 15) {
                        img.setRGB(x, y, blackOuter.getRGB());
                    } else if (x == 3 || x == 12 || y == 3 || y == 12) {
                        img.setRGB(x, y, blackBevel.getRGB());
                    } else if ((x == 1 || x == 14) && (y == 1 || y == 14)) {
                        img.setRGB(x, y, rivet.getRGB());
                    } else {
                        img.setRGB(x, y, blackBody.getRGB());
                    }
                } else {
                    int hash = (x * 37 + y * 17 + tier * 13) % 5;
                    if (hash == 0) img.setRGB(x, y, C_BG_DARK.getRGB());
                    else if (hash == 1) img.setRGB(x, y, C_BG_LIGHT.getRGB());
                    else img.setRGB(x, y, C_BG_MID.getRGB());
                }
            }
        }

        drawEvenFairyCluster(img, tier);
        return img;
    }

    /**
     * Helper to fill a 2x2 block cleanly:
     */
    private static void fill2x2(BufferedImage img, int x, int y, Color c) {
        setPix(img, x, y, c);
        setPix(img, x + 1, y, c);
        setPix(img, x, y + 1, c);
        setPix(img, x + 1, y + 1, c);
    }

    private static void setPix(BufferedImage img, int x, int y, Color c) {
        if (x >= 0 && x < 16 && y >= 0 && y < 16) {
            img.setRGB(x, y, c.getRGB());
        }
    }

    /**
     * All patterns designed strictly with EVEN x EVEN pixel geometry centered at (7..8, 7..8).
     * Eliminates off-by-one misalignment on 16x16 / 8x8 grids!
     */
    private static void drawEvenFairyCluster(BufferedImage img, int tier) {
        // True geometric center of 16x16 is the 2x2 box: (7..8, 7..8)
        switch (tier) {
            case 0 -> { // Tier 0: Single 2x2 gentle wisp
                fill2x2(img, 7, 7, C_WHITE_CORE);
                setPix(img, 6, 7, C_MINT_BRIGHT);
                setPix(img, 6, 8, C_MINT_BRIGHT);
                setPix(img, 9, 7, C_MINT_GLOW);
                setPix(img, 9, 8, C_MINT_GLOW);
                setPix(img, 7, 6, C_MINT_BRIGHT);
                setPix(img, 8, 6, C_MINT_BRIGHT);
                setPix(img, 7, 9, C_MINT_GLOW);
                setPix(img, 8, 9, C_MINT_GLOW);
            }
            case 1 -> { // Tier 1: Dual 2x2 floating wisps
                fill2x2(img, 5, 5, C_WHITE_CORE);
                setPix(img, 4, 5, C_MINT_BRIGHT);
                setPix(img, 5, 4, C_MINT_BRIGHT);

                fill2x2(img, 9, 9, C_WHITE_CORE);
                setPix(img, 11, 9, C_MINT_BRIGHT);
                setPix(img, 10, 11, C_MINT_BRIGHT);

                fill2x2(img, 7, 7, C_MINT_GLOW); // connection
            }
            case 2 -> { // Tier 2: Tri-cluster of 2x2 wisps
                fill2x2(img, 7, 5, C_WHITE_CORE);
                fill2x2(img, 5, 9, C_WHITE_CORE);
                fill2x2(img, 9, 9, C_WHITE_CORE);
                fill2x2(img, 7, 7, C_MINT_BRIGHT);
            }
            case 3 -> { // Tier 3: Growing crystal blade (even 2x2 stepped diagonal)
                fill2x2(img, 7, 7, C_WHITE_CORE);
                fill2x2(img, 5, 5, C_MINT_BRIGHT);
                fill2x2(img, 9, 9, C_MINT_BRIGHT);
                fill2x2(img, 3, 3, C_MINT_GLOW);
                fill2x2(img, 11, 11, C_MINT_GLOW);
            }
            case 4 -> { // Tier 4: Quad-core cluster (4 corners of 6x6 area, all 2x2)
                fill2x2(img, 5, 5, C_WHITE_CORE);
                fill2x2(img, 9, 5, C_WHITE_CORE);
                fill2x2(img, 5, 9, C_WHITE_CORE);
                fill2x2(img, 9, 9, C_WHITE_CORE);
                fill2x2(img, 7, 7, C_MINT_BRIGHT); // center core
            }
            case 5 -> { // Tier 5 (9^5): 4x4 Brilliant Core + 2x2 Radiant Ray Accents
                // Center 4x4 Core perfectly aligned at (6..9, 6..9)
                fill2x2(img, 7, 7, Color.WHITE);
                fill2x2(img, 5, 7, C_WHITE_CORE);
                fill2x2(img, 9, 7, C_WHITE_CORE);
                fill2x2(img, 7, 5, C_WHITE_CORE);
                fill2x2(img, 7, 9, C_WHITE_CORE);
                // Outer ray accents (all 2x2 blocks)
                fill2x2(img, 3, 7, C_MINT_GLOW);
                fill2x2(img, 11, 7, C_MINT_GLOW);
                fill2x2(img, 7, 3, C_MINT_GLOW);
                fill2x2(img, 7, 11, C_MINT_GLOW);
                // Star points
                fill2x2(img, 5, 5, C_MINT_BRIGHT);
                fill2x2(img, 9, 9, C_MINT_BRIGHT);
            }
            case 6 -> { // Tier 6 (9^6): Crystal Geode (6x6 cross + 2x2 diagonal crystal nodes)
                // Center 4x4
                for (int y = 6; y <= 9; y++) {
                    for (int x = 6; x <= 9; x++) {
                        img.setRGB(x, y, C_WHITE_CORE.getRGB());
                    }
                }
                fill2x2(img, 7, 7, Color.WHITE);
                // 4 diagonal satellite nodes (each 2x2)
                fill2x2(img, 4, 4, C_MINT_BRIGHT);
                fill2x2(img, 10, 4, C_WHITE_CORE);
                fill2x2(img, 4, 10, C_MINT_GLOW);
                fill2x2(img, 10, 10, C_MINT_BRIGHT);
                // Cardinal connectors
                fill2x2(img, 7, 4, C_MINT_GLOW);
                fill2x2(img, 7, 10, C_MINT_GLOW);
            }
            case 7 -> { // Tier 7 (9^7): Concentric Even Ring + 4-corner Spark Flares
                // Center 2x2 pure white
                fill2x2(img, 7, 7, Color.WHITE);
                // Inner 6x6 ring (x,y: 5..10)
                for (int i = 5; i <= 10; i++) {
                    setPix(img, i, 5, C_MINT_BRIGHT);
                    setPix(img, i, 10, C_MINT_BRIGHT);
                    setPix(img, 5, i, C_MINT_BRIGHT);
                    setPix(img, 10, i, C_MINT_BRIGHT);
                }
                // 4 corner flares (each 2x2)
                fill2x2(img, 3, 3, C_WHITE_CORE);
                fill2x2(img, 11, 3, C_WHITE_CORE);
                fill2x2(img, 3, 11, C_WHITE_CORE);
                fill2x2(img, 11, 11, C_WHITE_CORE);
                // Outer 10x10 aura accents
                fill2x2(img, 7, 2, C_MINT_GLOW);
                fill2x2(img, 7, 12, C_MINT_GLOW);
                fill2x2(img, 2, 7, C_MINT_GLOW);
                fill2x2(img, 12, 7, C_MINT_GLOW);
            }
            case 8 -> { // Tier 8 (9^8): Dense Swirling Energy Vortex (Even spiral blocks)
                // Center 4x4
                for (int y = 6; y <= 9; y++) {
                    for (int x = 6; x <= 9; x++) {
                        img.setRGB(x, y, C_WHITE_CORE.getRGB());
                    }
                }
                fill2x2(img, 7, 7, Color.WHITE);
                // Spiral arms in stepped 2x2 chunks
                fill2x2(img, 9, 5, C_WHITE_CORE);
                fill2x2(img, 11, 5, C_MINT_BRIGHT);
                fill2x2(img, 11, 7, C_MINT_GLOW);

                fill2x2(img, 9, 11, C_WHITE_CORE);
                fill2x2(img, 7, 11, C_MINT_BRIGHT);
                fill2x2(img, 5, 11, C_MINT_GLOW);

                fill2x2(img, 5, 9, C_WHITE_CORE);
                fill2x2(img, 3, 9, C_MINT_BRIGHT);
                fill2x2(img, 3, 7, C_MINT_GLOW);

                fill2x2(img, 5, 3, C_WHITE_CORE);
                fill2x2(img, 7, 3, C_MINT_BRIGHT);
                fill2x2(img, 9, 3, C_MINT_GLOW);
            }
            case 9 -> { // Tier 9 (9^9): Great Fairy Nebula (Dense even crystal matrix)
                // Center 4x4 bright nova
                for (int y = 6; y <= 9; y++) {
                    for (int x = 6; x <= 9; x++) {
                        img.setRGB(x, y, Color.WHITE.getRGB());
                    }
                }
                // 4 diagonal star nodes (each 2x2)
                fill2x2(img, 3, 3, C_WHITE_CORE);
                fill2x2(img, 11, 3, C_WHITE_CORE);
                fill2x2(img, 3, 11, C_WHITE_CORE);
                fill2x2(img, 11, 11, C_WHITE_CORE);
                // 4 cardinal beams (each 2x2)
                fill2x2(img, 7, 3, C_MINT_BRIGHT);
                fill2x2(img, 7, 11, C_MINT_BRIGHT);
                fill2x2(img, 3, 7, C_MINT_BRIGHT);
                fill2x2(img, 11, 7, C_MINT_BRIGHT);
                // Outer sparkle nodes
                fill2x2(img, 5, 5, C_MINT_GLOW);
                fill2x2(img, 9, 5, C_MINT_GLOW);
                fill2x2(img, 5, 9, C_MINT_GLOW);
                fill2x2(img, 9, 9, C_MINT_GLOW);
            }
            case 10 -> { // Tier 10 (9^10): Cosmic Fairy Supernova (Ultimate harmonic nova)
                // Center 6x6 blinding core (5..10, 5..10)
                for (int y = 5; y <= 10; y++) {
                    for (int x = 5; x <= 10; x++) {
                        int dist = Math.abs(x - 7) + Math.abs(y - 7);
                        if (dist <= 2) img.setRGB(x, y, Color.WHITE.getRGB());
                        else img.setRGB(x, y, C_WHITE_CORE.getRGB());
                    }
                }
                // Surrounding 8x8 border
                for (int i = 4; i <= 11; i++) {
                    setPix(img, i, 4, C_MINT_BRIGHT);
                    setPix(img, i, 11, C_MINT_BRIGHT);
                    setPix(img, 4, i, C_MINT_BRIGHT);
                    setPix(img, 11, i, C_MINT_BRIGHT);
                }
                // 4 cardinal rays extending to outer edges (each 2x2)
                fill2x2(img, 7, 2, Color.WHITE);
                fill2x2(img, 7, 12, Color.WHITE);
                fill2x2(img, 2, 7, Color.WHITE);
                fill2x2(img, 12, 7, Color.WHITE);
                // 4 corner flares (each 2x2)
                fill2x2(img, 3, 3, C_MINT_GLOW);
                fill2x2(img, 11, 3, C_MINT_GLOW);
                fill2x2(img, 3, 11, C_MINT_GLOW);
                fill2x2(img, 11, 11, C_MINT_GLOW);
            }
        }
    }

    public static BufferedImage createHarmonizedCore(int tier) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color[] pal = TIER_PALETTES[tier];

        int cx = 8, cy = 8;
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                if (dist <= 5.4) {
                    if (tier == 1) {
                        if (dist <= 2.0 && x <= 7 && y <= 7) img.setRGB(x, y, new Color(34, 34, 38).getRGB());
                        else if (dist <= 3.8) img.setRGB(x, y, new Color(20, 20, 24).getRGB());
                        else img.setRGB(x, y, new Color(12, 12, 14).getRGB());
                    } else {
                        if (dist <= 2.0 && x <= 7 && y <= 7) img.setRGB(x, y, pal[0].getRGB());
                        else if (dist <= 3.8) img.setRGB(x, y, pal[1].getRGB());
                        else img.setRGB(x, y, pal[2].getRGB());
                    }
                }
            }
        }

        if (tier == 1) {
            img.setRGB(8, 7, new Color(215, 45, 12).getRGB());
            img.setRGB(7, 8, new Color(170, 30, 8).getRGB());
        } else if (tier == 7) {
            img.setRGB(7, 7, Color.WHITE.getRGB());
            img.setRGB(6, 6, new Color(180, 255, 255).getRGB());
            img.setRGB(9, 9, new Color(0, 130, 200).getRGB());
        } else if (tier == 8) {
            img.setRGB(7, 6, new Color(255, 180, 255).getRGB());
            img.setRGB(8, 7, new Color(213, 0, 249).getRGB());
            img.setRGB(7, 8, new Color(120, 0, 160).getRGB());
        } else if (tier == 10) {
            img.setRGB(7, 7, new Color(255, 240, 160).getRGB());
            img.setRGB(8, 6, new Color(255, 200, 40).getRGB());
            img.setRGB(6, 8, new Color(255, 200, 40).getRGB());
        }

        Color casing = (tier == 1) ? new Color(22, 22, 25) : (tier == 10) ? new Color(45, 38, 42) : (tier == 8) ? new Color(30, 20, 35) : new Color(55, 60, 65);
        Color casingBorder = (tier == 1) ? new Color(36, 36, 40) : (tier == 10) ? new Color(230, 185, 50) : (tier == 8) ? new Color(120, 50, 150) : (tier == 7) ? new Color(140, 210, 235) : new Color(95, 100, 105);

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                boolean inOuterBox = (x >= 2 && x <= 13 && y >= 2 && y <= 13);
                boolean isBorder = (x == 2 || x == 13 || y == 2 || y == 13);
                boolean isSlit = ((x >= 6 && x <= 9 && y >= 4 && y <= 11) || (y >= 6 && y <= 9 && x >= 4 && x <= 11));

                if (inOuterBox) {
                    if (isSlit) {
                    } else if (isBorder) {
                        img.setRGB(x, y, casingBorder.getRGB());
                    } else {
                        img.setRGB(x, y, casing.getRGB());
                    }
                }
            }
        }
        return img;
    }

    public static BufferedImage createHarmonizedCatalyst(int tier) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color[] pal = TIER_PALETTES[tier];

        Color glass = new Color(210, 245, 255, 160);
        Color glassShine = new Color(255, 255, 255, 220);
        Color cap = (tier == 10) ? new Color(255, 215, 60) : (tier == 1) ? new Color(32, 32, 36) : new Color(225, 195, 110);
        Color capDark = (tier == 10) ? new Color(180, 140, 20) : (tier == 1) ? new Color(18, 18, 20) : new Color(160, 130, 60);

        for (int x = 6; x <= 9; x++) {
            img.setRGB(x, 2, cap.getRGB());
            img.setRGB(x, 3, capDark.getRGB());
        }

        for (int y = 4; y <= 5; y++) {
            img.setRGB(6, y, glass.getRGB());
            img.setRGB(7, y, (tier == 1) ? new Color(26, 26, 30).getRGB() : pal[0].getRGB());
            img.setRGB(8, y, (tier == 1) ? new Color(18, 18, 20).getRGB() : pal[1].getRGB());
            img.setRGB(9, y, glass.getRGB());
        }

        for (int y = 6; y <= 14; y++) {
            for (int x = 4; x <= 11; x++) {
                boolean isEdge = (x == 4 || x == 11 || y == 14);
                boolean isCornerCut = (y == 6 && (x == 4 || x == 11)) || (y == 14 && (x == 4 || x == 11));

                if (isCornerCut) continue;

                if (isEdge) {
                    img.setRGB(x, y, glass.getRGB());
                } else {
                    if (y >= 7) {
                        if (x == 5 && y <= 9) {
                            img.setRGB(x, y, glassShine.getRGB());
                        } else if (tier == 1) {
                            if (y <= 9) img.setRGB(x, y, new Color(30, 30, 34).getRGB());
                            else if (y <= 12) img.setRGB(x, y, new Color(20, 20, 22).getRGB());
                            else img.setRGB(x, y, new Color(12, 12, 14).getRGB());
                        } else {
                            if (y <= 9) img.setRGB(x, y, pal[0].getRGB());
                            else if (y <= 12) img.setRGB(x, y, pal[1].getRGB());
                            else img.setRGB(x, y, pal[2].getRGB());
                        }
                    } else {
                        img.setRGB(x, y, glass.getRGB());
                    }
                }
            }
        }

        if (tier == 1) {
            img.setRGB(8, 12, new Color(210, 45, 12).getRGB());
            img.setRGB(7, 13, new Color(160, 25, 8).getRGB());
        } else if (tier == 7) {
            img.setRGB(8, 10, Color.WHITE.getRGB());
        } else if (tier == 8) {
            img.setRGB(7, 11, new Color(255, 140, 255).getRGB());
        } else if (tier == 10) {
            img.setRGB(7, 10, new Color(255, 220, 80).getRGB());
            img.setRGB(8, 11, new Color(255, 190, 40).getRGB());
        }

        return img;
    }
}
