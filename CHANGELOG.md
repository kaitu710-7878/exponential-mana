# Changelog / 変更履歴

All notable changes to this project will be documented in this file.
本プロジェクトのすべての注目すべき変更はこのファイルに記録されます。

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.16.1-beta] - 2026-11-06

### Fixed / 修正
- **EN**:
  - **Updated Infinite Mana Recipe to Match Final Design Specification & Screenshots**:
    - Restored the 9-slot layout for "Infinite Mana": 4 Origin Armor pieces at the 4 corners, Origin Sword at the center, surrounded by 4 Mana Roots (cross), leaving all other slots empty as designed.
    - Verified and confirmed full consistency for all Tier 10 endgame recipes (Mana Root, Origin Sword, Helmet, Chestplate, Leggings, Boots, and Infinite Mana) and progression recipes (Tier 3 Core, etc.) against reference layouts.
- **JA**:
  - **「無限のマナ」レシピの仕様書・リファレンス画像準拠への更新**:
    - 「無限のマナ」のレシピを、四隅に根源装備4点（ヘルメット、チェストプレート、レギンス、ブーツ）、中央に根源の剣、その上下左右にマナの根源×4を配置し、その他のスロットを空欄とする9スロット構成（仕様書・画像準拠）に更新しました。
    - エンドゲームレシピ全種（マナの根源、根源の剣、ヘルメット、チェストプレート、レギンス、ブーツ、無限のマナ）および階層進行レシピ（Tier3コア等）がリファレンス画像および企画書と完全に一致していることを確認・整合しました。

---

## [0.16.0-beta] - 2026-11-06

### Fixed / 修正
- **EN**:
  - **Corrected Progression Crafting Materials According to Specification**:
    - Fixed recipe materials for advancing to Tier 8, Tier 9, and Tier 10:
      - Tier 8 Mana/Core/Equipment (crafted on Tier 7 10x10 Table): uses **Obsidian** (center 2x2 for Mana, 6x6 for Core).
      - Tier 9 Mana/Core/Equipment (crafted on Tier 8 11x11 Table): uses **Emerald** (center 3x3 for Mana and Core).
      - Tier 10 Mana/Core/Equipment (crafted on Tier 9 12x12 Table): uses **Netherite Ingot** (center 4x4 for Mana and Core).
    - Verified strict conformance to the master design doc: `Coal(T1) -> Copper(T2) -> Redstone(T3) -> Glowstone(T4) -> Iron(T5) -> Gold(T6) -> Diamond(T7) -> Obsidian(T8) -> Emerald(T9) -> Netherite(T10)`.

- **JA**:
  - **仕様書に準拠した階層進行素材（マナ・コア等）の修正**:
    - Tier 8、Tier 9、Tier 10 への進行レシピにおけるバニラ要求素材のズレを完全修正：
      - Tier 8 マナ/コア/設備（Tier 7 10×10作業台）: 要求素材を **黒曜石（Obsidian）** に修正（マナ: 2×2、コア: 6×6）。
      - Tier 9 マナ/コア/設備（Tier 8 11×11作業台）: 要求素材を **エメラルド（Emerald）** に修正（マナ: 3×3、コア: 3×3）。
      - Tier 10 マナ/コア/設備（Tier 9 12×12作業台）: 要求素材を **ネザライトインゴット（Netherite Ingot）** に修正（マナ: 4×4、コア: 4×4）。
    - 企画書通りの正規進行 `石炭(T1) → 銅(T2) → 赤石(T3) → グロウストーン(T4) → 鉄(T5) → 金(T6) → ダイヤ(T7) → 黒曜石(T8) → エメラルド(T9) → ネザライト(T10)` に完全合致させました。

---

## [0.15.9-beta] - 2026-11-06

### Fixed / 修正
- **EN**:
  - **Fixed Mana Material Recipes (Tier 1-10)**:
    - Corrected center materials and symmetric square dimensions for Mana Material recipes:
      - Tier 8 Mana Material: Center 5x5 Obsidian on 11x11 grid with Tier 8 Mana border.
      - Tier 9 Mana Material: Center 4x4 Emerald on 12x12 grid with Tier 9 Mana border.
      - Tier 10 Mana Material: Center 5x5 Netherite Ingot on 13x13 grid with Tier 10 Mana border.
    - Verified all Tier 1..10 progression materials and center box dimensions.

- **JA**:
  - **マナ素材レシピの修正（Tier 1〜10）**:
    - JSON化に伴いズレが生じていたマナ素材の中央バニラ素材および正方形中心サイズを正確に修正：
      - Tier 8 マナ素材: 11×11作業台の中央5×5に **黒曜石（Obsidian）**、外枠にTier 8 マナ。
      - Tier 9 マナ素材: 12×12作業台の中央4×4に **エメラルド（Emerald）**、外枠にTier 9 マナ。
      - Tier 10 マナ素材: 13×13作業台の中央5×5に **ネザライトインゴット（Netherite Ingot）**、外枠にTier 10 マナ。
    - 全Tier（1〜10）のマナ素材が左右・上下対称の美しい正方形中心レシピであることを検証・修正完了。

---

## [0.15.8-beta] - 2026-11-06

### Added / 追加
- **EN**:
  - **Complete Data-Driven Recipe Architecture**:
    - Converted all Extended Crafting Table recipes across Tier 1 (4x4) to Tier 10 (13x13) to Minecraft's standard data-driven JSON system (`data/exponential_mana/recipes/extended_crafting/...`).
    - Implemented custom `RecipeType<ExtendedCraftingRecipe>` and `RecipeSerializer<ExtendedCraftingRecipe>`, allowing datapack authors and modpack creators to customize, add, or override extended crafting recipes easily via JSON.
  - **Performance Optimization**:
    - Introduced intelligent material and grid hashing caches in `ExtendedCraftingTableBlockEntity` and `ExtendedCraftingMenu`, virtually eliminating recipe scanning overhead during idle ticks and menu synchronization.

- **JA**:
  - **完全JSON駆動レシピシステムへの移行**:
    - Tier 1（4×4）〜 Tier 10（13×13）までのすべての拡張作業台レシピを、Minecraft標準のデータパックJSON方式（`data/exponential_mana/recipes/extended_crafting/...`）へ完全移行。
    - 専用の `RecipeType` および `RecipeSerializer` を実装し、データパックやMODパック製作者がJSONを通じて自由に拡張作業台のレシピを追加・変更・削除できる柔軟な設計を確立。
  - **コード全体の最適化・高速化**:
    - `ExtendedCraftingTableBlockEntity`（自動クラフト）および `ExtendedCraftingMenu`（手動クラフト）にハッシュベースのレシピ照合キャッシュを導入。インベントリやバッファに変更がない場合の毎tick・毎同期時の不要なレシピ走査負荷をゼロに削減。

---

## [0.15.7-beta] - 2026-11-06

### Changed / 変更
- **EN**:
  - **Origin Boots Recipe Update**:
    - Updated 13x13 recipe for Origin Boots (`origin_boots`) with Compressed Origin Alloy outline, Mana Root interior, and Quantum Mana (`quantum_mana`) as the center core between the boots, completing the unified set of pixel-art recipes for all Origin Equipment.

- **JA**:
  - **「根源の靴」レシピの刷新（新ドットアートレシピ）**:
    - 13×13「根源の靴（Origin Boots）」レシピを更新。外枠を圧縮された根源の合金、内部をマナの根源で構成し、両足の間の中央コアに **量子のマナ（Quantum Mana）** を配置。これにより、根源装備一式（剣＝叡智、兜＝恒星、脚甲＝海洋、胸当て＝深淵、靴＝量子）の統一ドットアートレシピが全て完成。

---

## [0.15.6-beta] - 2026-11-06

### Changed / 変更
- **EN**:
  - **BiggerStacks Compatibility**:
    - When the `biggerstacks` mod is present, compact stack number rendering on GUI slots (Extended Crafting Screen, Fairy Attractor Screen) and custom large stack tooltips now gracefully delegate to BiggerStacks, eliminating duplicate number overlaps.
  - **New Pixel-Art Recipes for Mana Root & Origin Equipment**:
    - **Mana Root (`mana_root`)**: Updated 13x13 recipe featuring a center cross of Compressed Origin Alloy, diagonals of Mana Wisdom, and the four quadrants filled with Abyssal, Quantum, Marine, and Star Mana.
    - **Origin Sword (`origin_sword`)**: Updated 13x13 recipe depicting a diagonal broadsword forged of Mana Root with a Mana Wisdom gem inlaid in the crossguard.
    - **Origin Helmet (`origin_helmet`)**: Updated 13x13 recipe with Compressed Origin Alloy outline, Mana Root padding, and a Star Mana crest at the forehead.
    - **Origin Leggings (`origin_leggings`)**: Updated 13x13 recipe with Compressed Origin Alloy outline, Mana Root fabric, and a Marine Mana gem at the belt center.
    - **Origin Chestplate (`origin_chestplate`)**: Updated 13x13 recipe with Compressed Origin Alloy armor plating, Mana Root interior, and an Abyssal Mana heart core.

