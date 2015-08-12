package com.example.coolweather;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	
	public static final String DB_NAME = "cool_weather";
	
	
	public static final int VERSION = 1;
	
	private static CoolWeatherDB coolWeatherDB;
	
	private SQLiteDatabase db;
	
	// 将构造方法私有化
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	// 获取db实例
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	
	// 将province实例存储到数据库
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	// 从数据库读取全国所有的省份信息
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}
	
	// 将city实例存储到数据库
		public void saveCity(City city) {
			if (city != null) {
				ContentValues values = new ContentValues();
				values.put("city_name", city.getCityName());
				values.put("city_code", city.getCityCode());
				values.put("province", city.getProvinceId());
				db.insert("City", null, values);
			}
		}
		
		// 从数据库读取某省里的所偶城市的信息
		public List<City> loadCities(int provinceId) {
			List<City> list = new ArrayList<City>();
			Cursor cursor = db.query("City", null, "province_id = ?", new String[] {String.valueOf(provinceId)}, null, null, null);
			if (cursor.moveToFirst()) {
				do {
					City city= new City();
					city.setId(cursor.getInt(cursor.getColumnIndex("id")));
					city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
					city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
					city.setProvinceId(provinceId);
					list.add(city);
				} while (cursor.moveToNext());
			}
			if (cursor != null) {
				cursor.close();
			}
			return list;
		}
		
		// 将county实例存储到数据库
				public void saveCounty(County county) {
					if (county != null) {
						ContentValues values = new ContentValues();
						values.put("county_name", county.getCountyName());
						values.put("county_code", county.getCountyCode());
						values.put("county", county.getCityId());
						db.insert("County", null, values);
					}
				}
				
				// 从数据库读取某省里的所偶城市的信息
				public List<County> loadCounties(int cityId) {
					List<County> list = new ArrayList<County>();
					Cursor cursor = db.query("City", null, "city_id = ?", new String[] {String.valueOf(cityId)}, null, null, null);
					if (cursor.moveToFirst()) {
						do {
							County county= new County();
							county.setId(cursor.getInt(cursor.getColumnIndex("id")));
							county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
							county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
							county.setCityId(cityId);
							list.add(county);
						} while (cursor.moveToNext());
					}
					if (cursor != null) {
						cursor.close();
					}
					return list;
				}
		
}
