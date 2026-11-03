# Changelog / 変更履歴

All notable changes to this project will be documented in this file.
本プロジェクトのすべての注目すべき変更はこのファイルに記録されます。

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
