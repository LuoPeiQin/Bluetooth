package com.luo.bluetooth.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tony.luopeiqin on 2017/9/25.
 */

public class GsonUtils {

	public static boolean isPrintException = true;

	private static Gson gson = new GsonBuilder()
			  .setDateFormat("yyyy-MM-dd HH:mm:ss")
			  .create();  ;

	private GsonUtils() {
		throw new Error("Do not need instantiate!");
	}

	private static final SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Json数组转对象链表
	 *
	 * @param jsonArray
	 * @param cls
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> string2Objects(String jsonArray, Class<T> cls) {
		List<T> res = new LinkedList<T>();
		//Json的解析类对象
	    JsonParser parser = new JsonParser();
	    //将JSON的String 转成一个JsonArray对象
	    JsonArray array = parser.parse(jsonArray).getAsJsonArray();
		for (int i = 0; i < array.size(); i++) {
			T t = gson.fromJson(array.get(i).toString(), cls);
			// T t= JSONUtils.string2Obejct(array.get(i).toString(),cls);
			res.add(t);
		}
		return res;
	}

	/**
	 * 从java对象转换为字符串
	 */
	public static String Object2String(Object obj) {
		return gson.toJson(obj);
	}

	/**
	 * 从字符串转换为java对象
	 */
	public static <T> T string2Object(String jsonStr, Class<T> c) {
		if (jsonStr.startsWith("[") && jsonStr.endsWith("]")) {
			jsonStr = jsonStr.replace('[', '{').replace(']', '}');
		}
		T t = gson.fromJson(jsonStr, c);
		return t;
	}

	public static String objToJson(Object obj) {
		return gson.toJson(obj);
	}

}
