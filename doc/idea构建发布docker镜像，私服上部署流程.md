## 使用docker构建镜像

1. 将应用打包成jar
```
mvn clean package -Dmaven.test.skip=true
```

2. 编写Dockerfile
```
FROM hub.c.163.com/library/java:8-alpine  # 选择java基础镜像

ADD target/*.jar app.jar

EXPOSE 8761

ENTRTPOINT ["java", "-jar", "/app.jar"]
```
备注：ENTRTPOINT和CMD的区别

3. 构建eureka镜像
```
docker build -t springcloud/eureka .
```
备注：`-t`指定镜像名称。

4. 启动eureka镜像
```
docker run -p 8761:8761 -d springcloud/eureka
```
备注：window上需要安装docker。

## 将自己的镜像上传到网易镜像云上

[网易云](https://www.163yun.com/?h=fc)

选择**镜像仓库**
- 创建镜像仓库
- 推送本地镜像

其它：**镜像中心**同步了dockerhub上的镜像。


