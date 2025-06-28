# SpeedLimit VL系统说明

## 新增功能

SpeedLimit插件现已支持VL（违规次数）累积系统，可以自动踢出或封禁频繁超速的玩家。

## 配置选项

在 `config.yml` 文件中添加了以下配置：

```yaml
# VL累积系统配置
# 设置为 -1 表示不执行相应操作
vl-system:
  # 累积多少VL后踢出玩家 (-1 = 不踢出)
  kick-threshold: 5
  # 累积多少VL后封禁玩家 (-1 = 不封禁)
  ban-threshold: 10
  # 踢出玩家的命令 (使用 {player} 作为玩家名占位符)
  kick-command: "kick {player} 累积超速违规次数过多"
  # 封禁玩家的命令 (使用 {player} 作为玩家名占位符)
  ban-command: "ban {player} 累积超速违规次数过多"
  # VL重置时间 (分钟，-1 = 永不重置)
  vl-reset-minutes: 30
```

### 配置说明

- **kick-threshold**: 玩家累积多少VL后被踢出服务器，设置为-1则不踢出
- **ban-threshold**: 玩家累积多少VL后被封禁，设置为-1则不封禁
- **kick-command**: 踢出玩家时执行的命令，{player}会被替换为玩家名
- **ban-command**: 封禁玩家时执行的命令，{player}会被替换为玩家名
- **vl-reset-minutes**: VL自动重置的时间间隔（分钟），设置为-1则永不重置

## 权限

- `speedlimit.bypass`: 绕过速度限制（原有权限）
- `speedlimit.verbose`: 接收详细的超速违规消息（新增权限）

## 命令

### 原有命令
- `/speedlimit help` - 显示帮助信息
- `/speedlimit reload` - 重载配置

### 新增命令
- `/speedlimit vl <玩家名>` - 查看指定玩家的VL信息
- `/speedlimit resetvl <玩家名>` - 重置指定玩家的VL
- `/speedlimit vlall` - 查看所有玩家的VL信息

## 功能特点

1. **自动VL累积**: 每次玩家超速时自动增加VL
2. **自动踢出/封禁**: 达到阈值时自动执行配置的命令
3. **VL自动重置**: 可配置时间间隔自动重置VL
4. **详细日志**: 所有VL相关操作都会记录到控制台
5. **权限管理**: 只有OP或拥有相应权限的玩家才能查看VL信息
6. **自定义命令**: 支持自定义kick和ban命令

## 使用示例

### 基本配置
```yaml
vl-system:
  kick-threshold: 3
  ban-threshold: 5
  kick-command: "kick {player} 超速违规次数过多"
  ban-command: "ban {player} 严重超速违规"
  vl-reset-minutes: 60
```

### 使用Essentials插件的命令
```yaml
vl-system:
  kick-threshold: 3
  ban-threshold: 5
  kick-command: "essentials:kick {player} 超速违规次数过多"
  ban-command: "essentials:ban {player} 严重超速违规"
  vl-reset-minutes: 60
```

### 禁用踢出/封禁
```yaml
vl-system:
  kick-threshold: -1  # 不踢出
  ban-threshold: -1   # 不封禁
  vl-reset-minutes: 30
```

## 注意事项

1. 确保配置的kick和ban命令在服务器上可用
2. VL重置是全局性的，会影响所有玩家
3. 只有OP或拥有相应权限的玩家才能使用VL管理命令
4. 所有VL操作都会记录到服务器日志中 