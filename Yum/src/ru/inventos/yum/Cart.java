package ru.inventos.yum;

import java.util.ArrayList;
import java.util.HashMap;

import ru.inventos.yum.interfaces.Updatable;

public class Cart {
	private static ArrayList<CartItem> sItems;
	private static ArrayList<Updatable> sDataListeners;
	private static ArrayList<String> sCategories;
	
	public Cart() {
		if (sItems == null) {
			sItems = new ArrayList<CartItem>();
		}
		if (sDataListeners == null) {
			sDataListeners = new ArrayList<Updatable>();
		}
		if (sCategories == null) {
			sCategories = new ArrayList<String>();
		}
	}
	
	private boolean contains(String name) {
		for(CartItem item : sItems ) {
			if (item.name.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	private void delete(int id) {
		int count = sItems.size();
		CartItem item;
		for(int i = 0; i < count; i++) {
			item = sItems.get(i);
			if (item.id == id) {
				sItems.remove(i);
				sCategories.remove(item.category);
				notifyDataSetChanged();
				return;
			}
		}
	}
	
	public void add(int id, String name, float price, int weight, String category) {
		if (!contains(name)) {
			CartItem item = new CartItem();
			item.id = id;
			item.name = name;
			item.price = price;
			item.count = 1;
			item.weight = weight;
			item.category = category;
			sItems.add(item);
			notifyDataSetChanged();
			sCategories.add(category);			
		}		
	}
	
	public void setCount(int id, int count) {
		if (count == 0) {
			delete(id);			
		}
		else {
			for(CartItem item : sItems) {
				if (item.id == id) {
					item.count = count;
					notifyDataSetChanged();
					return;
				}
			}			
		}		
	}
	
	public int getCountByName(String name) {
		for(CartItem item : sItems) {
			if (item.name.equals(name)) {
				return item.count;
			}
		}
		return -1;
	}
	
	public CartItem getItem(int id) {
		for(CartItem item : sItems) {
			if (item.id == id) {
				return item;
			}
		}
		return null;		
	}
	
	public boolean containsCategory(String name) {
		for(String category: sCategories) {
			if (category.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public CartItem getItemByIndex(int index) {
		return sItems.get(index);
	}
	
	public int getCount() {
		return sItems.size();
	}
	
	public float getTotalPrice() {
		float result = 0;
		for (CartItem item : sItems) {
			result = result + item.price * item.count;
		}
		return result;
	}
	
	public void clear() {
		sItems.clear();
		sCategories.clear();
		notifyDataSetChanged();
	}
	
	public void registerDataListener(Updatable listener) {
		sDataListeners.add(listener);
	}
	
	public void unregisterDataListener(Updatable listener) {
		if (sDataListeners.contains(listener)) {
			sDataListeners.remove(listener);
		}		
	}
	
	private void notifyDataSetChanged() {
		for(Updatable listener: sDataListeners) {
			listener.update();
		}
	}
	
	public CartItem[] getArray() {
		return sItems.toArray(new CartItem[sItems.size()]);
	}
	
	
}
