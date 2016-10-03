#socket连接中断后 再去获取读写流报SocketException
java.net.SocketException: Socket is closed
	at java.net.Socket.getOutputStream(Socket.java:943)
accept: Socket[addr=/127.0.0.1,port=55262,localport=8888]
	at server.TestServer1.main(TestServer1.java:19)

