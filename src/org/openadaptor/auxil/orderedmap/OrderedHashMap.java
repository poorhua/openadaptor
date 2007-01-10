/*
 * [[
 * Copyright (C) 2001 - 2006 The Software Conservancy as Trustee. All rights
 * reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Nothing in this notice shall be deemed to grant any rights to
 * trademarks, copyrights, patents, trade secrets or any other intellectual
 * property of the licensor or any contributor except as expressly stated
 * herein. No patent license is granted separate from the Software, for
 * code that you delete from the Software, or for combinations of the
 * Software with other software or hardware.
 * ]]
 */
package org.openadaptor.auxil.orderedmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openadaptor.core.exception.ComponentException;

/**
 * HashMap based implementation of <code>IOrderedMap</code>.
 * <p>
 * This class extends <code>HashMap</code> to support the requirements of <code>IOrderedMap</code>, so that the
 * stored data can be views in an ordered <code>List</code>-like fashion.
 * <p>
 * Notes: It makes no attempt to catch auto-generated key conflicts with real key
 * 
 * @author Eddy Higgins
 * @author Kevin Scully
 */
public class OrderedHashMap extends HashMap implements IOrderedMap {

  private static final long serialVersionUID = 1L;

  // At the time of writing, the autogenerated key prefix is not configurable.
  private String _autoKeyPrefix = "_auto_";

  private int _nextKey = 1;

  /**
   * This contains the keys in the map, in order.
   */
  private List _orderedKeys;

  // Constructors

