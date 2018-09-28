1.I/O的五种模型：
	同步、异步、阻塞、非阻塞、多路复用
2.传统IO与NIO的区别
			IO				|				NIO
	面向流(Stream Oriented)	|	面向缓冲区(Buffer Oriented)
	阻塞IO(Blocking IO)		|	非阻塞IO(Non Blocking IO)
	(无)					|	选择器(Selectors)
3.NIO主要有三个核心部分组成：
	buffer缓冲区
	Channel管道
	Selector选择器
	在NIO中是以buffer缓冲区和Channel管道配合使用来处理数据的。即通过Channel管道运输着存储数据的Buffer缓冲区来实现数据的处理。

