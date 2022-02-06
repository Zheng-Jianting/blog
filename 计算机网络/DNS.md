## DNS 介绍

域名系统（Domain Name System，DNS）是一个将域名和 IP 地址相互映射的分布式数据库。域名具有层次结构，从上到下依次为：根域名、顶级域名、二级 / 权威域名。

<div align="center"> <img width="75%" src="https://api.zhengjianting.com/picture?name=dnsHierarchy"/> </div> <br>

DNS 可以使用 UDP 或者 TCP 进行传输，使用的端口号都为 53，大多数情况下 DNS 使用 UDP 进行传输，这就要求域名解析器和域名服务器都必须自己处理超时和重传从而保证可靠性。在两种情况下会使用 TCP 进行传输：

- 如果返回的响应超过 512 字节（UDP 最大只支持 512 字节的数据）。
- 区域传送（区域传送是主域名服务器向辅助域名服务器传送变化的那部分数据）。

此外，DNS 也是一个使得主机能够查询分布式数据库的应用层协议。

## DNS 缓存

在将域名解析为 IP 的过程中，首先会依次判断以下缓存是否有相关记录：

- 浏览器缓存

  以 chrome 为例，访问 [chrome://chrome-urls](chrome://chrome-urls) 可以看到一系列 Chrome URLs，进入 [chrome://net-export](chrome://net-export) 后收集日志，随后在 [netlog-viewer.appspot.com](https://netlog-viewer.appspot.com/#import) 导入日志便可以看到浏览器的 DNS 缓存：

  <div align="center"> <img src="https://api.zhengjianting.com/picture?name=dnsBrowserCache"/> </div>

- 系统缓存

  以 win 10 为例，C:\Windows\System32\drivers\etc\hosts 记录了一部分域名和 IP 地址的映射关系，也可通过命令 ipconfig /displaydns 查询。

- 路由器缓存

- ISP（网络服务提供商）DNS 缓存，即本地 DNS 服务器缓存

若上述缓存都没命中，则客户端向本地 DNS 服务器发送 DNS 查询报文，由本地 DNS 服务器依次向根域名服务器、顶级域名服务器、二级域名服务器以递归（左图）或迭代（右图）的方式查询，直到将域名解析为 IP 地址，需要注意的是，以迭代方式查询时，请求主机和本地 DNS 服务器之间仍然是递归的。

<div align="center"> <img width="31%" src="https://api.zhengjianting.com/picture?name=dnsRecursive"/> <img width="32.6%" src="https://api.zhengjianting.com/picture?name=dnsIteration"/> </div> <br>

## DNS 报文

以博客域名 zhengjianting.com 为例，使用 ping 访问该域名时，通过 wireshark 抓包：

<div align="center"> <img src="https://api.zhengjianting.com/picture?name=dnsWiresharkOverview"/> </div> <br>

其中请求主机 IP 地址为 192.168.42.148，本地 DNS 服务器 IP 地址为 192.168.42.129（可以通过命令 ipconfig /all 查询），可以看到，产生了五条数据，对应两次查询，其中 Frame 7 是 Frame 6 的重传报文，Frame 6 和 Frame 8 是查询 zhengjianting.com 域名对应的 IPv4 地址的查询以及回答报文，Frame 9 和 Frame 10 是查询 IPv6 地址的查询以及回答报文，由于本地 DNS 服务器有缓存域名 zhengjianting.com 对应的 IP 地址，因此本地 DNS 服务器没有继续向根 DNS 服务器、顶级域名服务器等查询。

以 Frame 6 和 Frame 8 为例解读 DNS 查询和回答报文，其数据如下图所示：

<div align="center"> <img width="85%" src="https://api.zhengjianting.com/picture?name=dnsRequest"/> </div> <br>

<div align="center"> <img width="85%" src="https://api.zhengjianting.com/picture?name=dnsResponse"/> </div> <br>

首先需要明确的是，DNS 只有查询和回答这两种报文，并且，查询和回答报文有着相同的格式：

<div align="center"> <img width="60%" src="https://api.zhengjianting.com/picture?name=dnsMessage"/> </div> <br>

- Transaction ID（标识符）

  标识符是一个 16 bit 的数，用于标识该查询，如 Frame 6 和 Frame 8 都有相同的标识符 0xd3e1

- Flags（标志）

  标志字段中有若干标志，此处仅介绍比较重要的标志位：

  - Response：标识报文是查询报文（0）还是回答报文（1）
  - Authoritative：标识请求的 DNS 服务器是否该域名的权威 DNS 服务器，在该例子中，本地 DNS 服务器并不是域名 zhengjianting.com 的权威 DNS 服务器，在 NS 记录中可以查看该域名的权威服务器
  - Recursion desired：标识当该 DNS 服务器没有记录时是否希望递归查询

- Questions（问题数）

- Answer RRs（回答 RR 数）

- Authority RRs（权威 RR 数）

- Additional RRs（附加 RR 数）

- Queries（问题）

  问题区域包括以下字段：

  - 名字字段：正在被查询的主机名字，此处为 zhengjianting.com
  - 类型字段：资源记录（Resource Record，RR）类型，如 A，AAAA，NS，CNAME，MX 等
  - 地址类型：通常为互联网地址，值为 0x0001

- Answers（回答）

  回答区域字段和问题区域类似，在其基础上增加了主机名对应的 IP 地址

- Authoritative nameservers（权威）

  权威区域包括被查询主机的权威 DNS 服务器，即 NS 类型的资源记录

- Additional records（附加信息）

  在该例子中，Frame 8 回答报文还提供了权威 DNS 服务器的 IPv4 地址和 IPv6 地址

## DNS 记录

共同实现 DNS 分布式数据库的所有 DNS 服务器存储了**资源记录（Resource Record，RR）**，RR 提供了主机名到 IP 地址的映射，资源记录时一个包含以下字段的 4 元组：

_(Name, Value, Type, TTL)_

TTL（Time To Live）是该记录的生存时间，它决定了资源记录当从缓存中删除的时间，Name 和 Value 的值取决于 Type，常见的 Type 有：

- Type == A，即 A 记录，Name 是主机名，Value 是对应的 IPv4 地址，如上述的（zhengjianting.com，172.67.222.199，A）就是一条 A 记录。
- Type == AAAA，和 A 记录类似，不同的是 Value 是对应的 IPv6 地址。
- Type == NS，则 Name 是域名，Value 是知道如何将该域名映射为 IP 地址的权威 DNS 服务器，如上述 Frame 8 回答报文 Authoritative nameservers 区域中的（zhengjianting.com，aragorn.ns.cloudflare.com，NS）就是一条 NS 记录。
- Type == CNAME，则 Value 是别名为 Name 的主机对应的规范主机名，如（zhengjianting.com，www.zhengjianting.com，CNAME）就是一条 CNAME 记录。

## DNS 提供的服务

- 主机别名
- 邮件服务器别名
- 负载均衡，不难发现，上述 Wireshark 抓包得到的 Frame 8 回答报文中，将域名 zhengjianting.com 以及权威 DNS 服务器 aragorn.ns.cloudflare.com，joselyn.ns.cloudflare.com 映射到了一个 IP 地址集合，通常选取 IP 地址集合中的第一个 IP 地址，以域名 zhengjianting.com 为例，该站点被部署到了多个 Cloudflare CND 节点，因此 DNS 在这些节点之间循环分配负载。

## 一些实用的命令

```shell
# 以下命令仅在 win 10 实践过
ipconfig /all # 查看网络配置信息
ipconfig /displaydns # 查看 dns 系统缓存
nslookup your_domain # 域名映射为 IP
tracert hostname # 跟踪路由信息，Linux 中为 traceroute hostname
```
