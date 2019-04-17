# 数据协议

| 数据类型 | 名称 | 说明 |
|:---:|:---:|:---:|
|byte|version|版本号|
|byte|crc|CRC|
|byte[6]|srcAddr|源地址：服务器地址是0|
|byte[6]|dstAddr|目标地址：同上|
|long|timestamp|时间戳|
|byte|seq|序列号：响应在请求上加1|
|byte|tag|类型|
|short|length|数据长度|
|byte[]|data|数据段|