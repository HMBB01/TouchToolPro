package top.bogey.touch_tool_pro.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTouch;

public class GsonUtils {
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Pin.class, new Pin.PinDeserialize())
            .registerTypeAdapter(PinObject.class, new PinObject.PinObjectDeserializer())
            .registerTypeAdapter(PinTouch.class, new PinTouch.PinTouchSerializer())
            .registerTypeAdapter(Action.class, new Action.ActionDeserializer())
            .registerTypeAdapter(FunctionContext.class, new FunctionContext.FunctionContextDeserializer())
            .create();

    public static <T> T copy(T object, Class<T> tClass) {
        String json = gson.toJson(object);
        return gson.fromJson(json, tClass);
    }

    public static <T> T copy(T object, Type type) {
        String json = gson.toJson(object);
        return gson.fromJson(json, type);
    }

    public static String toJson(Object o) {
        return gson.toJson(o);
    }

    public static <T> T getAsObject(JsonObject jsonObject, String key, Class<T> tClass, T defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null) return gson.fromJson(element, tClass);
        return defaultValue;
    }

    public static <T> T getAsObject(String json, Class<T> tClass, T defaultValue) {
        if (json != null) return gson.fromJson(json, tClass);
        return defaultValue;
    }

    public static <T> T getAsObject(JsonObject jsonObject, String key, Type type, T defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null) return gson.fromJson(element, type);
        return defaultValue;
    }

    public static <T> T getAsObject(String json, Type type, T defaultValue) {
        if (json != null) return gson.fromJson(json, type);
        return defaultValue;
    }

    public static int getAsInt(JsonObject jsonObject, String key, int defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null) return element.getAsInt();
        return defaultValue;
    }

    public static long getAsLong(JsonObject jsonObject, String key, long defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null) return element.getAsLong();
        return defaultValue;
    }

    public static float getAsFloat(JsonObject jsonObject, String key, float defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null) return element.getAsFloat();
        return defaultValue;
    }

    public static String getAsString(JsonObject jsonObject, String key, String defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null) return element.getAsString();
        return defaultValue;
    }

    public static boolean getAsBoolean(JsonObject jsonObject, String key, boolean defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null) return element.getAsBoolean();
        return defaultValue;
    }
}