- **JA**:
  - **BiggerStacks 互換性の向上**:
    - `biggerstacks` MOD導入環境において、拡張作業台および妖精誘引機画面での独自スタック数値描画（K/M/B）を停止し、アイテムツールチップのスタック詳細表示も含めて BiggerStacks 側の表示機能に一任。数値が二重に重なって表示される問題を解消。
  - **マナの根源および根源装備レシピの刷新（新ドットアートレシピ）**:
    - **マナの根源（Mana Root）**: 中央十字に圧縮された根源の合金、X字対角線にマナの叡智、上下左右の4象限にそれぞれ深淵・恒星・量子・海洋のマナを配した新レシピへ更新。
    - **根源の剣（Origin Sword）**: マナの根源で精緻に描かれた斜めの大剣デザイン、鍔（つば）の中心にマナの叡智をはめ込んだ新レシピへ更新。
    - **根源の兜（Origin Helmet）**: 外枠を圧縮された根源の合金、内部をマナの根源、額の中心に恒星のマナを配した新レシピへ更新。
    - **根源の脚甲（Origin Leggings）**: 外枠を圧縮された根源の合金、内部をマナの根源、ベルトの中心に海洋のマナを配した新レシピへ更新。
    - **根源の胸当て（Origin Chestplate）**: 外枠を圧縮された根源の合金、内部をマナの根源、胸の核に深淵のマナを配した新レシピへ更新。

---

## [0.15.5-beta] - 2026-11-06

### Changed / 変更
- **EN**:
  - **Infinite Mana Recipe Ingredient Update (Mana Root)**:
    - Updated the 13x13 "Infinite Mana" recipe: all 164 filler slots now require **Mana Root** (`mana_root`) instead of Tier 10 Mana, elevating the recipe to true endgame difficulty fitting the 4 Origin Armors and Origin Sword.
  - **Enhanced JEI Margin Clearance**:
    - Increased minimum recipe area width to `280px` and minimum height to `130px` across all tiers, ensuring ample clearance between the output slot, the recipe transfer ("+") button, and JEI's bottom-right bookmark and favorite widgets even on smaller grids (Tier 1-3).

- **JA**:
  - **「無限のマナ」レシピの要求素材を「マナの根源」に変更**:
    - 13×13「無限のマナ」レシピの空きスロット（164マス）の要求素材を Tier 10 マナから **マナの根源（Mana Root）** に変更。4隅の根源防具4種および中央の根源の剣に見合う最高峰のエンドゲームレシピに調整。
  - **JEI拡張作業台カテゴリの余白・クリアランス強化**:
    - レシピ領域の最小幅を `280px`、最小高さを `130px` に拡大。小規模なTier 1〜3（4×4, 5×5等）でも、成果物スロットやレシピ転送（"+"）ボタンがJEI右下の星ボタン（お気に入り）・栞ボタン（ブックマーク）と絶対に干渉・重複しないよう余白を確保。

---

## [0.15.4-beta] - 2026-11-06

### Added / 追加
- **EN**:
  - **JEI Transfer & Output Button Layout Overhaul**:
    - Expanded width (`Math.max(260, gridPixels + 140)`) and height to provide extensive margins.
    - Repositioned recipe transfer button ("+") to the right side of the output slot (`outX + 32, outY + 4`), completely clearing JEI's bottom-right bookmark (ribbon) and favorite (star) widgets.
  - **Correct Inverted Scaling for Compressed Mana**:
    - Re-aligned compressed mana requirements according to proper inverse tier scaling (higher tiers require smaller compression grids, lower tiers require larger compression grids).
    - Tier 2 Compressed Mana is now crafted on Tier 9 Table (12x12), and Tier 8 Compressed Mana is crafted on Tier 2 Table (5x5).
    - Removed duplicate 13x13 recipe for Tier 10 Compressed Mana from Extended Crafting Tables (strictly unified into the vanilla 3x3 crafting table recipe).
  - **Symmetric Centered Square Recipes for Mana Materials**:
    - Corrected grid centering for Netherite, Obsidian, Gold, Glowstone, and Coal Mana Material recipes.
    - Odd-sized grids use odd-sized center boxes (1x1, 3x3, 5x5) and even-sized grids use even-sized center boxes (2x2, 4x4), guaranteeing perfect bilateral and radial symmetry.
  - **Tier 10 Mana Fill for Infinite Mana Recipe**:
    - Filled all previously empty background slots in the 13x13 "Infinite Mana" recipe with Tier 10 Mana.
  - **Applied Distinct Textures for Mana Knowledge & Mana Wisdom**:
    - Officially updated game assets with dedicated icons: Mana Knowledge (Arcane Tome) and Mana Wisdom (Transcendental Crystalline Orb).

- **JA**:
  - **拡張作業台JEI画面のボタン・成果物配置の抜本的改善**:
    - 横幅（`gridPixels + 140px`）および高さを拡大し、右下に十分なクリアランスを確保。
    - レシピ転送ボタン（"+"）を出力スロットの右横（`outX + 32, outY + 4`）に逃がすことで、JEI右下に表示されるブックマーク（栞）やお気に入り（星）ボタンとの重なり・干渉を完全解消。
  - **圧縮マナの反比例スケール適正化**:
    - 高Tierマナほど小規模作業台で圧縮し、低Tierマナほど大規模作業台で圧縮する仕様を正確に適用。
    - Tier 2 圧縮マナは Tier 9 拡張作業台 (12×12)、Tier 8 圧縮マナは Tier 2 拡張作業台 (5×5) での圧縮に再設定。
    - Tier 10 圧縮マナの拡張作業台（13×13）重複レシピを撤廃し、バニラ作業台（3×3）の1系統に一本化。
  - **マナ素材レシピの中心ズレ解消・正方形対称化**:
    - ネザライト、黒曜石、金、グロウストーン、石炭などのマナ素材レシピにおいて、中心ボックスの偶数・奇数のズレを修正。
    - 偶数グリッドには偶数の中心正方形（2×2, 4×4）、奇数グリッドには奇数の中心正方形（1×1, 3×3, 5×5）を割り当て、上下左右の余白が完全一致する対称正方形レシピに統一。
  - **無限のマナレシピへのTier 10マナ敷き詰め**:
    - 13×13「無限のマナ」レシピの空きマス全枠に Tier 10 マナを敷き詰めるよう改修。
  - **マナの知識・マナの叡智のテクスチャ確定登録**:
    - マナの知識（全Tierの知識を記した魔導書）およびマナの叡智（169個の知識が結晶化した至高の宝珠）の個別テクスチャを正式アセットへ反映。

---

## [0.15.3-beta] - 2026-11-06

### Added / 追加
- **EN**:
  - **Separation of Manual vs Automated Crafting Pipelines**:
    - **Manual Crafting (Grid)**: Crafting on the table grid now operates purely as manual crafting. Items transferred or placed into the grid (e.g. from JEI) are never consumed automatically on placement; instead, the result is previewed in the main craft result slot and crafted on take/shift-click.
    - **Automated Crafting (Material Buffer)**: Automated continuous crafting is strictly confined to the Material Buffer (18 slots). Finished items output exclusively into the Auto-Output Buffer (9 slots) without cluttering the manual crafting result slot.
  - **Repositioned Tier 1-10 Compression Recipes**:
    - Fixed an issue where Tier 2-8 compression recipes (Compressed Mana, Mana Materials, and Compressed Mana Materials) were improperly registered to Tier 10 tables. Now accurately configured to their native tier tables (Tier 1: 4x4, Tier 2: 5x5, ..., Tier 8: 11x11, Tier 9: 12x12, Tier 10: 13x13).
  - **Shapeless Transformation for Full-Grid Compression & Mana Knowledge**:
    - All uniform full-grid compression recipes (Compressed Mana, Compressed Mana Materials, Compressed Minerals, Compressed Origin Alloy, Mana Wisdom, and Root Manas) are now Shapeless recipes requiring the exact quantity anywhere across the grid.
    - Mana Knowledge is now a Shapeless 13x13 recipe (requiring all 169 ingredients across all tiers of mana, ore, and catalysts) that can be arranged freely.
  - **JEI Layout Deconfliction**:
    - Expanded layout width and repositioned output slot/transfer button in `ExtendedCraftingJeiCategory`, eliminating overlaps with JEI's top-right bookmark, favorite, and config widgets.
  - **Texture Differentiation Proposal for Mana Knowledge & Mana Wisdom**:
    - Generated distinctive designs for Mana Knowledge (Arcane Tome) and Mana Wisdom (Crystalline Radiance Orb).

