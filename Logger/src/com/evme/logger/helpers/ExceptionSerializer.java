package com.evme.logger.helpers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * This serializer helps GSON to handle objects of Exception types. Since this
 * is a know issue (https://code.google.com/p/google-gson/issues/detail?id=469)
 * our internal implementation should be used.
 * 
 * @author sromku
 * 
 */
public class ExceptionSerializer implements JsonSerializer<Throwable> {

	@Override
	public JsonElement serialize(Throwable src, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject jsonObject = new JsonObject();
		jsonObject.add("cause", new JsonPrimitive(String.valueOf(src.getCause())));
		jsonObject.add("message", new JsonPrimitive(src.getMessage()));
		return jsonObject;

	}

}
