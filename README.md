# burgundyred

后端划分为基础服务和聚合服务，聚合服务通过RPC调用基础服务。聚合服务又划分为买家端和卖家端。买家端返回HTML数据，使用Thymeleaf + JQuery实现定宽布局。卖家端只返回JSON数据配合Vue实现前后端分离。
备注：可参考下面架构图。

**项目还在看法中...**

[TOC]

## 技术

- Spring Boot 基础框架
- Dubbo/Zookeeper 服务调用和治理
- Redis 缓存，分布式锁
- ElasticSearch 搜索服务
- RabbitMQ 解耦，异步
- Ngnix 反向代理
- Vue 卖家端SPA
- Swagger 卖家端API文档自动生成
- JQuery 买家端Web页面
- 支付宝沙箱环境 支付，退款
- 七牛云对象存储
- JWT Json Web Token 无状态服务
- etc

## 架构图及其它相关图片

- burgundyred架构图
![burgundyred架构图.png](https://i.loli.net/2019/11/06/TitoeGn8bzZpmNP.png)

- burgundyred订单状态机
![burgundyred订单状态机.png](https://i.loli.net/2019/11/06/6MejvJgSR9TtH3G.png)

- burgundyred模块职责
![burgundyred模块职责.png](https://i.loli.net/2019/11/06/3qd5syA2FWUr876.png)

- 勃艮第红鉴权流程
![勃艮第红鉴权流程.png](https://i.loli.net/2019/11/06/3Iqtj4DRzKhQeoi.png)

- 勃艮第红商场E-R图
![勃艮第红商场E-R图.png](https://i.loli.net/2019/11/06/erlNhnmDCMxJTsP.png)

- 用户行为
![用户行为.png](https://i.loli.net/2019/11/06/9ljwOCnVfp8rKsS.png)

- 勃艮第红层次模块图
![勃艮第红层次模块图.png](https://i.loli.net/2019/11/06/qc2PrBCZUeugQL9.png)

## 需求分析，数据库表设计和部署请参考

doc文件下文件：
- burgundyred需求分析.md
- SQL.md
- idea构建发布docker镜像，私服上部署流程.md