- **JA**:
  - **手動クラフトと自動クラフトの2系統完全分離**:
    - **手動クラフト（作業台グリッド）**: JEIからの転送や手動配置時に素材が勝手に自動クラフトで消費されてしまう事故を防止。矢印の先の成果物スロットに完成品がプレビューされ、クリックまたはシフトクリックした時のみ素材を消費してクラフトする方式へ回帰。
    - **自動クラフト（素材置き場）**: 自動クラフトの対象を素材置き場（下段18スロット）のみに完全限定。自動クラフトされた成果物は自動出力バッファ（9スロット）へ直接出力され、手動クラフト側の成果物スロットを一切占拠しない構造に改修。
  - **Tier 2〜8の圧縮レシピの適正Tier作業台への再配置**:
    - 圧縮マナ、マナ素材、圧縮マナ素材が誤って全てTier 10作業台で行われていた不具合を修正。各対応Tier（Tier 1: 4x4、Tier 2: 5x5、...、Tier 8: 11x11、Tier 9: 12x12、Tier 10: 13x13）の拡張作業台へ適正配置。
  - **全マス敷き詰め圧縮およびマナの知識の不定形（Shapeless）化**:
    - すべてのマスに同じアイテムを配置して圧縮する形式（圧縮マナ、圧縮マナ素材、圧縮鉱石、圧縮オリジン合金、マナの叡智、4つの根源マナ等）を全て不定形レシピ化。
    - マナの知識（13x13全169マス要求）も配置不問の不定形レシピ化（全Tierのマナ・鉱石・触媒の合計169個要求を維持）。
  - **JEI画面におけるブックマーク系UIとの重なり解消**:
    - `ExtendedCraftingJeiCategory` の横幅および成果物スロット・転送ボタンの位置を調整し、JEIの右上に表示されるブックマーク・お気に入り・設定ウィジェットとの重なりを完全解消。
  - **マナの知識・マナの叡智のテクスチャ差別化案の提示**:
    - 重複していたテクスチャを解消し、マナの知識（全Tierの叡智を記した魔導書）とマナの叡智（169個の知識が結晶化した至高の宝珠）の差別化デザインを作成・提示。

---

## [0.15.2-beta] - 2026-11-05

### Added / 追加
- **EN**:
  - **Six-Directional Face Textures (Neon Cyan Without Black Background)**:
    - Implemented clean directional face textures (N, S, E, W, U, D) with high-visibility neon cyan pixel badges drawn directly onto the block textures without black bounding boxes.
    - Fully applied across all 34 side-configurable machine blocks: Extended Crafting Tables (Tier 1-10), Fairy Blocks & Compressed Fairies (Tier 0-10), Fairy Attractor & Compressed Fairy Attractors (Tier 0-10), Batch Ore Breaker, and Thawer.
    - Updated all corresponding block model JSON definitions to map respective directional textures to absolute world orientations.
  - **Overhauled Extended Crafting Table Auto-Crafting Engine**:
    - Automatic recipe discovery: Placing raw ingredients into the Material Buffer (or grid) now automatically detects matching tiered recipes without requiring a pre-laid grid pattern.
    - Integrated Result Slot: The primary craft result slot in the UI is now directly bound to the BlockEntity's internal item handler (`resultSlotIndex`), persisting across GUI closing/reopening and continuous background ticks.
    - Direct Result Slot Yield: Auto-crafted yields are immediately deposited into the UI Result Slot and overflow into the 9 auto-output buffer slots, both supporting `Integer.MAX_VALUE` capacity.
    - Full Auto-Eject integration: Automatically ejects finished products from both the UI result slot and auto-output slots to adjacent inventories based on side configuration.

- **JA**:
  - **全サイド制御対応ブロックへの背景黒塗りなし方角テクスチャ適用**:
    - 黒色の背景ボックスを排除し、視認性の高いネオンシアン色のピクセル文字（N, S, E, W, U, D）をベーステクスチャ各面に直接描画したクリーンな方角別テクスチャを全適用。
    - 対象全34ブロック（拡張作業台 Tier 1〜10、妖精ブロックおよび圧縮妖精 Tier 0〜10、妖精誘引機および圧縮妖精誘引機 Tier 0〜10、一括破壊機、解凍機）のテクスチャ（計204面分）およびブロックモデルJSON定義を完備。
  - **拡張作業台の自動クラフト機能の抜本的刷新**:
    - 自動レシピ検出: 型紙をグリッドに配置しなくても、素材置場（Material Buffer）またはインベントリに素材を投入するだけで作れるレシピを自動判別しクラフトを開始。
    - 成果物欄のBlockEntity統合: UI上の成果物スロットを一時的なコンテナからBlockEntityの内部スロット（`resultSlotIndex`）に直接統合。UIの開閉やつけっぱなしに関わらず `serverTick` で常に成果物が蓄積・保持される仕様に改修。
    - 成果物欄への直接出力: 自動クラフトされた成果物はUI上の成果物欄（Result Slot）に即座に出現し、溢れた分は9枠の自動出力バッファへ格納（どちらも1枠あたり `Integer.MAX_VALUE` 個対応）。
    - 自動搬出（Auto Eject）連携: サイド設定に従い、成果物欄および自動出力バッファの両方から隣接チェスト等の外部インベントリへ完成品を自動搬出。

---

## [0.15.1-beta] - 2026-11-05

### Added / 追加
- **EN**:
  - **JEI Favorites & Bookmarks Full Compatibility**:
    - Implemented unique recipe registry names (`ResourceLocation id`) across all Extended Crafting recipes (Tier 1 to 10).
    - Overrode `IRecipeCategory.getRegistryName()` in `ExtendedCraftingJeiCategory` to enable full support for recipe bookmarks, favorites ('A' key), and recipe ID copying in JEI / JEI Unofficial.
- **JA**:
  - **JEIのお気に入り・ブックマーク機能の完全対応**:
    - 拡張作業台（Tier 1〜10）の全レシピに一意の識別子（`ResourceLocation id`）を実装。
    - `ExtendedCraftingJeiCategory` にて `IRecipeCategory.getRegistryName()` をオーバーライドし、JEI / JEI Unofficial におけるレシピのブックマーク（お気に入り登録・'A'キー）、レシピIDのコピー、ブックマークからの正確なレシピ復元に対応。

---

## [0.15.0-beta] - 2026-11-04

### Added / 追加
- **EN**:
  - **Compressed Fairy Attractors (Tier 1 - 10)**:
    - Added 10 tiers of Compressed Fairy Attractors craftable via 3x3 crafting of the previous tier (9 attractors).
    - Production scales exponentially up to $9^{10}$ fairies per cycle (every 5 minutes / 6000 ticks).
    - Features an expanded 8x8 inventory (64 slots) with `Integer.MAX_VALUE` capacity per slot and automatic ejection.
    - Added dedicated evolving block models and textures for all 10 compression tiers.
  - **13x13 Mana Knowledge Recipe**:
    - Replaced individual recipes with a unified 13x13 crafting recipe on Tier 10 table requiring all tiers (0 to 10) of Mana, Ore, and Catalysts (Tier 0 uses Fairy Block).
- **JA**:
  - **圧縮妖精誘引機 (Tier 1〜10)**:
    - 前Tierの誘引機9個による3×3クラフトで作成可能な、圧縮妖精誘引機（Tier 1〜10）を追加。
    - クールダウン5分（6000 ticks）据え置きのまま、生産量が最大 $9^{10}$ 倍まで指数関数的に増加。
    - 8×8スロット（全64スロット）、1スロットあたり `Integer.MAX_VALUE` 個の巨大インベントリおよび自動搬出（Auto Eject）機能を実装。
    - 圧縮段階に応じた専用の外観モデル・テクスチャ（Tier 1〜10）を追加。
  - **マナの知識の13×13レシピ化**:
    - 旧来の個別クラフトレシピを削除し、Tier10拡張作業台（13×13）にて全Tier（0〜10）のマナ・鉱石・触媒（Tier0枠は妖精ブロック）を一堂に要求する新レシピへ刷新。

### Fixed / 修正
- **EN**:
  - **Extended Crafting Table Auto-Crafting Logic**:
    - Fixed an issue where recipes would stall due to tag mismatch (`null` vs empty `{}`) in `ItemStackKey`.
    - Extended auto-crafting material consumption: items placed in the crafting grid above count 1 can now also be used as crafting ingredients while preserving template items.
  - **Compressed Fairy Attractor Tooltip Format**:
    - Fixed unparsed `%d` in tooltip by passing properly formatted count string to translation key.
