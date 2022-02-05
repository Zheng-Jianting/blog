## 前言

在以 markdown 格式写博客时涉及到插图，考虑到后续要放到 [个人博客](https://zhengjianting.com)，以链接的形式引用图片比较合适，因此简单写了一个 SpringBoot 应用并使用 docker 部署到 vps 上，作为一个简易的图片资源服务器，顺带记录一下部署过程。



## 基于 Dockerfile 部署 SpringBoot 项目

1. maven 打包项目

   ```shell
   mvn package
   ```

2. 编写 Dockerfile

   ```dockerfile
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
   ```

3. 将 jar 包以及 Dockerfile 上传至 linux 服务器同一目录下

4. 构建 docker 镜像

   ```shell
   docker build -t api:v1 .
   ```

5. 基于镜像新建一个 docker 容器并启动

   ```shell
   docker run -it -d --name api -p 9000:9000 -v ~/.api:/root/.api --restart=unless-stopped api:v1
   ```

6. 将图片上传至 linux 服务器 ~/.api/picture 目录下



## Nginx 反向代理

由于使用博客 zhengjianting.com 的子域名 api.zhengjianting.com 提供 api 服务，因此单独编写 nginx 配置文件

```shell
vim /etc/nginx/sites-available/api.zhengjianting.com
```

```nginx
server {
    listen 80;
    listen [::]:80;
    server_name api.zhengjianting.com www.api.zhengjianting.com;
    return 302 https://$server_name$request_uri;
}

server {

    # SSL configuration

    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    ssl_certificate         /etc/ssl/cert.pem;
    ssl_certificate_key     /etc/ssl/key.pem;
    ssl_client_certificate /etc/ssl/cloudflare.crt;
    ssl_verify_client on;

    server_name api.zhengjianting.com www.api.zhengjianting.com;

    location / {
        proxy_pass http://127.0.0.1:9000;
        proxy_set_header HOST $host;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

}
```

```shell
ln -s /etc/nginx/sites-available/api.zhengjianting.com /etc/nginx/sites-enabled/api.zhengjianting.com
```

```shell
nginx -t
systemctl restart nginx
```



## 效果展示

[测试图片](https://api.zhengjianting.com/picture?name=测试图片)

![测试图片](https://api.zhengjianting.com/picture?name=测试图片)



## 参考链接

[Docker -- 从入门到实践](https://yeasy.gitbook.io/docker_practice/)