package com.snapmeds;

public interface PersistenceHandler {
	public void reorderItems(int from, int to);
	public void removeItem(int pos);
	public Object getItem(int pos);
}