- **JA**:
  - **拡張作業台の自動クラフト機能の修正**:
    - `ItemStackKey` のタグ一致判定において空タグ `{}` と `null` の差異によりクラフトが停止していた不具合を解消。
    - 素材バッファ（18スロット）に加えて、クラフトグリッド上にスタックされた余剰アイテム（2個目以降）もクラフト素材として自動消費できるよう改善（型紙としての1個は保護）。
  - **圧縮妖精誘引機の説明文フォーマット修正**:
    - ツールチップ内の `%d` がそのまま表示されていた不具合を修正し、桁区切りされた正確な誘引数を表示するように修正。

### Changed / 変更
- **EN**:
  - **Fairy Residue Yield**:
    - Crafting a Fairy Block now yields 64 Fairy Residues per craft while retaining the Fairy Block.
  - **Comprehensive Texture Harmonization**:
    - Overhauled mana textures (Tier 0, 1, 7, 8, 10) and distinct sacred medallion Mana Symbols (Tier 0 to 10).
    - Harmonized Cores (Tier 1 to 10), Fairy Catalysts (Tier 1 to 10), and Shards to match the new Mana motifs and colors.
    - Confirmed and applied textures for Activated Conduit and Compressed Tier 0-10 Mana.
- **JA**:
  - **妖精の残滓のクラフト獲得量増加**:
    - 妖精ブロックの単体クラフト時に獲得できる妖精の残滓の数量を 1個 から 64個 に変更（妖精ブロックは消費されず保持）。
  - **マナモチーフテクスチャの包括的調和**:
    - マナ球体（T0淡シアン, T1熾火赤, T7ダイヤ十字, T8黒曜石紫, T10ネザライト金）および神聖メダリオン象徴の視認性改善。
    - コア（Tier 1〜10）、妖精活発化触媒（Tier 1〜10）、鉱石・触媒欠片のデザイン・色彩を新しいマナモチーフへ完全同期。
    - 活性化コンジットおよび圧縮Tier0〜10マナの正式テクスチャをアセットへ適用。

---

## [0.14.0-beta] - 2026-11-04

### Changed / 変更
- **EN**:
  - **Full Alignment with Design Specification (Part 2: Detailed Recipes & Tier Progression)**:
    - **Vanilla Material Sequence Synchronized**:
      Updated progression to `Coal (T1) -> Copper (T2) -> Redstone (T3) -> Glowstone (T4) -> Iron (T5) -> Gold (T6) -> Diamond (T7) -> Obsidian (T8) -> Emerald (T9) -> Netherite (T10)`.
    - **Extended Crafting Table Recipe Tier Assignment**:
      - Tier 1 Table (4x4) now crafts Tier 2 items (Copper).
      - Tier 2 Table (5x5) crafts Tier 3 items (Redstone).
      - Tier 3 Table (6x6) crafts Tier 4 items (Glowstone).
      - Tier 4 Table (7x7) crafts Tier 5 items (Iron).
      - Tier 5 Table (8x8) crafts Tier 6 items (Gold).
      - Tier 6 Table (9x9) crafts Tier 7 items (Diamond).
      - Tier 7 Table (10x10) crafts Tier 8 items (Obsidian).
      - Tier 8 Table (11x11) crafts Tier 9 items (Emerald).
      - Tier 9 Table (12x12) crafts Tier 10 items (Netherite).
      - Tier 10 Table (13x13) exclusively crafts all End-Game recipes.
    - **Tier 0 Mana Ore Mining Drop**:
      Directly drops Tier 0 Mana only without dropping inactive ore blocks, matching the design document.
    - **Complete End-Game Recipe Implementation**:
      - Added recipes for 11 Mana Symbols (`mana_symbol_tier0` to `tier10`).
      - Added Compressed Tier 0-10 Mana (`compressed_tier0_mana` to `tier10_mana`).
      - Added Pure Mana (165 compressed mana), Pure Mana Ore, and Pure Fairy Catalyst.
      - Added 10 Tier Mana Materials, 10 Compressed Mana Materials, and Compressed Minerals (Lapis, Amethyst, Quartz).
      - Added Origin Alloy and Compressed Origin Alloy.
      - Added Mana Knowledge (from any tier mana+ore+catalyst) and Mana Wisdom.
      - Added Activated Conduit (Conduit + 8 Sea Lanterns) and 4 Root Manas (Star, Quantum, Abyssal, Marine).
      - Added Mana Root and Origin Equipment suite.
      - Added Infinite Mana 13x13 recipe and duplicator function.
- **JA**:
  - **企画書（第2部：詳細レシピ・階層進行まとめ）への完全準拠**:
    - **要求バニラ素材順序の完全同期**:
      素材進行を `石炭(T1) → 銅(T2) → 赤石(T3) → グロウストーン(T4) → 鉄(T5) → 金(T6) → ダイヤ(T7) → 黒曜石(T8) → エメラルド(T9) → ネザライト(T10)` に完全改訂。
    - **各作業台サイズとレシピのTier対応の正規化**:
      - Tier1拡張作業台(4×4): Tier2アイテム群（銅）をクラフト。
      - Tier2拡張作業台(5×5): Tier3アイテム群（赤石）をクラフト。
      - Tier3拡張作業台(6×6): Tier4アイテム群（グロウストーン）をクラフト。
      - Tier4拡張作業台(7×7): Tier5アイテム群（鉄）をクラフト。
      - Tier5拡張作業台(8×8): Tier6アイテム群（金）をクラフト。
      - Tier6拡張作業台(9×9): Tier7アイテム群（ダイヤ）をクラフト。
      - Tier7拡張作業台(10×10): Tier8アイテム群（黒曜石）をクラフト。
      - Tier8拡張作業台(11×11): Tier9アイテム群（エメラルド）をクラフト。
      - Tier9拡張作業台(12×12): Tier10アイテム群（ネザライト）をクラフト。
      - Tier10拡張作業台(13×13): エンドゲーム（全169スロット）の全レシピをクラフト。
    - **Tier0マナ鉱石の採掘時ドロップ修正**:
      企画書通り、採掘時に非活性鉱石をドロップせず「Tier0マナ」のみをドロップするように修正。
    - **エンドゲーム全レシピの実装**:
      - 11種のマナの象徴（T0〜T10マナ各169個）。
      - 11種の圧縮Tier0〜10マナ（各Tierの象徴を指定数要求）。
      - 無垢のマナ（圧縮Tier0〜10マナ各15個計165個）、無垢のマナ鉱石、無垢の妖精活発化触媒。
      - 10種のマナ素材、10種の圧縮マナ素材、3種の圧縮鉱石（ラピス、アメジスト、クォーツ）。
      - オリジン合金（圧縮素材計169個）および圧縮オリジン合金。
      - マナの知識（各Tierマナ＋鉱石＋触媒）およびマナの叡智。
      - 活性化コンジット（コンジット＋シーランタン8個）および4つの根源マナ（星、量子、深淵、海洋）。
      - マナの根源および根源装備一式。
      - 無限のマナ（13×13レシピおよびバニラ作業台でのアイテム64倍増殖）。

### Added / 追加
- **EN**:
  - Registered `compressed_tier0_mana` through `compressed_tier10_mana` items.
  - Registered `activated_conduit` item and recipe.
  - Added Shapeless recipes for `mana_knowledge` across all tiers (0 to 10).
- **JA**:
  - `compressed_tier0_mana` 〜 `compressed_tier10_mana`（圧縮Tier0〜10マナ）アイテムを追加登録。
  - `activated_conduit`（活性化コンジット）アイテムおよびレシピを追加登録。
  - 全Tier（0〜10）からの `mana_knowledge`（マナの知識）不定形クラフトレシピを追加。

---

## [0.13.3-beta] - 2026-11-04

### Added / 追加
- **EN**:
  - **9-Slot Auto-Craft Output Buffer**:
    - Added 9 dedicated output slots (holding up to `Integer.MAX_VALUE` stacks each) to all Extended Crafting Tables for storing automated crafting yields.
    - Players can now manually take automatically crafted items directly from the GUI without requiring external pipe extraction.
  - **Side Direction Block Textures (UDNSWE)**:
    - Designed 16x16 directional pixel art letter badges on the bottom-right corner of all 6 faces (Up, Down, North, South, West, East) for Tier 1 through Tier 10 Extended Crafting Tables.
