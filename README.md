<div align="center">
  <h1>
    🍷
    <br>
    Burgundyred
  </h1>

![](https://badgen.net/github/last-commit/lucayeung/burgundyred)
![](https://badgen.net/github/commits/lucayeung/burgundyred)
![](https://badgen.net/github/license/lucayeung/burgundyred)
![](https://badgen.net/github/dependents-repo/lucayeung/burgundyred)

</div>

基于 SpringBoot 和 Dubbo 的红酒主题 B2C 商城。

后端划分为基础服务和聚合服务，聚合服务通过RPC调用基础服务。聚合服务又划分为买家端和卖家端。买家端返回HTML数据，使用Thymeleaf + JQuery实现定宽布局。卖家端只返回JSON数据配合Vue实现前后端分离。

# Get Started

参考`/doc`下的部署文档

# Preview

买家端

![buyer1.jpg](https://i.loli.net/2019/11/06/jFC7wgpZqG4KQJV.jpg)

![buyer2.jpg](https://i.loli.net/2019/11/06/WcJP53oUrmF47ns.jpg)

![确认订单.jpg](https://i.loli.net/2019/11/06/rmnQ5g9b7G2jodh.jpg)

![扫码支付.jpg](https://i.loli.net/2019/11/06/FXAGbwv1Sl5npIH.jpg)


# Diagram

**burgundyred架构图**

![burgundyred架构图.png](https://i.loli.net/2019/11/06/TitoeGn8bzZpmNP.png)

说明：为了方便开发，这里我将所有的基础服务放在**burgundyred-svc**中。后期可根据业务功能（这里我已经按service划分）将服务拆分为一个个进程。订单服务中包括支付的相关功能，可以将这部分内容拆分成单独的支付服务。降低耦合度，提高代码可维护性。

**burgundyred订单状态机**

![burgundyred订单状态机.png](https://i.loli.net/2019/11/06/6MejvJgSR9TtH3G.png)

**burgundyred模块职责**

![burgundyred模块职责.png](https://i.loli.net/2019/11/06/3qd5syA2FWUr876.png)

**勃艮第红鉴权流程**

![勃艮第红鉴权流程.png](https://i.loli.net/2019/11/06/3Iqtj4DRzKhQeoi.png)

**勃艮第红商场E-R图**

![勃艮第红商场E-R图.png](https://i.loli.net/2019/11/06/erlNhnmDCMxJTsP.png)

**用户行为**

![用户行为.png](https://i.loli.net/2019/11/06/9ljwOCnVfp8rKsS.png)

**勃艮第红层次模块图**

![勃艮第红层次模块图.png](https://i.loli.net/2019/11/06/qc2PrBCZUeugQL9.png)
