# 数据协议

| 数据类型 | 偏移量 | 名称 | 说明 |
|:---|:---|:---|:---|
|byte[2]|0x00|magic|魔术|
|byte|0x02|version|版本号|
|byte|0x03|crc|CRC|
|byte[6]|0x04|srcAddr|源地址：00:00:00:00:00:00->服务器|
|byte[6]|0x0A|dstAddr|目标地址：其他使用MAC|
|long|0x10|timestamp|时间戳，毫秒级|
|byte|0x18|seq|序列号：响应在请求上加1|
|byte|0x19|category|大类型|
|byte|0x1A|tag|小类型|
|short|0x1B|last|0：不分包，其他：剩余包数量|
|short|0x1D|length|数据长度|
|byte[]|0x1F|data|数据段，可以为空|

> 最小包大小**31byte**

# 枚举 dev_feature (4byte)
|枚举值|说明|
|:---|:---|
|0x01|光照传感器|
|0x02|人体传感器|
|0x04|麦克风|
|0x08|扬声器|
|0x10|继电器|
|0x20|WS2812|
|0x30|温度传感器|
|0x40|湿度传感器|
|0x80|降雨传感器|
|0x100|降雨传感器|
|0x200|摄像头|


# category、tag说明
|category(1byte)|tag(1byte)|data(0..n byte)|说明|
|:---|:---|:---|:---|
|0x01|0x01|dev_feature|登录|
||0x02|无|登出|
||0x03|1byte|心跳：协商心跳时间|
|0x02|0x01|无|查询在线设备信息|
||0x02|无|查询离线设备信息|
||0x03|无|查询不稳定设备信息|