- **JA**:
  - **自動クラフト成果物用9スロット（Int_MAX対応）の追加**:
    - 全拡張作業台に、自動クラフトされた完成品を一時保持する専用スロット（9スロット、1スロットあたりInt_MAXスタック保持可能）を追加。
    - パイプ搬出を行わなくても、プレイヤーがGUIから直接自動クラフト成果物を取り出せるようになりました。
  - **拡張作業台の各面UDNSWE方角テクスチャ**:
    - Tier1〜Tier10の全拡張作業台の全6面（上面:U, 下面:D, 北面:N, 南面:S, 西面:W, 東面:E）の右下に方角頭文字を記載した16×16バニラ基準テクスチャを作成。

### Fixed / 修正
- **EN**:
  - **Resolved Player Ticking Kick Exception**:
    - Fixed slot indexing mismatch between `ExtendedCraftingTableBlockEntity` and `ExtendedCraftingMenu` that caused Neruina to kick players due to ticking exceptions.
  - **Manual Crafting Result Container Separation**:
    - Decoupled manual crafting output into a dedicated vanilla-style `ResultContainer`, completely fixing the bug where matched crafting output vanished or could not be taken.
  - **GUI Slot Layout & Overflow Fix**:
    - Fixed player inventory and hotbar overflowing outside the GUI container background panel.
    - Completely resolved the misplaced 26x26 big frame highlight and slot alignment discrepancies across all table tiers.
  - **JEI Display & Transfer Optimization**:
    - Cleaned up JEI recipe view so slot highlights, output slots, and tooltips do not crowd or overlap.
    - Adjusted recipe transfer handler indices to correctly target the player inventory.
- **JA**:
  - **プレイヤーのTick例外によるキック不具合の解消**:
    - `ExtendedCraftingTableBlockEntity` と `ExtendedCraftingMenu` 間のスロットインデックス不整合を解消し、Neruina によるキック（接続を維持できません）を完全に修正。
  - **手動クラフト成果物の消失・取得不可バグの根絶**:
    - 手動クラフトの成果物管理を独立した `ResultContainer` に分離し、レシピが一致しているのに成果物が消えてクラフトできない問題を根本解決。
  - **GUI枠はみ出し・スロットズレの完全修正**:
    - Tier1〜10においてプレイヤーインベントリやホットバーが背景パネルからはみ出ていた問題、およびグリッド上に成果物大枠が誤描画されていた現象を解消。
  - **JEIレイアウトおよびレシピ転送の最適化**:
    - JEIレシピ画面でのスロット枠・成果物・ツールチップの重なりを解消し、転送「＋」ボタンのインベントリ対象インデックスを修正。

---

## [0.13.2-beta] - 2026-11-04

### Fixed / 修正
- **EN**:
  - **Fixed Crafting Grid Slot Placement**:
    - Resolved critical bug where `isItemValid` blocked players and JEI Recipe Transfer from placing items into Extended Crafting Table grid slots (`slot < bufferSlotStart`).
    - Now players and JEI "+" button can smoothly place recipes into all grid slots (4x4 up to 13x13).
  - **Enhanced Recipe Matching**:
    - Made recipe matching lenient against NBT variations for standard materials (ores, ingots, tiered mana).
    - Automatically checks and refreshes recipe preview output when opening the Extended Crafting Table screen.
- **JA**:
  - **拡張作業台スロットへのアイテム配置不可バグの修正**:
    - `isItemValid` がグリッドスロット（`slot < bufferSlotStart`）への配置を拒否していた不具合を修正。手動およびJEIの「＋」ボタンによるレシピ自動配置が正常に機能するようになりました。
  - **レシピ照合の安定化**:
    - 通常の素材アイテム（鉱石、インゴット、各Tierマナ）において不要なNBT差分でレシピが弾かれないようマッチング処理を強化。
    - 作業台GUIを開いた際にも即座にレシピ照合を行って成果物欄を更新するよう改善。

---

## [0.13.1-beta] - 2026-11-04

### Changed / 変更
- **EN**:
  - **Mekanism Formulaic Assemblicator-style Batch Crafting**:
    - Upgraded Extended Crafting Table auto-crafting from 1 craft/tick to **unlimited batch crafting per tick**: As long as materials exist in the Material Buffer, it will craft as many items as possible in a single tick (e.g. 10x materials = 10 crafts in 1t).
    - Unlocked `Integer.MAX_VALUE` stack limit for Material Buffer slots in the GUI and shift-click quick move logic.
  - **UI Alignment**:
    - Relocated the Side Configuration widget (U, D, N, S, W, E, EJ) inside the bottom-right corner of the Extended Crafting Table screen.
    - Added compact stack counter overlay (`1.5K`, `20M`, `2.1B`) for buffer/output slots holding $\ge 1,000$ items.
  - **Terminology**:
    - Updated references to Mekanism's machine name to Formulaic Assemblicator (定式組立機).
- **JA**:
  - **定式組立機（Formulaic Assemblicator）仕様の全量一括クラフト**:
    - 拡張作業台の自動クラフトを「1tにつき保持しているアイテムの限り全量クラフト」へ強化。素材インベントリ内に10回分の素材があれば1tで10回分、1億回分あれば1tで1億回分を一括でクラフトして出力します。
    - GUI操作およびシフトクリック時において、自動クラフト用素材置きスロット（Material Buffer）のスタック上限を `Integer.MAX_VALUE` に完全引き上げ。
  - **UI配置の改善**:
    - 拡張作業台の面設定ウィジェット（UDNSWEおよび自動搬出トグル）を、作業台テクスチャ内の右下隅（プレイヤーインベントリ右横）に配置。
    - 素材バッファや出力スロットで1,000個以上のアイテムがある場合に「1.5K」「20M」「2.1B」と描画するコンパクトスタック表示を実装。
  - **用語統一**:
    - Mekanismの機械名称の表記を「定式組立機（Formulaic Assemblicator）」に統一。

---

## [0.13.0-beta] - 2026-11-03

### Added / 追加
- **EN**:
  - **Mekanism-style Side Configuration**:
    - Added 6-face side configuration (Disabled: Grey, Input: Blue, Output: Yellow, Both: Purple) to Fairy Blocks, Batch Ore Breaker, Thawer, and Extended Crafting Tables.
    - Added Auto-Eject mechanism: Automatically pushes output items into adjacent inventories on Output/Both faces every tick.
    - Added interactive GUI widget with 6-side buttons and Auto-Eject toggle, fully synced via Forge network packets.
  - **Mekanism Formulaic Assemblicator-style Automation for Extended Crafting Tables**:
    - Added 18-slot Material Buffer (holding up to Int_MAX items per slot) for automated material piping and insertion.
    - Implemented high-speed 1 craft / 1t automated crafting: Automatically consumes matching materials from the Material Buffer every tick and outputs crafted results.
  - **Extended Crafting Recipe Matching Engine**:
    - Implemented real-time in-game recipe checking for all Tier 1 (4x4) through Tier 10 (13x13) crafting tables.
    - Seamlessly supports manual crafting and JEI "+" recipe transfers, instantly displaying crafted results in the output slot.
- **JA**:
  - **Mekanism風の面設定（Side Configuration）機能**:
    - 妖精ブロック、鉱石一括破壊機、解凍機、拡張作業台の全機械に6方向（上・下・北・南・西・東）の面設定（遮断:灰 / 搬入:青 / 搬出:黄 / 搬入出:紫）を追加。
    - 自動搬出（Auto-Eject）機構を追加：搬出面・搬入出面に隣接するチェストやパイプへ、毎tick完成品・マナを自動でプッシュ排出。
    - 各GUIの右側に6面切り替えボタンと自動搬出ON/OFFトグルを備えた直感的なサイド設定ウィジェットを追加。ネットワークパケットで即座に同期。
  - **拡張作業台の自動化（Mekanism 分子組立機仕様・1クラフト/1t）**:
    - 外部からのパイプ搬入を受け入れる18スロットの「素材インベントリ（Material Buffer、各スロットInt_MAXスタック対応）」を新設。
    - グリッドのレシピに従い、素材インベントリ内のアイテムを消費して毎tick（1クラフト/1t）自動でクラフトし出力スロットへ送る超高速自動クラフト機構を実装。
  - **拡張作業台のレシピ照合エンジンの実装**:
    - Tier 1（4×4）〜 Tier 10（13×13）の全拡張作業台において、グリッド配置のリアルタイムレシピ照合・成果物生成ロジックを実装。
    - 手動クラフトおよびJEIの「＋」ボタンによるレシピ配置時に、成果物欄に即座にアイテムが表示され正常にクラフト可能に。

### Fixed / 修正
- **EN**:
  - Fixed JEI Recipe Transfer ("+") button overlapping the recipe grid by moving it cleanly below the output slot.
  - Fixed Extended Crafting GUI layout clipping and slot displacement by properly initializing screen boundaries and anchoring all rendering to leftPos/topPos.
  - Fixed player inventory and hotbar offset issues across Tier 1 through Tier 10 table screens.
