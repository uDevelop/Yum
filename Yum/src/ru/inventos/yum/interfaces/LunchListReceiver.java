package ru.inventos.yum.interfaces;

import ru.inventos.yum.LunchItem;

public interface LunchListReceiver {
	public void receiveLunchList(LunchItem[] items);
}