  /**
   * Constructs an empty <code>OrderedHashMap</code> with the specified initial capacity and load factor.
   * <p>
   * It creates an underlying HashMap with specified parameters.
   * 
   * @param initialCapacity
   *          initialCapacity
   * @param loadFactor
   *          loadFactor
   * @see HashMap
   */
  public OrderedHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
    _orderedKeys = new ArrayList(initialCapacity);
  }

  /**
   * Constructs an empty <code>OrderedHashMap</code> with the specified initial capacity and <code>HashMap</code>'s
   * default load factor.
   * <p>
   * It creates an underlying HashMap with specified initial capacity
   * 
   * @param initialCapacity
   *          initialCapacity
   * @see HashMap
   */
  public OrderedHashMap(int initialCapacity) {
    super(initialCapacity);
    _orderedKeys = new ArrayList(initialCapacity);
  }

  /**
   * Constructs an empty <code>OrderedHashMap</code> with initial capacity & load factor as per <code>HashMapM</code>.
   */
  public OrderedHashMap() {
    super();
    _orderedKeys = new ArrayList();
  }

  /**
   * Constructs a new <code>OrderedHashMap</code> with the same mappings and orderings as the specified IOrderdMap.
   * 
   * @param map
   *          an IOrderedMap instance
   */
  public OrderedHashMap(IOrderedMap map) {
    this(map.size());
    List keys = map.keys();
    for (int i = 0; i < keys.size(); i++) {
      Object key = keys.get(i);
      put(key, map.get(key));
    }
  }

  /**
   * Constructs a new <code>OrderedHashMap</code> with the same mappings as the supplied Map.
   * <p>
   * The ordering is determined by the result of keySet() on the map.
   * 
   * @param map
   *          any <code>Map</code> instance.
   */
  public OrderedHashMap(Map map) {
    super(map);
    _orderedKeys = new ArrayList(keySet());
  }

  // List-like constructor.

  /**
   * Constructs a new <code>OrderedHashMap</code> with the values in the supplied collection.
   * <p>
   * Each entry will have an auto-generated key value.
   * 
   * @param collection
   *          any <code>Collection</code> instance.
   */
  public OrderedHashMap(Collection collection) {
    this(collection.size());
    int size = collection.size(); // Can't do on previous line - 'this' has to be first stmt.
    _orderedKeys = new ArrayList(size);
    Iterator it = collection.iterator();
    while (it.hasNext()) {
      add(it.next());
    }
  }

  // BEGIN methods applying to Map only (only affected methods)

  /**
   * Associates the specified value with the specified key in this map.
   * <p>
   * If the map previously contained a mapping for this key, the old value is replaced by the specified value.
   * <p>
   * For ordering purposes, the entry goes at the end of the existing entries, unless a mapping previously existed for
   * it, in which case existing order is preserved. (A map m is said to contain a mapping for a key k if and only if
   * m.containsKey(k) would return true.))
   * 
   * @param key
   *          which must not be null.
   * @param value
   * @return The previous value associated with the key, or null if there was no value associated or that value was
   *         itself null.
   * @throws NullPointerException
   *           if the supplied key is null
   * @see java.util.HashMap
   */
  public Object put(Object key, Object value) throws NullPointerException {
    /* issue #11 */
    if (key == null) {
      throw new NullPointerException("<null> key value not permitted");
    }
    if (!containsKey(key)) {
      _orderedKeys.add(key);
    } // This key is next in list.
    return super.put(key, value);
  }

  /**
   * Removes the mapping for this key from this <code>OrderedHashMap</code> if it is present.
   * <p>
   * More formally, if this map contains a mapping from key k to value v such that (key==null ? k==null :
   * key.equals(k)), that mapping is removed. (The map can contain at most one such mapping).
   * <p>
   * Returns the value to which the map previously associated the key, or null if the map contained no mapping for this
   * key. (A null return can also indicate that the map previously associated null with the specified key if the
   * implementation supports null values.) The map will not contain a mapping for the specified key once the call
   * returns.
   * <p>
   * The ordering is appropriately adjusted assuming the map contained the key.
   * 
   * @param key
   * @return The previous value associated with the key, or null if there was no value associated or that value was
   *         itself null.
   */
  public Object remove(Object key) {
    if (containsKey(key)) {
      _orderedKeys.remove(key);
    }
    return super.remove(key);
  }

  /**
   * Returns a collection view of the values contained in this <code>OrderedHashMap</code>.
   * <p>
   * The ordering of the values is preserved.
   * 
   * @return Ordered list of the values in the collection.
   */
  public Collection values() {
    List values = new ArrayList();
    Iterator it = _orderedKeys.iterator();
    while (it.hasNext()) {
      values.add(get(it.next()));
    }
    return values;
  }

  // END methods applying to Map only (only affected methods)
  // BEGIN methods applying to both Map and List

  /**
   * Remove all the mappings stored in this <code>OrderedHashMap</code>.
   */
  public void clear() {
    super.clear();
    _orderedKeys.clear();
  }

  // END methods applying to both Map and List

  // BEGIN List only methods

  /**
   * Add the specified element to the <code>OrderedHashMap</code> after the last existing element.
   * <p>
   * An auto-generated key is associated with the element.
   * 
   * @param object
   *          the element to add
   * @return true as per the general contract of <code>Collection</code>.add
   */
  public boolean add(Object object) {
    put(nextAutoKey(), object);
    return true; // Same as List interface!
  }

  /**
   * Returns the element at the specified position in this <code>OrderedHashMap</code>.
   * 
   * @param i
   *          index of the element to return
   * @return the element at the specificd position in the <code>OrderedHashMap</code>
   * @throws IndexOutOfBoundsException -
   *           if index out of range (index < 0 || index >= size()).
   */
  public Object get(int i) {
    return get(_orderedKeys.get(i));
  }

  /**
   * Replaces the element at the specified position in this <code>OrderedHashMap</code> with the specified element.
   * 
   * @param i
   *          index of element to replace
   * @param object
   *          element to be stored at the specified position
   * @return the element previously stored at the position.
   * @throws IndexOutOfBoundsException
   *           if index out of range (index < 0 || index >= size()).
   */
  public Object set(int i, Object object) {
    Object key = _orderedKeys.get(i);
    Object result = get(i);// Retrieve old value (that's what a <code>List</code> does).
    put(key, object);
    return result;
  }

  /**
   * Inserts the specified mapping (key->value) at the specified position in this <code>OrderedHashMap</code>.
   * <p>
   * Shifts the element currently at that position (if any) and any subsequent elements to the right (adds one to their
   * indices).
   * 
   * @param i
   *          index at which the specified element is to be inserted.
   * @param key
   *          the key to be associated with the element.
   * @param value
   *          element to be inserted.
   * @throws IndexOutOfBoundsException
   *           if index out of range (index < 0 || index >= size()).
   */
  public void add(int i, Object key, Object value) {
    _orderedKeys.add(i, key);
    super.put(key, value);
  }

  /**
   * Inserts the specified element at the specified position in this <code>OrderedHashMap</code>.
   * <p>
   * Shifts the element currently at that position (if any) and any subsequent elements to the right (adds one to their
   * indices). Generates a mapping for the element with an autogenerated key.
   * 
   * @param i
   *          index at which the specified element is to be inserted.
   * @param value
   *          element to be inserted.
   * @throws IndexOutOfBoundsException
   *           if index out of range (index < 0 || index >= size()).
   */
  public void add(int i, Object value) {
    add(i, nextAutoKey(), value);
  }

  /**
   * Removes the element at the specified position in this <code>OrderedHashMap</code>.
   * <p>
   * Shifts any subsequent elements to the left (subtracts one from their indices).
   * 
   * @param i
   *          index of the element to be removed.
   * @return the element that was removed
   * @throws IndexOutOfBoundsException
   *           if index out of range (index < 0 || index >= size()).
   */
  public Object remove(int i) {
    Object key = _orderedKeys.remove(i);
    return remove(key);
  }

  /**
   * Returns the index in this <code>OrderedHashMap</code> of the specified element, or -1 if this
   * <code>OrderedHashMap</code> does not contain this element.
   * 
   * @param object
   *          element to search for.
   * @return the index in this <code>OrderedHashMap</code> of the occurrence of the specified element, or -1 if this
   *         <code>OrderedHashMap</code> does not contain this element.
   */

  public int indexOf(Object object) {
    int result = -1;
    if (containsValue(object)) { // Need to find it's index.
      int size = _orderedKeys.size(); // Dereference for speed in loop
      for (int index = 0; index < size; index++) {
        if (object.equals(get(index))) {
          result = index;
          break;
        }
      }
    }
    return result;
  }

  /**
   * Returns an ordered <code>List</code> of the keys for this <code>OrderedHashMap</code>.
   * 
   * @return <code>List</code> containing the keys for this <code>OrderedHashMap</code>, in their assigned order.
   */
  public List keys() {
    return _orderedKeys;
  }

  // END List only methods

  /**
   * Return next auto-generated key (for anonymous lists etc.)
   * <p>
   * <b>Note:</b> it makes no attempt to check that the key doesn't conflict with an existing one.
   * 
   * @return String containing the next auto key.
   */
  private String nextAutoKey() {
    return (_autoKeyPrefix + _nextKey++);
  }

  /**
   * Provides a string representation of this <code>OrderedHashMap</code>.
   * <p>
   * The order of the elements is preserved.
   * 
   * @return string representation of this <code>OrderedHashMap</code>.
   */
  public String toString() {
    StringBuffer sb = new StringBuffer("OM[");
    for (int i = 0; i < _orderedKeys.size(); i++) {
      String key = (String) _orderedKeys.get(i);
      Object value = get(key);
      if (value instanceof Object[]) {// Be nice - expand it
        StringBuffer sb2 = new StringBuffer();
        Object[] values = (Object[]) value;
        for (int j = 0; j < values.length; j++) {
          Object tmpVal = values[j];
          sb2.append((tmpVal == null) ? "<null>" : tmpVal);
          if (j < values.length - 1) {
            sb2.append(",");
          }
        }
        value = sb2.toString();
      }
      sb.append(key).append("->").append(value).append(',');
    }
    if (sb.length() > 0) {
      sb.setCharAt(sb.length() - 1, ']');
    }
    return sb.toString();
  }

  // BEGIN IOrderedMap only

  /**
   * Return a shallow copy of this implementation of <code>OrderedHashMap</code>.
   * <p>
   * The copy preserves the correct key order. Keys and Values are not copied. We don't call super.clone() as
   * <code>HashMap</code>'s clone() method is not correct with respect to IOrderedMap implementations.
   * <p>
   * 
   * @return Object a clone of this <code>OrderedHashMap</code>
   */
  public Object clone() {
    IOrderedMap clone;
    try {
      // We do this to ensure that subclasses can call super.clone() and
      // still create instances of the correct subclass.
      clone = (IOrderedMap) getClass().newInstance();
    } catch (InstantiationException e) {
      throw new ComponentException("Failed to instantiate OrderedHashMap clone.", e, null);
    } catch (IllegalAccessException e) {
      throw new ComponentException("Failed to instantiate OrderedHashMap clone.", e, null);
    }
    for (int i = 0; i < keys().size(); i++) {
      Object key = keys().get(i);
      clone.put(key, get(key));
    }
    return clone;
  }

  // Implementation of ISimpleRecord methods.
  /**
   * Return the actual Record which is being viewed as an ISimpleRecord.
   * <p>
   * As <code>OrderedHashMap</code> implements IOrderedMap (which, in turn, implements ISimpleRecord) it can simply
   * return <tt>this</tt>
   * 
   * @return the <code>OrderedHashMap</code> instance itself, i.e. 'this'
   */
  public Object getRecord() {
    return this;
  }
}