- **JA**:
  - JEIのレシピ転送（「＋」）ボタンがグリッド枠内に重なって表示されていた問題を、出力スロット直下の安全な位置へ移動して完全解消。
  - 拡張作業台GUIでウィンドウ背景の高さが途中で切れたり、スロットやホットバーが外側にはみ出ていた描画バグを修正。`init()` で正しく寸法を再計算し、`leftPos`/`topPos` に完全追従。

---

## [0.12.1-alpha] - 2026-11-03

### Fixed / 修正
- **EN**:
  - Clarified and aligned Batch Ore Breaker behavior with the mod specification:
    - Ores are **not consumed**! The machine virtually manages the 5-minute inactive-to-active cooldown and mines them periodically, extracting mana into Batch Compressed Mana.
    - Added instant Batch Inactivation: Inserting active mana ores immediately converts them into inactive ores while granting initial mining mana.
    - Added live cycle countdown (5 minutes) in the UI.
- **JA**:
  - 鉱石一括破壊機の仕様を企画書通りに完全適正化：
    - **鉱石は消費されません**。10×10インベントリ内の非活性鉱石を仮想管理し、5分ごとに自動で活性化＆一括採掘を行ってマナを一括圧縮マナとして出力スロットへ抽出します。
    - 活性状態の通常マナ鉱石を投入した際、即座に採掘マナを排出しつつ「非活性マナ鉱石」へと一括変換する「一括非活性化」機能を実装。
    - UI上に仮想サイクルの残り時間（5分カウントダウン）をリアルタイム表示。

---

## [0.12.0-alpha] - 2026-11-03

### Fixed / 修正
- **EN**:
  - Overhauled JEI Extended Crafting categories into 10 dedicated tier categories (Tier 1 4x4 up to Tier 10 13x13) matching Extended Crafting Reforked. Standardized on 18x18 slot pitch with background frames to completely eliminate crowded and overlapping icons.
  - Implemented JEI Recipe Transfer Handler support ("+" button) on Extended Crafting Tables, allowing one-click auto-filling of recipes from player inventory.
  - Added dedicated output slot to the Batch Ore Breaker UI for packing and extracting Batch Compressed Mana without dismantling the machine.
  - Fixed Fairy catalyst quota logic: 1 cycle total quota = $9^{\text{compressionTier}}$. Catalysts consume their counts from the quota to produce tiered mana, and any remaining quota fills with Tier 0 mana. Multiple catalyst types cleanly share the single cycle quota without duplicate multi-cycle explosions.
  - Synchronized Fairy Block countdown timer to client via `ContainerData` so the UI seconds counter updates smoothly in real time.
- **JA**:
  - JEIの拡張クラフトを Extended Crafting Reforked 準拠の10段階専用カテゴリ（Tier 1 4×4 〜 Tier 10 13×13）へと大幅刷新。全スロットを18×18ピッチに統一し、アイコン同士の重なり・密集を完全解消。
  - 拡張作業台にJEIレシピ自動配置機能（「＋」ボタン）を追加。プレイヤーインベントリから作業台グリッドへワンクリックで材料を一括配置可能に。
  - 鉱石一括破壊機に「一括圧縮マナ」専用出力スロットを追加。機械を撤去しなくても、蓄積されたマナをUI上から直接回収可能に改善。
  - 圧縮妖精の触媒処理ロジックを完全適正化：1サイクルの生産枠 $9^{\text{compressionTier}}$ 個に対し、投入された触媒数分だけ該当Tierのマナが生産され、**余った生産枠はすべてTier 0マナとして生産**されるように修正。複数種の触媒投入時も1サイクルの総生産枠内で正しく按分。
  - 妖精ブロックのクールダウン秒数を `ContainerData` でパケット同期し、UI上の残り秒数カウントダウンがリアルタイムに変化するよう修正。

---

## [0.11.2-alpha] - 2026-11-03

### Fixed / 修正
- **EN**:
  - Resolved raw internal localization keys appearing in JEI header and tooltips.
  - Aligned JEI Extended Crafting category aesthetics with Extended Crafting Reforked (clean slot backgrounds, clear headers, full bilingual localization).
- **JA**:
  - JEIのヘッダーおよびツールチップに生キー（内部名称）が表示されていた不具合を修正。
  - Extended Crafting Reforkedに準拠したJEI表示の刷新（スロット枠の美麗な描画、タイトル・レシピ名の明確な表示、完全な日英多言語対応）。

### Added / 追加
- **EN**:
  - Comprehensive registration of all Extended Crafting recipes in JEI strictly matching the Mod Specification Document:
    - Tier 1 (4x4): Mana, Cores, Ores, Catalysts, Tables.
    - Tier 2 to 10: Even-tier shard deconstruction & 12/32/60/96/140 core recipes, odd-tier direct upgrades.
    - End-Game (13x13): Pure Mana & Catalysts, Mana Wisdom, 4 Root Manas (Star, Quantum, Abyssal, Marine), Mana Root, Origin Equipment, and Infinite Mana.
- **JA**:
  - 企画書（第1部・第2部）に記載されたすべての拡張クラフトレシピをJEIに完全網羅して登録：
    - Tier 1（4×4）: マナ、コア、鉱石、触媒、作業台の各クラフト。
    - Tier 2〜10: 偶数Tierの設備破片（×4）とコアによる進化レシピ、奇数Tierの直接アップグレードレシピ。
    - エンドゲーム（13×13）: 無垢のマナ/鉱石/触媒、マナの叡智、4大根源マナ（星、量子、深淵、海洋）、マナの根源、根源装備一式、無限のマナ。

---

## [0.11.1-alpha] - 2026-11-03

### Fixed / 修正
- **EN**:
  - Fixed `Slot not in valid range` crash/kick when opening BlockEntities (Thawer, Fairy Block, etc.) placed in older versions or loaded with legacy NBT size tags.
  - Enforced minimum slot sizes in `ItemStackHandler.setSize()` and `load()` to prevent legacy NBT from downsizing inventories below current slot specifications.
- **JA**:
  - 旧バージョンで設置・保存されたブロックエンティティ（解凍機、妖精ブロックなど）のUIを開いた際に、保存された旧NBTのSizeタグによってスロットが縮小され `Slot not in valid range` でキックされる不具合を修正。
  - `ItemStackHandler` の `setSize` および `load` において現在の必要スロット数未満に縮小されないよう下限ガードを適用し、旧データからの安全なスロット自動拡張に対応。

---

## [0.11.0-alpha] - 2026-11-03

### Fixed / 修正
- **EN**:
  - Fixed server freeze / infinite loop when shift-clicking or inserting ores into the Batch Ore Breaker. Enabled processing for both active and inactive mana ores.
  - Fixed slot alignment, clipping, and overlapping in Extended Crafting Tables (4x4 to 13x13) by standardizing to 18x18 pitch and dynamically calculating panel width/height (referencing Extended Crafting Reforked architecture).
  - Resolved slot rendering offset bugs across all GUI screens by synchronizing slot backgrounds directly with menu slot coordinates.
- **JA**:
  - 鉱石一括破壊機への鉱石投入時（Shiftクリック含む）に無限ループとなりサーバー/ゲームがフリーズする不具合を完全修正。活性マナ鉱石および非活性マナ鉱石の両方の一括溶解処理に対応。
  - 拡張作業台（4×4〜13×13）においてスロットが重なったりインベントリがGUI枠外にはみ出る不具合を、18×18ピッチ統一および画面サイズ・スロット背景描画の完全同期（Extended Crafting Reforked準拠）により解決。
  - 全GUIスクリーンにおいて、Menu側のスロット座標とScreen側のスロット枠描画のズレ・隙間・不要な重なりを完全に解消。

### Changed / 変更
- **EN**:
  - Overhauled Fairy inventory: 8x8 (64 slots) Catalyst inputs and 8x8 (64 slots) Mana outputs, each supporting up to `Integer.MAX_VALUE` stacks.
  - Overhauled Thawer: UI-managed 2x2 input slots and 10x10 (100 slots) output grid, each supporting up to `Integer.MAX_VALUE` stacks.
  - Added dedicated Extended Crafting category (`ExtendedCraftingJeiCategory`) to JEI displaying recipes from Tier 1 (4x4) through Tier 10 (13x13).
- **JA**:
  - 妖精ブロックのインベントリを大幅刷新：触媒入力 8×8 (64スロット) および生産マナ出力 8×8 (64スロット) をUI管理化し、全スロット上限を `Integer.MAX_VALUE`（約21億個）に対応。
  - 解凍機のUIを大幅刷新：入力（2×2）および出力 10×10 (100スロット) をUI管理化し、出力スロット上限を `Integer.MAX_VALUE` に拡張。
  - JEIに「拡張クラフト」専用カテゴリ（`ExtendedCraftingJeiCategory`）を新設し、Tier 1 (4×4) から Tier 10 (13×13) の全レシピを視覚的に閲覧可能に対応。

