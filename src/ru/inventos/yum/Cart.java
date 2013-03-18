package ru.inventos.yum;

import java.util.ArrayList;

public class Cart {
	private static ArrayList<CartItem> sItems;
	
	public Cart() {
		if (sItems == null) {
			sItems = new ArrayList<CartItem>();
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
				return;
			}
		}
	}
	
	public void add(int id, String name, float price) {
		if (!contains(name)) {
			CartItem item = new CartItem();
			item.id = id;
			item.name = name;
			item.price = price;
			item.count = 1;
			sItems.add(item);
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
}
