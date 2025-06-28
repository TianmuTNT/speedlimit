# SpeedLimit VL系统说明

## 新增功能

SpeedLimit插件现已支持VL（违规次数）累积系统，可以自动踢出或封禁频繁超速的玩家。**普通飞行和鞘翅飞行的VL分别计算**，可以设置不同的阈值和惩罚措施。

## 配置选项

在 `config.yml` 文件中添加了以下配置：

```yaml
# VL累积系统配置
# 设置为 -1 表示不执行相应操作
vl-system:
  # 普通飞行VL配置
  normal-flying:
    # 累积多少VL后踢出玩家 (-1 = 不踢出)
    kick-threshold: 5
    # 累积多少VL后封禁玩家 (-1 = 不封禁)
    ban-threshold: 10
    # 踢出玩家的命令 (使用 {player} 作为玩家名占位符)
    kick-command: "kick {player} 普通飞行超速违规次数过多"
    # 封禁玩家的命令 (使用 {player} 作为玩家名占位符)
    ban-command: "ban {player} 普通飞行严重超速违规"
  
  # 鞘翅飞行VL配置
  elytra-flying:
    # 累积多少VL后踢出玩家 (-1 = 不踢出)
    kick-threshold: 3
    # 累积多少VL后封禁玩家 (-1 = 不封禁)
    ban-threshold: 6
    # 踢出玩家的命令 (使用 {player} 作为玩家名占位符)
    kick-command: "kick {player} 鞘翅飞行超速违规次数过多"
    # 封禁玩家的命令 (使用 {player} 作为玩家名占位符)
    ban-command: "ban {player} 鞘翅飞行严重超速违规"
  
  # VL重置时间 (分钟，-1 = 永不重置)
  vl-reset-minutes: 30
```

### 配置说明

#### 普通飞行VL配置
- **kick-threshold**: 玩家普通飞行累积多少VL后被踢出服务器，设置为-1则不踢出
- **ban-threshold**: 玩家普通飞行累积多少VL后被封禁，设置为-1则不封禁
- **kick-command**: 普通飞行超速踢出玩家时执行的命令，{player}会被替换为玩家名
- **ban-command**: 普通飞行超速封禁玩家时执行的命令，{player}会被替换为玩家名

#### 鞘翅飞行VL配置
- **kick-threshold**: 玩家鞘翅飞行累积多少VL后被踢出服务器，设置为-1则不踢出
- **ban-threshold**: 玩家鞘翅飞行累积多少VL后被封禁，设置为-1则不封禁
- **kick-command**: 鞘翅飞行超速踢出玩家时执行的命令，{player}会被替换为玩家名
- **ban-command**: 鞘翅飞行超速封禁玩家时执行的命令，{player}会被替换为玩家名

#### 全局配置
- **vl-reset-minutes**: VL自动重置的时间间隔（分钟），设置为-1则永不重置

## 权限

- `speedlimit.bypass`: 绕过速度限制（原有权限）
- `speedlimit.verbose`: 接收详细的超速违规消息（新增权限）

## 命令

### 原有命令
- `/speedlimit help` - 显示帮助信息
- `/speedlimit reload` - 重载配置

### 新增命令
- `/speedlimit vl <玩家名>` - 查看指定玩家的所有VL信息（普通飞行+鞘翅飞行）
- `/speedlimit vlnormal <玩家名>` - 查看指定玩家的普通飞行VL信息
- `/speedlimit vlelytra <玩家名>` - 查看指定玩家的鞘翅飞行VL信息
- `/speedlimit resetvl <玩家名>` - 重置指定玩家的所有VL
- `/speedlimit resetvlnormal <玩家名>` - 重置指定玩家的普通飞行VL
- `/speedlimit resetvlelytra <玩家名>` - 重置指定玩家的鞘翅飞行VL
- `/speedlimit vlall` - 查看所有玩家的VL信息

## 功能特点

1. **分别VL累积**: 普通飞行和鞘翅飞行的VL分别计算，互不影响
2. **独立阈值设置**: 可以为两种飞行类型设置不同的kick和ban阈值
3. **自定义命令**: 支持为不同飞行类型设置不同的kick和ban命令
4. **自动VL重置**: 可配置时间间隔自动重置所有玩家的VL
5. **详细日志**: 所有VL相关操作都会记录到控制台，区分飞行类型
6. **权限管理**: 只有OP或拥有相应权限的玩家才能查看VL信息
7. **精确管理**: 可以单独查看和管理某种飞行类型的VL

## 使用示例

### 基本配置（普通飞行更宽松，鞘翅飞行更严格）
```yaml
vl-system:
  normal-flying:
    kick-threshold: 5
    ban-threshold: 10
    kick-command: "kick {player} 普通飞行超速违规次数过多"
    ban-command: "ban {player} 普通飞行严重超速违规"
  
  elytra-flying:
    kick-threshold: 3
    ban-threshold: 6
    kick-command: "kick {player} 鞘翅飞行超速违规次数过多"
    ban-command: "ban {player} 鞘翅飞行严重超速违规"
  
  vl-reset-minutes: 60
```

### 使用Essentials插件的命令
```yaml
vl-system:
  normal-flying:
    kick-threshold: 5
    ban-threshold: 10
    kick-command: "essentials:kick {player} 普通飞行超速违规次数过多"
    ban-command: "essentials:ban {player} 普通飞行严重超速违规"
  
  elytra-flying:
    kick-threshold: 3
    ban-threshold: 6
    kick-command: "essentials:kick {player} 鞘翅飞行超速违规次数过多"
    ban-command: "essentials:ban {player} 鞘翅飞行严重超速违规"
  
  vl-reset-minutes: 60
```

### 只限制鞘翅飞行
```yaml
vl-system:
  normal-flying:
    kick-threshold: -1  # 不限制普通飞行
    ban-threshold: -1
    kick-command: ""
    ban-command: ""
  
  elytra-flying:
    kick-threshold: 3
    ban-threshold: 6
    kick-command: "kick {player} 鞘翅飞行超速违规"
    ban-command: "ban {player} 鞘翅飞行严重超速违规"
  
  vl-reset-minutes: 30
```

## 注意事项

1. 确保配置的kick和ban命令在服务器上可用
2. VL重置是全局性的，会影响所有玩家的所有类型VL
3. 只有OP或拥有相应权限的玩家才能使用VL管理命令
4. 所有VL操作都会记录到服务器日志中，并区分飞行类型
5. 普通飞行和鞘翅飞行的VL完全独立，不会相互影响
6. 可以针对不同飞行类型设置不同的惩罚策略 