---

## [0.10.0-alpha] - 2026-11-03

### Added / 追加
- **EN**:
  - Full GUI / UI for all block entities (`FairyBlock`, `Thawer`, `BatchOreBreaker`, `ExtendedCraftingTable`, `FairyAttractor`) utilizing vanilla inventory textures.
  - Tier 0 Mana passive generation for Fairy Blocks operating without any catalyst.
  - Safe NBT-preserved block dismantling: Blocks with internal inventories no longer scatter items on break; contents are securely packed into the dropped item's `BlockEntityTag` and restored upon placement.
  - Optional JEI integration plugin (`ExponentialManaJeiPlugin`):
    - Recipe browsing and catalyst registration for Extended Crafting Tables and Fairies.
    - Comprehensive acquisition guides for Tier 0 Mana and Inactive Ores.
    - In-depth info guides for all machines, Batch Compressed Mana, and Infinite Mana.
  - Natural Overworld surface world generation for Fairy Blocks (`fairy_block_patch`).
- **JA**:
  - バニラインベントリテクスチャを流用した全ブロックエンティティ用GUI（`FairyBlock`, `Thawer`, `BatchOreBreaker`, `ExtendedCraftingTable`, `FairyAttractor`）を追加。
  - 触媒を持たない妖精ブロックによる Tier 0 マナのパッシブ自動生産機能を追加。
  - 内部インベントリを持つ全ブロックの安全なNBT保持ドロップ化：破壊時に地面にアイテムをばら撒かず、ブロックアイテム自身の `BlockEntityTag` に内部データを記録・保持し、再設置時に完全復元。
  - JEI（Just Enough Items）オプション連携機能の追加：
    - 拡張作業台および妖精ブロックの全レシピ表示・触媒登録。
    - Tier 0 マナおよび非活性鉱石の詳細な入手方法ガイドの追加。
    - 全機械（一括破壊機、解凍機、妖精、妖精誘引機、一括圧縮マナ、無限のマナ、根源装備）の解説ガイドの追加。
  - 妖精ブロックの地上（オーバーワールド地表草ブロック上）への自然生成を追加。

## [0.9.1-alpha] - 2026-11-03

### Changed / 変更
- **EN**:
  - Differentiated textures for Mana Ore Shards and Fairy Catalyst Shards.
  - Mana Ore Shards now feature raw crystal protruding from stone matrix.
  - Fairy Catalyst Shards feature crescent-shaped cracked catalyst glass with gold rim and glowing essence.
  - Filtered shard textures strictly to Tier 1, 3, 5, 7, and 9 (even-grid table deconstruction only; odd grids have central slot and require no shards).
- **JA**:
  - マナ鉱石破片（`mana_ore_shard`）と妖精触媒破片（`fairy_catalyst_shard`）のテクスチャを個別デザイン化。
  - 鉱石破片は石の母岩から鋭く突き出る原石結晶デザインを採用。
  - 触媒破片は金の口金と割れたガラスフラスコ、内部に輝く触媒エッセンスを表現した三日月状デザインを採用。
  - 偶数グリッド解体用（Tier 1, 3, 5, 7, 9）のみに限定・整理（奇数グリッドは中央1マス配置のため破片不要）。

## [0.9.0-alpha] - 2026-11-03

### Added / 追加
- **EN**:
  - Added Inactive Pure Mana Ore (`inactive_pure_mana_ore`).
  - Extracted genuine vanilla assets (`stone.png`, `obsidian.png`, `diamond_*.png`) for pixel-perfect texture recoloring.
  - Recycled vanilla diamond armor & sword textures with Origin color palette.
  - Overlaid mana crystal veins directly onto vanilla `stone.png` and `obsidian.png`.
  - Adjusted Abyssal Mana to darker, deeper sculk/echo-shard blue.
  - Synchronized bilingual translations in `en_us.json` and `ja_jp.json`.
- **JA**:
  - 非活性無垢のマナ鉱石（`inactive_pure_mana_ore`）を追加。
  - 公式バニラアセット（`stone.png`, `obsidian.png`, `diamond_*.png`等）を直接流用したピクセル完全準拠のテクスチャリカラーを適用。
  - 根源武具一式（兜、胸当て、脚甲、靴、剣）をバニラダイヤ装備のテクスチャから根源カラーへリカラー。
  - バニラの石（`stone.png`）および黒曜石（`obsidian.png`）のテクスチャをそのまま下敷きにし、鉱脈部分のみ各Tierテーマ色で着色。
  - 深淵のマナの明るさを落とし、暗く神秘的な青（ダークスカルクブルー）に調整。
  - 日英言語ファイル（`en_us.json`, `ja_jp.json`）を完全同期更新。

## [0.8.0-alpha] - 2026-11-03

### Changed / 変更
- **EN**:
  - Overhauled textures according to feedback:
    - Pure Mana made brilliant pure white with subtle pearl shading.
    - Completely differentiated Infinite Mana (Cosmic singularity) from Root of Mana (4 converging root colors).
    - Redesigned 4 Root Manas based on origin items (Nether Star, End Crystal, Echo Shard/Sculk, Conduit Heart).
    - Reduced industrial elements in core/casing textures in favor of mystical rune frames.
    - Recreated Batch Compressed Mana with four 1/4-size Tier 0 mana spheres and diagonal enchantment shimmer.
    - Rebuilt all Tier 0-10 active ores onto authentic vanilla Stone matrix, and inactive ores onto vanilla Obsidian matrix.
    - Redesigned Batch Breaker (crusher teeth) and Thawer (frost chamber) for instant visual distinction.
    - Restructured Origin Armor and Sword to match vanilla equipment silhouettes and Root of Mana color palette.
- **JA**:
  - フィードバックに基づき全テクスチャを抜本的に再設計・改訂:
    - 無垢のマナを純白・パール陰影の真っ白な光球へ調整。
    - 「無限のマナ」（宇宙特異点・ブラックホール）と「マナの根源」（4つの根源色が収束する渦）を完全差別化。
    - 4つの根源マナをもととなるアイテム（ネザースター四芒星、エンドクリスタルガラス、残響の欠片スカルク、コンジット心臓）に寄せて再構築。
    - コアの装甲から工業的要素を抑え、神秘的な魔導ルーンフレームへシフト。
    - 一括圧縮マナをご指示通り「四隅にぴったりはまった1/4サイズTier0マナ＋対角エンチャント光彩」で制作。
    - 全Tier（0〜10）の通常鉱石をバニラ石の斑点母岩へ、非活性鉱石をバニラ黒曜石母岩＋拘束バンドへ刷新。
    - 一括破壊機（粉砕歯クラッシャー）と解凍機（氷霜加熱チャンバー）の見た目を一目で区別できるよう差別化。
    - 根源装備一式および剣の形状をバニラ装備の正統なシルエットに統一し、マナの根源と同系統の神聖カラー（深淵紫・純金・量子シアン）で調和。

## [0.7.0-alpha] - 2026-11-03

### Added / 追加
- **EN**:
  - Implemented 16×16 pixel art textures for all items and blocks following tier theme colors.
  - Mana items designed as glowing theme spheres.
  - Core items designed with mechanical armor casing with large theme spheres peeking through cross slits.
  - Generated all model JSONs (`models/item`, `models/block`) and `blockstates` JSONs.
- **JA**:
  - Tierごとのテーマカラー（石炭、銅、赤石、グロウストーン、鉄、金、ダイヤ、黒曜石、エメラルド、ネザライト等）に基づく全アイテム・ブロックの16×16ピクセルテクスチャを制作・配置。
  - マナアイテムをテーマ色の発光球体として制作。
  - コアアイテムを四隅リベット留めの金属装甲の隙間・スリットから大きなテーマ色の球体が覗き見えるデザインとして制作。
  - すべてのアイテムモデル・ブロックモデル・ブロックステートのJSON定義を生成・配備。

## [0.6.0-alpha] - 2026-11-03

### Added / 追加
- **EN**:
  - Implemented Origin Armor set (`origin_helmet`, `origin_chestplate`, `origin_leggings`, `origin_boots`) with creative flight and absolute invulnerability (nullifying all damage including `/kill` and Void).
  - Implemented Origin Sword (`origin_sword`) with `/kill` instant-death logic against hit entities.
  - Implemented Infinite Mana duplication crafting recipe (`InfiniteManaDuplicationRecipe`) multiplying non-NBT items into full 64-item stacks, with strict NBT anti-exploit blocking.
  - Added Mana Materials (10 tiers), Compressed Mana Materials (10 tiers), Compressed Lapis, Amethyst, and Quartz.
  - Added Mana Symbols (Tier 0 to 10).
  - Synchronized bilingual translations in `en_us.json` and `ja_jp.json`.
