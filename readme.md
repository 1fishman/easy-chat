# easyChat

一个简易的聊天室,使用Netty实现,包含登录注册功能,包含心跳检测机制,服务器端发送心跳检测包,使用Netty自带的Idle来进行发送心跳包,如果
每个一段时间服务器向客户端发送心跳包,客户端收到会回复,如果服务器超过一段时间没有收到客户端的应答,则认为掉线,关闭掉Socket连接,释放资源.
反之如果客户端一段时间没有收到服务器发送的消息,那么就会断开连接,知道重新登录.

所有的登录用户保存在一个concurrentHashMap结构中,用来保证线程安全.并且提升并发量.

有list功能,也就是展示最近的聊天记录,所以会将发送的消息都持久化到数据库中,因此新建了一个线程池专门做对消息的持久化过程.
对于这个功能我的设计思路是每次有消息的时候先将消息存放到Redis缓存中,使用一个list来存储,并且开一个定时任务,每2秒刷新一次缓存,从
缓存中获取所有的message,然后批量插入到数据库中. 使用mybatis来对数据库进行插入. 使用mybatis的原因就是和spring整合特别方便并且也很好用.  

这里我考虑到一个问题,就是在插入缓存和从缓存中弹出的时候有个问题,一边插入一边删除会导致可能在删除的时候有删除掉刚插入缓存的数据.
因此使用了读写锁, lpush操作都用读锁,因为他们没有必要竞争,因此可以很好的操作. 但是对于del()操作,使用写锁,保证在删除缓存的时候没有任何其他线程在插入缓存数据,保证数据的一致性.


对于客户端有维护几种状态, 目前是登录中,注册中和正常状态.用来保证登陆成功的类型.进程间通信使用的是countdownLatch.来保证发送消息的状态一定是登录成功的状态.


因为没有用HTTP请求来做命令的实现,因此要在登录的时候阻塞客户端发送数据,使用countdownLatch来判断,只有收到登录成功才会让客户端进行发送消息.

主要用到的技术: springboot,Netty,mybatis和redis.
springboot用到依赖注入和简化配置. mybatis是因为比较好用,和spring能够很好的继承,写的代码量比较少
Netty就不用多说了,核心. redis缓存主要用在发送的消息缓存. 相当于用一个redis做一个消息队列,然后后台跑一个线程来进行将redis中的消息进行插库的操作.



# 消息类型
首先发送消息:　有两种消息,一种是心跳包,一种是正常包. 

## 正常包
type 代表类型, 1代表命令,0代表发送消息
toUser 如果为null, 服务端认为是群发, 客户端认为这条消息是群里过来的
fromUser, 必须不为空. 客户端会强制要求

commond 枚举,有几个命令. 首先是登录和退出 . 
- exit 退出命令
- list 展示出最近的20条消息, 可以使用list:用户名,展示和此用户的最近的聊天记录
- login 登录,参数为地址:端口 用户名 密码
- register 注册, 参数为 register 地址:端口 用户名 密码

## 心跳包
type 表示是请求还是应答,1为请求,2为应答
还有一个时间戳,用来记录请求和应答事件.

# client使用
命令上面说过了,正常发送消息直接发送就好了,默认是在聊天室中发送消息. 
支持单发消息,使用@用户名:消息内容发送.  例如;
@Alice:hello,world.

注意login和register命令是从命令行参数中获取到到的,也就是添加main方法的启动参数里面.

