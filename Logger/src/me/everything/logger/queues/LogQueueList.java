package me.everything.logger.queues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import me.everything.logger.Log.LogEntry;


public class LogQueueList implements List<LogEntry> {

	private List<LogEntry> logEntries = new ArrayList<LogEntry>();

	@Override
	public boolean add(LogEntry object) {
		logEntries.add(object);
		return true;
	}

	@Override
	public void add(int location, LogEntry object) {
		throw new RuntimeException("not supported");
	}

	@Override
	public boolean addAll(Collection<? extends LogEntry> arg0) {
		throw new RuntimeException("not supported");
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends LogEntry> arg1) {
		throw new RuntimeException("not supported");
	}

	@Override
	public void clear() {
		logEntries.clear();
	}

	@Override
	public boolean contains(Object object) {
		return logEntries.contains(object);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return logEntries.containsAll(arg0);
	}

	@Override
	public LogEntry get(int location) {
		return logEntries.get(location);
	}

	@Override
	public int indexOf(Object object) {
		return logEntries.indexOf(object);
	}

	@Override
	public boolean isEmpty() {
		return logEntries.isEmpty();
	}

	@Override
	public Iterator<LogEntry> iterator() {
		return logEntries.iterator();
	}

	@Override
	public int lastIndexOf(Object object) {
		return logEntries.lastIndexOf(object);
	}

	@Override
	public ListIterator<LogEntry> listIterator() {
		return logEntries.listIterator();
	}

	@Override
	public ListIterator<LogEntry> listIterator(int location) {
		return logEntries.listIterator(location);
	}

	@Override
	public LogEntry remove(int location) { 
		return logEntries.remove(location);
	}

	@Override
	public boolean remove(Object object) {
		return logEntries.remove(object);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return logEntries.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return logEntries.removeAll(arg0);
	}

	@Override
	public LogEntry set(int location, LogEntry object) {
		return logEntries.set(location, object);
	}

	@Override
	public int size() {
		return logEntries.size();
	}

	@Override
	public List<LogEntry> subList(int start, int end) {
		return logEntries.subList(start, end);
	}

	@Override
	public Object[] toArray() {
		return logEntries.toArray();
	}

	@Override
	public <T> T[] toArray(T[] array) {
		return logEntries.toArray(array);
	}

}