- **JA**:
  - クリエイティブ飛行および完全無敵化（`/kill` コマンドやVoidダメージすら完全無効化）を備えた根源防具一式（`origin_helmet`, `origin_chestplate`, `origin_leggings`, `origin_boots`）を実装。
  - 攻撃対象を無敵貫通で即座に消滅させる `/kill` ロジック搭載の根源の剣（`origin_sword`）を実装。
  - NBTなしの任意アイテムを64個に増殖し、NBT保持アイテムを厳格に拒否する無限のマナ増殖レシピ（`InfiniteManaDuplicationRecipe`）を実装。
  - 10種のマナ素材、10種の圧縮マナ素材、圧縮ラピスラズリ・アメジスト・ネザークォーツを追加。
  - 各種マナの象徴（Tier 0〜10）を追加。
  - 日英言語ファイル（`en_us.json`, `ja_jp.json`）を完全同期更新。

## [0.5.0-alpha] - 2026-11-03

### Added / 追加
- **EN**:
  - Implemented Extended Crafting Table blocks from Tier 1 (4×4) up to Tier 10 (13×13, 169 slots).
  - Implemented `ExtendedCraftingTableBlockEntity` supporting energy-free automatic crafting with AE2 Pattern Provider compatibility (`IItemHandler`).
  - Added Tier 1 crafting recipes (Tier 1 Mana, Core, Ore, Catalyst, and 4×4 Crafting Table).
  - Implemented Even-Tier deconstruction recipes (breaking down Tier 1, 3, 5, 7, 9 ores, catalysts, and tables into 4 shards).
  - Synchronized bilingual translations in `en_us.json` and `ja_jp.json`.
- **JA**:
  - Tier 1（4×4）から Tier 10（13×13、169スロット）に至る拡張作業台ブロックを実装。
  - AE2のパターンプロバイダー等の自動クラフトにデフォルト対応（`IItemHandler`公開・エネルギー消費なし）する `ExtendedCraftingTableBlockEntity` を実装。
  - Tier 1の各種進化クラフトレシピ（Tier1マナ、コア、鉱石、触媒、4×4拡張作業台）を追加。
  - 偶数Tier移行用の設備分解レシピ（Tier 1, 3, 5, 7, 9の鉱石・触媒・作業台を単体クラフトで欠片×4に分解）を実装。
  - 日英言語ファイル（`en_us.json`, `ja_jp.json`）を完全同期更新。

## [0.4.0-alpha] - 2026-11-03

### Added / 追加
- **EN**:
  - Implemented Batch Ore Breaker (`batch_ore_breaker`) with 10×10 internal slots (`Int_MAX` capacity per slot) and virtual timer processing.
  - Implemented safe unscattered drop mechanism: drops NBT-encoded Batch Compressed Mana (`batch_compressed_mana`) on breaker dismantle.
  - Implemented Thawer (`thawer`) to safely extract and output real item-form mana from Batch Compressed Mana.
  - Added recipe for Batch Ore Breaker (8 Mana Ores) and Thawer.
  - Synchronized bilingual translations in `en_us.json` and `ja_jp.json`.
- **JA**:
  - 10×10（100スロット、各スロット`Int_MAX`容量）の内部インベントリと仮想タイマー処理を備えた鉱石一括破壊機（`batch_ore_breaker`）を実装。
  - ワールド撤去時にアイテムを散乱させず、内部データを焼き込んだ一括圧縮状態のマナ（`batch_compressed_mana`）として安全にドロップする機構を実装。
  - 一括圧縮状態のマナから通常アイテム状態のマナを安全に解凍・抽出する解凍機（`thawer`）を実装。
  - 鉱石一括破壊機（マナ鉱石×8）および解凍機のクラフトレシピを追加。
  - 日英言語ファイル（`en_us.json`, `ja_jp.json`）を完全同期更新。

## [0.3.0-alpha] - 2026-11-03

### Added / 追加
- **EN**:
  - Added Inactive Tier 0 Mana Ore (`inactive_tier0_mana_ore`).
  - Updated Tier 0 Mana Ore to drop both Tier 0 Mana and Inactive Tier 0 Mana Ore upon mining.
  - Implemented Fairy Block, Compressed Fairy Blocks (Tier 1 to 10: $9^1$ to $9^{10}$), and Fairy Attractor.
  - Implemented `FairyBlockEntity` with 1-minute production timer, non-consumed catalyst slot with `Int_MAX` capacity.
  - Allowed external tick acceleration on Fairy Blocks.
  - Implemented Fairy Residue crafting logic (returns Fairy Block unconsumed).
  - Implemented Fairy 9x compression crafting recipes.
  - Added GUI compact stack formatter (`1.5K`, `20M`, `2.1B`) with exact count tooltips.
  - Synchronized bilingual translations in `en_us.json` and `ja_jp.json`.
- **JA**:
  - 非活性Tier0 マナ鉱石（`inactive_tier0_mana_ore`）を追加。
  - Tier0 マナ鉱石の採掘時に「Tier0 マナ」と「非活性Tier0 マナ鉱石」の双方がドロップするよう更新。
  - 妖精ブロック、圧縮妖精ブロック（Tier 1〜10: $9^1$〜$9^{10}$）、および妖精誘引機を実装。
  - 1分間の自動生産タイマー、消費なしの触媒スロット（`Int_MAX` 容量対応）を備えた `FairyBlockEntity` を実装。
  - 妖精ブロックに対する外部MODのTick加速を意図的に許容する設計を適用。
  - 妖精ブロックを消費せず「妖精の残滓」を抽出する単体クラフトロジックを実装。
  - 妖精ブロックの9圧縮クラフトレシピを実装。
  - GUIスロットの大容量スタック表示フォーマッタ（`1.5K`, `20M`, `2.1B`）および正確な数値ツールチップを実装。
  - 日英言語ファイル（`en_us.json`, `ja_jp.json`）を完全同期更新。

## [0.2.0-alpha] - 2026-11-03

### Added / 追加
- **EN**:
  - Implemented core registry systems (`ModCreativeModeTabs`, `ModItems`, `ModBlocks`).
  - Added Tier 0 to 10 Mana items and Core items.
  - Added Fairy Residue, Tier 1 to 10 Fairy Catalysts, and Pure Fairy Catalyst.
  - Added Shard items for even-tier transitions (Ore Shards, Catalyst Shards, Mini Crafting Tables for Tiers 1, 3, 5, 7, 9).
  - Added Tier 0 to 10 Mana Ore blocks, Pure Mana Ore, and Inactive Mana Ore blocks (Tier 1 to 10).
  - Implemented custom `InactiveManaOreBlock` with 5-minute activation timer and obsidian-grade resistance.
  - Full bilingual localization sync for all items and blocks in `en_us.json` and `ja_jp.json`.
- **JA**:
  - コアレジストリシステム（`ModCreativeModeTabs`, `ModItems`, `ModBlocks`）を実装。
  - Tier 0〜10のマナアイテムおよびコアアイテムを追加。
  - 妖精の残滓、Tier 1〜10の妖精活発化触媒、無垢の妖精活発化触媒を追加。
  - 偶数Tier移行用の各種欠片アイテム（Tier 1, 3, 5, 7, 9の鉱石の欠片、触媒の欠片、ミニ拡張作業台）を追加。
  - Tier 0〜10のマナ鉱石ブロック、無垢のマナ鉱石、およびTier 1〜10の非活性マナ鉱石ブロックを追加。
  - 5分間の活性化タイマーと黒曜石同等の耐久度・耐爆性を持つカスタムブロック `InactiveManaOreBlock` を実装。
  - すべてのアイテム・ブロックに対応する日英言語ファイル（`en_us.json`, `ja_jp.json`）を同期更新。

## [0.1.0-alpha] - 2026-11-03

### Added / 追加
- **EN**: Initial project foundation for Minecraft Forge 1.20.1.
  - Setup ForgeGradle 6.x build configuration targeting Java 17 with UTF-8 encoding.
  - Project configuration: ModID `exponential_mana`, GPL-3.0 license.
  - Bilingual documentation (README.md, CHANGELOG.md, CONFIG.md).
- **JA**: Minecraft Forge 1.20.1 向け初期プロジェクト基盤を構築。
  - Java 17ターゲットおよびUTF-8エンコーディングを明示したForgeGradle 6.xビルド構成を整備。
  - プロジェクト構成設定: ModID `exponential_mana`、GPL-3.0ライセンス。
  - 日英バイリンガルドキュメント (README.md, CHANGELOG.md, CONFIG.md) を整備。
