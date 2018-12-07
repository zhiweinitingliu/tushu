package com.dukang.tushu.service.utils;
 
import java.util.HashMap;
import java.util.Map;
 
public class ResponseData {
	
	public static final String ERRORS_KEY = "errors";
	
	private final int code;
	private final String message;
	private final Map<String, Object> data = new HashMap<>();
 
	public String getMessage() {
		return message;
	}
 
	public int getCode() {
		return code;
	}
 
	public Map<String, Object> getData() {
		return data;
	}
	
	public ResponseData putDataValue(String key, Object value) {
		data.put(key, value);
		return this;
	}
	
	private ResponseData(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public static ResponseData ok() {
		return new ResponseData(200, "Ok");
	}
	
	public static ResponseData notFound() {
		return new ResponseData(404, "Not Found");
	}
	
	public static ResponseData badRequest(String errorMsg) {
		return new ResponseData(400, errorMsg);
	}
	
	public static ResponseData forbidden() {
		return new ResponseData(403, "Forbidden");
	}
	
	public static ResponseData unauthorized() {
		return new ResponseData(401, "unauthorized");
	}
	
	public static ResponseData serverInternalError() {
		return new ResponseData(500, "Server Internal Error");
	}
	
	public static ResponseData customerError() {
		return new ResponseData(1001, "Customer Error");
	}
}
