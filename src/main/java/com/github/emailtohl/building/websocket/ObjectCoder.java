package com.github.emailtohl.building.websocket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * 为websocket提供序列化支持，可在@ServerEndpoint的参数encoders和decoders中声明继承了本类的类
 * 
 * @author HeLei
 * @date 2016.06.11
 *
 * @param <T extends Serializable> 被序列化的类
 */
public abstract class ObjectCoder<T extends Serializable> implements Encoder.BinaryStream<T>, Decoder.BinaryStream<T> {
	/**
	 * 持有对象的class
	 */
	protected final Class<T> clazz;
	
	@SuppressWarnings("unchecked")
	public ObjectCoder() {
		Class<T> temp = null;
		Class<?> clz = this.getClass();
		while (clz != ObjectCoder.class) {
			Type genericSuperclass = clz.getGenericSuperclass();
			if (genericSuperclass instanceof ParameterizedType) {
				ParameterizedType type = (ParameterizedType) genericSuperclass;
				Type[] arguments = type.getActualTypeArguments();
				if (arguments != null && arguments.length > 0 && arguments[0] instanceof Class) {
					temp = (Class<T>) arguments[0];
					break;
				}
			}
			clz = clz.getSuperclass();
		}
		if (temp == null) {
			throw new IllegalStateException("未获取持有对象（被解析序列化的对象）的class");
		} else {
			clazz = temp;
		}
	}
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void init(EndpointConfig config) {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public T decode(InputStream in) throws DecodeException, IOException {
		T obj = null;
		try (ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in))) {
			obj = (T) oin.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public void encode(T obj, OutputStream out) throws EncodeException, IOException {
		try (ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(out))) {
			oout.writeObject(obj);
		}
	}
}