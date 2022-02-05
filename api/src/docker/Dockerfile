# 基础镜像
FROM java:8

# 将 /root/.api 挂载为匿名卷 在运行 docker run 命令时 -v 参数会覆盖该值
VOLUME /root/.api

# 将上下文路径下的 api-1.0-SNAPSHOT.jar 复制到 docker 容器的 /root/.api/ 目录下并解压
# 上下文路径在运行 docker build -t api:v1 . 命令时指定 (.)
ADD ./api-1.0-SNAPSHOT.jar /root/.api/api-1.0-SNAPSHOT.jar

# 修改 docker 容器内 /root/.api/api-1.0-SNAPSHOT.jar 的修改时间为当前时间
RUN bash -c 'touch /root/.api/api-1.0-SNAPSHOT.jar'

# api-1.0-SNAPSHOT.jar 运行在 9000端口
EXPOSE 9000

# 指定 docker 容器启动时运行 /root/.api/api-1.0-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/root/.api/api-1.0-SNAPSHOT.jar"]

# 指定维护者名字
MAINTAINER zhengjianting