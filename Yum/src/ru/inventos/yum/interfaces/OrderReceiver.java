package ru.inventos.yum.interfaces;

import ru.inventos.yum.OrderItem;

public interface OrderReceiver {
	public void receiveOrders(OrderItem[] orders);
}
