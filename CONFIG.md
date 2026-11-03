# Configuration / コンフィグ説明書

[English](#english) | [日本語](#japanese)

---

<a name="english"></a>
## English

`Exponential Mana` provides customizable server and client configuration options via Forge's standard config system.

### Configuration File
- **Server Config**: `.minecraft/saves/<world_name>/serverconfig/exponential_mana-server.toml` (or `defaultconfigs/exponential_mana-server.toml`)
- **Client Config**: `.minecraft/config/exponential_mana-client.toml`

### Config Options

#### Server Configuration (`exponential_mana-server.toml`)
| Option | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `fairyBaseCooldownTicks` | Integer | `1200` (60s) | Base cooldown in ticks for Fairy Blocks to generate mana. |
| `inactiveOreActivationTicks` | Integer | `6000` (5m) | Required time in ticks for inactive mana ores placed in the world to activate. |
| `allowExternalTickAcceleration` | Boolean | `true` | Whether external tile entity tick acceleration (Torcherino, Watch of Flowing Time, etc.) is allowed on fairy blocks. |
| `batchBreakerMaxSlotCapacity` | Long | `2147483647` | Maximum capacity per slot in the Batch Ore Breaker. |

#### Client Configuration (`exponential_mana-client.toml`)
| Option | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `enableCompactStackDisplay` | Boolean | `true` | Formats large stack quantities as `1.5K`, `20M`, `2.1B` in GUI slots. |
| `compactStackThreshold` | Integer | `1000` | Minimum item count required before formatting is applied. |

---

<a name="japanese"></a>
## 日本語

『**Exponential Mana**』は、Minecraft Forge標準のコンフィグシステムを通じて各種動作パラメータをカスタマイズできます。

### 設定ファイル配置場所
- **サーバー設定 (ワールド別)**: `.minecraft/saves/<ワールド名>/serverconfig/exponential_mana-server.toml` (または `defaultconfigs/exponential_mana-server.toml`)
- **クライアント設定**: `.minecraft/config/exponential_mana-client.toml`

### 設定項目一覧

#### サーバー設定 (`exponential_mana-server.toml`)
| 設定項目 | データ型 | デフォルト値 | 挙動説明 |
| :--- | :--- | :--- | :--- |
| `fairyBaseCooldownTicks` | 整数 (Integer) | `1200` (60秒) | 妖精ブロックがマナを自動生産する基本インターバル（Tick単位）。 |
| `inactiveOreActivationTicks` | 整数 (Integer) | `6000` (5分) | ワールドに設置された非活性マナ鉱石が活性化するまでの所要時間（Tick単位）。 |
| `allowExternalTickAcceleration` | 真偽値 (Boolean) | `true` | 他MODの時間加速（Tick加速）による妖精ブロックの加速を許容するかどうか。 |
| `batchBreakerMaxSlotCapacity` | 長整数 (Long) | `2147483647` | 鉱石一括破壊機の1スロットあたり格納上限数。 |

#### クライアント設定 (`exponential_mana-client.toml`)
| 設定項目 | データ型 | デフォルト値 | 挙動説明 |
| :--- | :--- | :--- | :--- |
| `enableCompactStackDisplay` | 真偽値 (Boolean) | `true` | GUIスロット内のアイテムスタック数が大容量の場合、`1.5K`, `20M`, `2.1B` などの短縮表記を行うか。 |
| `compactStackThreshold` | 整数 (Integer) | `1000` | 短縮表記を適用する最小アイテム数。 |
