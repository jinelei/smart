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
|byte|0x1F|reserved|保留字节|
|byte[]|0x20|data|数据段，可以为空|

> 最小包大小**32byte**  

# 枚举 dev_feature (8byte)

|枚举值|说明|
|:---|:---|
|0x01|光照传感器|
|0x02|人体传感器|
|0x04|麦克风|
|0x08|扬声器|
|0x10|继电器|
|0x20|WS2812|
|0x40|温度传感器|
|0x80|湿度传感器|
|0x100|降雨传感器|
|0x200|摄像头|
|0x400|电灯|


# category、tag说明
|category(1byte)|tag(1byte)|data(0..n byte)|说明|
|:---|:---|:---|:---|
|0x01|0x01|dev_feature(8byte), timeout(1byte)|登录|
|||errcode(byte), errmsg(byte N)|登录响应|
||0x02|无|登出|
||0x03|1byte|心跳：协商心跳时间|
|0x02|0x01|无|查询在线设备信息|
||0x02|无|查询离线设备信息|
||0x03|无|查询不稳定设备信息|

# errcode 说明 1byte
|errcode(hex)|errcode|errmsg|说明|
|:---|:---|:---|:---|
|0x00|0|SUCCESS|成功|
|0x01|1|FAILURE|普通失败|
|0x02|2|INVALID_PARAM|无效参数|
|0x03|3|NOT_IMPLEMENTED|未实现|
|0x04|4|DEPRECATED|已废弃|
|0x05|5|NO_PERMISSION|鉴权失败|
|0x06|6|EAUTH|登录失败|
|0x07|7|NOT_ALLOWED|不允许操作|
|0x08|8|NOT_EXISTS|不存在|
|0x09|0|ALREADY_EXISTS|已存在|
|0x0A|10|NO_DEVICE|设备不存在|
|0x0B|11|USER_OR_PASSWD_WRONG|用户名或密码错误|
|0x0C|12|OFFLINE|已离线|
|0x0D|13|ALREADY_CANCELED|已被取消|
|0x0E|14|IN_PROGRESS|正在处理|
|0x0F|15|EXPIRED|已过期|
|0x10|16|ALREADY_FINISH|已结束|
|0x11|17|IS_EMPTY|为空|
|0x12|18|INVALIIS_FULLD_PARAM|已满|
|0x13|19|DATA_CONFLICT|数据冲突|