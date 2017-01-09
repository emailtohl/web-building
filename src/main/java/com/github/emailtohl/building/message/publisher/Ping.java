package com.github.emailtohl.building.message.publisher;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 接收ClusterManager的连接
 * （1）当spring的上下文初始化或刷新时，会触发ContextRefreshedEvent，这时候就发起连接到本服务地址上；
 * （2）经过短暂的响应后ClusterManager就会将自身的地址通过socket发到广播地址上；
 * （3）ClusterManager的listener属性是一个线程，它也使用socket监听广播地址上的消息；
 * （4）如果是自己的地址就忽略，否则就创建一个websocket连接，并将该websocket连接注册到ClusterEventMulticaster中；
 * （5）当有ClusterEvent发生时，就会触发ClusterEventMulticaster的multicastEvent，这时就通过websocket将消息发送到各个节点上。
 * 
 * @author HeLei
 */
@WebServlet("/ping")
public class Ping extends HttpServlet {
	private static final long serialVersionUID = 1048508760045030519L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Content-Type", "text/plain;charset=UTF-8");
		response.setStatus(200);
		response.getWriter().print(ClusterManager.RESPONSE_OK);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
