package org.kteam.palm.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.util.Base64;


public class ObjectUtil {

	public static String objectToString(Object object) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream obj = null;
		try {
			obj = new ObjectOutputStream(baos);
			obj.writeObject(object);
			return new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object stringToObject(String string) {
		byte[] bsByte = Base64.decode(string, Base64.DEFAULT);
		ByteArrayInputStream bais = new ByteArrayInputStream(bsByte);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {

		}
		return null;
	}
}
