package org.yupeng.redis;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Redis abstraction interface
 * @author: yupeng
 **/
public interface RedisCache {

    /**
     * Get string object
     *
     * @param redisKeyBuild   RedisKeyBuild
     * @param clazz class object
     * @param <T>   T
     * @return T ordinary object
     */
    <T> T get(RedisKeyBuild redisKeyBuild, Class<T> clazz);
    
    /**
     * Get the string object (if it does not exist in the cache, execute the given supplier interface)
     *
     * @param redisKeyBuild   RedisKeyBuild
     * @param clazz class object
     * @param <T>   T
     * @param supplier The logic executed when the cache is empty
     * @param ttl expiration time
     * @param timeUnit time unit
     * @return T ordinary object
     */
    <T> T get(RedisKeyBuild redisKeyBuild, Class<T> clazz, Supplier<T> supplier, long ttl, TimeUnit timeUnit);

    /**
     * Returns the subcharacter of the string value in key
     * @param redisKeyBuild cache key
     * @param start start
     * @param end end
     * @return result
     */
    String getRange(RedisKeyBuild redisKeyBuild, long start, long end);

    /**
     * Get the string object, and the string is the collection content
     *
     * @param redisKeyBuild cache key
     * @param clazz type
     * @param <T> specifies the generic type
     * @return List<T>
     */
    <T> List<T> getValueIsList(RedisKeyBuild redisKeyBuild, Class<T> clazz);
    
    /**
     * Get the string object, and the string is the collection content (if it does not exist in the cache, the given supplier interface is executed)
     *
     * @param redisKeyBuild cache key
     * @param clazz type
     * @param <T> specifies the generic type
     * @param supplier The logic executed when the cache is empty
     * @param ttl expiration time
     * @param timeUnit time unit
     * @return List<T>
     */
    <T> List<T> getValueIsList(RedisKeyBuild redisKeyBuild, Class<T> clazz, Supplier<List<T>> supplier, long ttl, TimeUnit timeUnit);


    /**
     * Get multiple values ​​in batches through multiple keys
     *
     * @param keyList key collection
     * @return List<String>
     */
    List<String> getKeys(List<RedisKeyBuild> keyList);

    /**
     * Determine whether the key exists
     *
     * @param redisKeyBuild redisKeyBuild
     * @return exists or may be empty
     */
    Boolean hasKey(RedisKeyBuild redisKeyBuild);

    /**
     * Delete key
     *
     * @param redisKeyBuild cache key
     * @return
     */
    void del(RedisKeyBuild redisKeyBuild);


    /**
     * Delete keys in batches
     *
     * @param keys key collection
     */
    void del(Collection<RedisKeyBuild> keys);

    /**
     * Set key expiration time
     *
     * @param redisKeyBuild      RedisKeyBuild
     * @param ttl expiration time
     * @param timeUnit time unit
     * @return whether successful
     */
    Boolean expire(RedisKeyBuild redisKeyBuild, long ttl, TimeUnit timeUnit);

    /**
     * Get key timeout
     *
     * @param redisKeyBuild redisKeyBuild
     * @return timeout
     */
    Long getExpire(RedisKeyBuild redisKeyBuild);
    
    /**
     * Get key timeout
     *
     * @param redisKeyBuild redisKeyBuild
     * @param timeUnit time unit
     * @return timeout
     */
    Long getExpire(RedisKeyBuild redisKeyBuild,TimeUnit timeUnit);

    /**
     * Find matching key
     *
     * @param pattern cache key
     * @return keys
     */
    Set<String> keys(String pattern);

    /**
     * Move the key of the current database to the given database db
     *
     * @param redisKeyBuild cache key
     * @param dbIndex
     * @return
     */
    Boolean move(RedisKeyBuild redisKeyBuild, int dbIndex);

    /**
     * Remove the expiration time of the key and the key will be persisted
     *
     * @param redisKeyBuild cache key
     * @return
     */
    Boolean persist(RedisKeyBuild redisKeyBuild);

    /**
     * Returns a random key from the current database
     *
     * @return
     */
    String randomKey();

    /**
     * Modify key name
     *
     * @param oldKey cache key
     * @param newKey cache key
     */
    void rename(RedisKeyBuild oldKey, RedisKeyBuild newKey);

    /**
     * Rename oldKey to newKey only if newKey does not exist
     *
     * @param oldKey cache key
     * @param newKey cache key
     * @return
     */
    Boolean renameIfAbsent(RedisKeyBuild oldKey, RedisKeyBuild newKey);

    /**
     * Returns the type of value stored in key
     *
     * @param redisKeyBuild cache key
     * @return
     */
    DataType type(RedisKeyBuild redisKeyBuild);

    /**
     * Set up cache
     *
     * @param redisKeyBuild cache key
     * @param object cache object
     */
    void set(RedisKeyBuild redisKeyBuild, Object object);

    /**
     * Set up cache
     *
     * @param redisKeyBuild cache key
     * @param object cache object
     * @param ttl expiration time
     */
    void set(RedisKeyBuild redisKeyBuild, Object object, long ttl);

    /**
     * Set up cache
     *
     * @param redisKeyBuild cache key
     * @param object cache object
     * @param ttl expiration time
     * @param timeUnit time unit
     */
    void set(RedisKeyBuild redisKeyBuild, Object object, long ttl, TimeUnit timeUnit);

    /**
     * Set the value of key only if key does not exist
     *
     * @param redisKeyBuild cache key
     * @param object object
     * @return Returns false if it already exists, returns true if it does not exist
     */
    boolean setIfAbsent(RedisKeyBuild redisKeyBuild, Object object);
    
    /**
     * Set the value of key only if key does not exist
     *
     * @param redisKeyBuild cache key
     * @param object object
     * @param ttl expiration time
     * @param timeUnit time unit
     * @return Returns false if it already exists, returns true if it does not exist
     */
    boolean setIfAbsent(RedisKeyBuild redisKeyBuild, Object object, long ttl, TimeUnit timeUnit);

    /**
     * Get the length of a string
     *
     * @param redisKeyBuild cache key
     * @return length
     */
    Long size(RedisKeyBuild redisKeyBuild);

    /**
     * Add in batches
     *
     * @param map object
     */
    void multiSet(Map<RedisKeyBuild, ?> map);

    /**
     * Set one or more key-value pairs at the same time if and only if all given keys do not exist
     *
     * @param map object
     * @return Returns false if it already exists, returns true if it does not exist
     */
    boolean multiSetIfAbsent(Map<RedisKeyBuild, ?> map);

    /**
     * Increase (self-increasing), negative number is self-decreasing
     *
     * @param redisKeyBuild cache key
     * @param increment step size
     * @return
     */
    Long incrBy(RedisKeyBuild redisKeyBuild, long increment);

    /**
     * The double type increases (self-increasing), and the negative number is self-decreasing.
     * @param redisKeyBuild cache key
     * @param increment step size
     * @return
     */
    Double incrByDouble(RedisKeyBuild redisKeyBuild, double increment);

    /**
     * Append to the end
     *
     * @param redisKeyBuild cache key
     * @param value value
     * @return
     */
    Integer append(RedisKeyBuild redisKeyBuild, String value);

    /** ------------------Hash related operations-------------------------------- */

    /**
     * Place a key-value pair
     *
     * @param redisKeyBuild hash key
     * @param hashKey hash key
     * @param value   hash value
     */
    void putHash(RedisKeyBuild redisKeyBuild, String hashKey, Object value);

    /**
     * Place a key-value pair and set the expiration time
     *
     * @param redisKeyBuild hash key
     * @param hashKey       hash key
     * @param value         hash value
     * @param ttl expiration time
     */
    void putHash(RedisKeyBuild redisKeyBuild, String hashKey, Object value, long ttl);

    /**
     * Place a key-value pair and set the expiration time
     *
     * @param redisKeyBuild hash key
     * @param hashKey   hash key
     * @param value     hash value
     * @param ttl expiration time
     * @param timeUnit time unit
     */
    void putHash(RedisKeyBuild redisKeyBuild, String hashKey, Object value, long ttl, TimeUnit timeUnit);

    /**
     * Put all key-value pairs in the map
     *
     * @param redisKeyBuild key
     * @param map hash
     */
    void putHash(RedisKeyBuild redisKeyBuild, Map<String, ?> map);

    /**
     * Put all the key-value pairs in the map and set the expiration time
     *
     * @param redisKeyBuild key
     * @param map hash
     * @param ttl expiration time
     */
    void putHash(RedisKeyBuild redisKeyBuild, Map<String, ?> map, long ttl);

    /**
     * Put all the key-value pairs in the Map and set the expiration time and time unit
     *
     * @param redisKeyBuild key
     * @param map hash
     * @param ttl expiration time
     * @param timeUnit time unit
     */
    void putHash(RedisKeyBuild redisKeyBuild, Map<String, ?> map, long ttl, TimeUnit timeUnit);

    /**
     * Set only if hashKey does not exist
     *
     * @param redisKeyBuild cache key
     * @param hashKey key in hash
     * @param value object
     * @return
     */
    Boolean putHashIfAbsent(RedisKeyBuild redisKeyBuild, String hashKey, Object value);

    /**
     * Get ordinary objects from Hash
     *
     * @param redisKeyBuild     key
     * @param hashKey hash key
     * @param clazz class object
     * @param <T>     T
     * @return ordinary object
     */
    @SuppressWarnings("all")
    <T> T getForHash(RedisKeyBuild redisKeyBuild, String hashKey, Class<T> clazz);

    /**
     * The value of Hash is a collection of strings, which can be extracted
     *
     * @param redisKeyBuild     key
     * @param hashKey hash key
     * @param clazz class object
     * @param <T>     T
     * @return ordinary object
     */
    <T> List<T> getValueIsListForHash(RedisKeyBuild redisKeyBuild, String hashKey, Class<T> clazz);

    /**
     * Get the value of the given {@code hashKeys} from {@code key}
     *
     * @param redisKeyBuild      key
     * @param hashKeys hashKeys
     * @param clazz class object
     * @param <T>      T
     * @return
     */
    <T> List<T> multiGetForHash(RedisKeyBuild redisKeyBuild, List<String> hashKeys, Class<T> clazz);

    /**
     * Use with caution!
     * Get all values ​​under Hash Key
     *
     * @param redisKeyBuild cache key
     * @param clazz type
     * @param <T> Generic
     * @return
     */
    <T> List<T> getAllForHash(RedisKeyBuild redisKeyBuild, Class<T> clazz);
    
    /**
     * Use with caution!
     * Get all values ​​under Hash Key, the return value is map
     *
     * @param redisKeyBuild cache key
     * @param clazz type
     * @param <T> Generic
     * @return
     */
    <T> Map<String,T> getAllMapForHash(RedisKeyBuild redisKeyBuild, Class<T> clazz);
    /**
     * Determine whether the key in the hash exists
     *
     * @param redisKeyBuild cache key
     * @param hashKey key in hash
     * @return result
     */
    Boolean hasKeyForHash(RedisKeyBuild redisKeyBuild, String hashKey);

    /**
     * Delete hash key
     *
     * @param redisKeyBuild cache key
     * @param hashKey key in hash
     * @return result
     */
    Long delForHash(RedisKeyBuild redisKeyBuild, String hashKey);

    /**
     * Delete hash keys in batches
     *
     * @param redisKeyBuild cache key
     * @param hashKeys key in hash
     * @return result
     */
    Long delForHash(RedisKeyBuild redisKeyBuild, Collection<String> hashKeys);

    /**
     * Add increment to the integer value of the specified field in the hash table key
     *
     * @param redisKeyBuild cache key
     * @param hashKey key in hash
     * @param increment step size
     * @return result
     */
    Long incrByForHash(RedisKeyBuild redisKeyBuild, String hashKey, long increment);

    /**
     * Add increment (double type) to the integer value of the specified field in the hash table key
     *
     * @param redisKeyBuild cache key
     * @param hashKey key in hash
     * @param delta step size
     * @return result
     */
    Double incrByDoubleForHash(RedisKeyBuild redisKeyBuild, String hashKey, double delta);

    /**
     * Get hashKey in all hash tables
     *
     * @param redisKeyBuild cache key
     * @return result
     */
    Set<String> hashKeysForHash(RedisKeyBuild redisKeyBuild);

    /**
     * Get the number of fields in a hash table
     *
     * @param redisKeyBuild cache key
     * @return result
     */
    Long sizeForHash(RedisKeyBuild redisKeyBuild);

    /**------------------------list related operations------------------------------ */

    /**
     * Get an element in a list by index
     *
     * @param redisKeyBuild cache key
     * @param index index
     * @param clazz type
     * @return result
     */
    <T> T indexForList(RedisKeyBuild redisKeyBuild, long index, Class<T> clazz);

    /**
     * List puts elements from the left
     *
     * @param redisKeyBuild   key
     * @param value value
     * @return Change the number of lines
     */
    Long leftPushForList(RedisKeyBuild redisKeyBuild, Object value);

    /**
     * List puts elements from the left
     *
     * @param redisKeyBuild   key
     * @param valueList valueList
     * @return Change the number of lines
     */
    Long leftPushAllForList(RedisKeyBuild redisKeyBuild, List<?> valueList);

    /**
     * List puts elements from the left (only adds them when the list exists)
     *
     * @param redisKeyBuild
     * @param value
     * @return
     */
    Long leftPushIfPresentForList(RedisKeyBuild redisKeyBuild, Object value);

    /**
     * If pivot exists, add it to the left of pivot
     *
     * @param redisKeyBuild cache key
     * @param pivot pivot
     * @param value object
     * @return result
     */
    Long leftPushForList(RedisKeyBuild redisKeyBuild, Object pivot, Object value);

    /**
     * List puts elements from the right
     *
     * @param redisKeyBuild   key
     * @param value value
     * @return Change the number of lines
     */
    Long rightPushForList(RedisKeyBuild redisKeyBuild, Object value);

    /**
     * List puts elements from the right
     *
     * @param redisKeyBuild   key
     * @param valueList valueList
     * @return Change the number of lines
     */
    Long rightPushAllForList(RedisKeyBuild redisKeyBuild, List<Object> valueList);

    /**
     * List puts elements from the right (added when list already exists)
     *
     * @param redisKeyBuild cache key
     * @param value object
     * @return result
     */
    Long rightPushIfPresentForList(RedisKeyBuild redisKeyBuild, Object value);

    /**
     * If pivot exists, add it to the right of pivot
     *
     * @param redisKeyBuild cache key
     * @param pivot pivot
     * @param value object
     * @return result
     */
    Long rightPushForList(RedisKeyBuild redisKeyBuild, Object pivot, Object value);

    /**
     * Set the value of a list element by index
     *
     * @param redisKeyBuild cache key
     * @param index
     *            Location
     * @param value object
     */
    void setForList(RedisKeyBuild redisKeyBuild, long index, Object value);

    /**
     * Remove and get the first element of the list
     *
     * @param redisKeyBuild cache key
     * @param clazz type
     * @return deleted element
     */
    <T> T leftPopForList(RedisKeyBuild redisKeyBuild, Class<T> clazz);

    /**
     * Remove and get the first element of the list. If there is no element in the list, the list will be blocked until the wait times out or a pop-up element is found.
     *
     * @param redisKeyBuild cache key
     * @param clazz type
     * @param timeout
     *            waiting time
     * @param unit
     *            time unit
     * @return
     */
    <T> T leftPopBlockForList(RedisKeyBuild redisKeyBuild, Class<T> clazz, long timeout, TimeUnit unit);

    /**
     * Remove and get the last element of the list
     *
     * @param redisKeyBuild cache key
     * @param clazz type
     * @return deleted element
     */
    <T> T rightPopForList(RedisKeyBuild redisKeyBuild, Class<T> clazz);

    /**
     * Remove and get the last element of the list. If there is no element in the list, the list will be blocked until the wait times out or a pop-up element is found.
     *
     * @param redisKeyBuild cache key
     * @param clazz type
     * @param timeout
     *            waiting time
     * @param unit
     *            time unit
     * @return
     */
    <T> T rightPopBlockForList(RedisKeyBuild redisKeyBuild, Class<T> clazz, long timeout, TimeUnit unit);

    /**
     * Removes the last element of a list and adds that element to another list and returns
     *
     * @param sourceKey
     * @param destinationKey
     * @param clazz
     * @return
     */
    <T> T rightPopAndLeftPushForList(RedisKeyBuild sourceKey, RedisKeyBuild destinationKey, Class<T> clazz);

    /**
     * Pop a value from the list, insert the popped element into another list and return it; if the list has no elements, the list will be blocked until the wait times out or a popable element is found.
     *
     * @param sourceKey cache key
     * @param destinationKey popup key
     * @param clazz type
     * @param timeout time
     * @param unit time unit
     * @return result
     */
    <T> T rightPopBlockAndLeftPushForList(RedisKeyBuild sourceKey, RedisKeyBuild destinationKey, Class<T> clazz, long timeout, TimeUnit unit);

    /**
     * Get all data of List
     *
     * @param redisKeyBuild cache key
     * @param <T> Generic
     * @param clazz type
     * @return
     */
    <T> List<T> getAllForList(RedisKeyBuild redisKeyBuild, Class<T> clazz);

    /**
     * Get elements within a specified range of a list
     *
     * @param redisKeyBuild cache key
     * @param start starting position, 0 is the starting position
     * @param end end position, -1 returns all
     * @param clazz type
     * @return result
     */
    <T> List<T> rangeForList(RedisKeyBuild redisKeyBuild, long start, long end, Class<T> clazz);


    /**
     * Delete the elements in the collection whose value is equal to value
     *
     * @param redisKeyBuild cache key
     * @param index
     *            index=0, delete all elements whose value is equal to value; index>0, delete the first element whose value is equal to value from the head;
     *            index<0, delete the first element whose value is equal to value from the end;
     * @param value object
     * @return result
     */
    Long removeForList(RedisKeyBuild redisKeyBuild, long index, Object value);


    /**
     * crop list
     *
     * @param redisKeyBuild
     * @param start
     * @param end
     */
    void trimForList(RedisKeyBuild redisKeyBuild, long start, long end);

    /**
     * Get list length
     *
     * @param redisKeyBuild
     * @return
     */
    Long lenForList(RedisKeyBuild redisKeyBuild);


    /**--------------------set related operations-------------------------- */

    /**
     * setadd element
     *
     * @param redisKeyBuild
     * @param value
     * @return
     */
    Long addForSet(RedisKeyBuild redisKeyBuild, Object value);

    /**
     * set adds elements in batches
     *
     * @param redisKeyBuild
     * @param values
     * @return
     */
    Long addForSet(RedisKeyBuild redisKeyBuild, List<?> values);

    /**
     * set removes elements
     *
     * @param redisKeyBuild
     * @param value
     * @return
     */
    Long removeForSet(RedisKeyBuild redisKeyBuild, Object value);

    /**
     * set removes elements in batches
     *
     * @param redisKeyBuild
     * @param values
     * @return
     */
    Long removeForSet(RedisKeyBuild redisKeyBuild, List<?> values);

    /**
     * Removes and returns a random element of the collection
     *
     * @param redisKeyBuild
     * @param clazz
     * @return
     */
    <T> T popForSet(RedisKeyBuild redisKeyBuild, Class<T> clazz);

    /**
     * Move element value from one collection to another
     *
     * @param redisKeyBuild
     * @param value
     * @param destRedisKeyBuild
     * @return
     */
    boolean moveForSet(RedisKeyBuild redisKeyBuild, Object value, RedisKeyBuild destRedisKeyBuild);

    /**
     * Get the size of the collection
     *
     * @param redisKeyBuild
     * @return
     */
    Long sizeForSet(RedisKeyBuild redisKeyBuild);

    /**
     * Determine whether the collection contains value
     *
     * @param redisKeyBuild
     * @param value
     * @return
     */
    Boolean isMemberForSet(RedisKeyBuild redisKeyBuild, Object value);

    /**
     * Get the intersection of two sets
     *
     * @param redisKeyBuild
     * @param otherRedisKeyBuild
     * @param clazz
     * @return
     */
    <T> Set<T> intersectForSet(RedisKeyBuild redisKeyBuild, RedisKeyBuild otherRedisKeyBuild, Class<T> clazz);

    /**
     * Get the intersection of key set and multiple sets
     *
     * @param redisKeyBuild
     * @param otherRedisKeyBuilds
     * @param clazz
     * @return
     */
    <T> Set<T> intersectForSet(RedisKeyBuild redisKeyBuild, Collection<RedisKeyBuild> otherRedisKeyBuilds, Class<T> clazz);

    /**
     * The intersection of key set and otherKey set is stored in destKey set
     *
     * @param redisKeyBuild
     * @param otherRedisKeyBuild
     * @param destRedisKeyBuild
     * @return
     */
    Long intersectAndStoreForSet(RedisKeyBuild redisKeyBuild, RedisKeyBuild otherRedisKeyBuild, RedisKeyBuild destRedisKeyBuild);

    /**
     * The intersection of the key set and multiple sets is stored in the destKey set.
     *
     * @param redisKeyBuild
     * @param otherRedisKeyBuilds
     * @param destRedisKeyBuild
     * @return
     */
    Long intersectAndStoreForSet(RedisKeyBuild redisKeyBuild, Collection<RedisKeyBuild> otherRedisKeyBuilds, RedisKeyBuild destRedisKeyBuild);

    /**
     * Get the union of two sets
     *
     * @param redisKeyBuild
     * @param otherRedisKeyBuild
     * @param clazz
     * @return
     */
    <T> Set<T> unionForSet(RedisKeyBuild redisKeyBuild, RedisKeyBuild otherRedisKeyBuild, Class<T> clazz);

    /**
     * Get the union of key set and multiple sets
     *
     * @param redisKeyBuild
     * @param otherRedisKeyBuilds
     * @param clazz
     * @return
     */
    <T> Set<T> unionForSet(RedisKeyBuild redisKeyBuild, Collection<RedisKeyBuild> otherRedisKeyBuilds, Class<T> clazz);

    /**
     * The union of key set and otherKey set is stored in destKey.
     *
     * @param redisKeyBuild
     * @param otherRedisKeyBuild
     * @param destRedisKeyBuild
     * @return
     */
    Long unionAndStoreForSet(RedisKeyBuild redisKeyBuild, RedisKeyBuild otherRedisKeyBuild, RedisKeyBuild destRedisKeyBuild);

    /**
     * The union of the key set and multiple sets is stored in destKey
     *
     * @param redisKeyBuild
     * @param otherRedisKeyBuilds
     * @param destRedisKeyBuild
     * @return
     */
    Long unionAndStoreForSet(RedisKeyBuild redisKeyBuild, Collection<RedisKeyBuild> otherRedisKeyBuilds, RedisKeyBuild destRedisKeyBuild);

    /**
     * Get the difference between two sets
     *
     * @param redisKeyBuild
     * @param otherRedisKeyBuild
     * @param clazz
     * @return
     */
    <T> Set<T> differenceForSet(RedisKeyBuild redisKeyBuild, RedisKeyBuild otherRedisKeyBuild, Class<T> clazz);

    /**
     * Get the difference between the key set and multiple sets
     *
     * @param redisKeyBuild
     * @param otherRedisKeyBuilds
     * @param clazz
     * @return
     */
    <T> Set<T> differenceForSet(RedisKeyBuild redisKeyBuild, Collection<RedisKeyBuild> otherRedisKeyBuilds, Class<T> clazz);

    /**
     * The difference between the key set and the otherKey set is stored in destKey.
     *
     * @param redisKeyBuild
     * @param otherRedisKeyBuild
     * @param destRedisKeyBuild
     * @return
     */
    Long differenceForSet(RedisKeyBuild redisKeyBuild, RedisKeyBuild otherRedisKeyBuild, RedisKeyBuild destRedisKeyBuild);

    /**
     * The difference between the key set and multiple sets is stored in destKey.
     *
     * @param redisKeyBuild
     * @param otherRedisKeyBuilds
     * @param destRedisKeyBuild
     * @return
     */
    Long differenceForSet(RedisKeyBuild redisKeyBuild, Collection<RedisKeyBuild> otherRedisKeyBuilds, RedisKeyBuild destRedisKeyBuild);

    /**
     * Get all elements of the collection
     *
     * @param redisKeyBuild
     * @param clazz
     * @return
     */
    <T> Set<T> membersForSet(RedisKeyBuild redisKeyBuild, Class<T> clazz);

    /**
     * Get a random element from the collection
     *
     * @param redisKeyBuild
     * @param clazz
     * @return
     */
    <T> T randomMemberForSet(RedisKeyBuild redisKeyBuild, Class<T> clazz);

    /**
     * Randomly obtain count elements from the collection
     *
     * @param redisKeyBuild
     * @param count
     * @param clazz
     * @return
     */
    <T> List<T> randomMembersForSet(RedisKeyBuild redisKeyBuild, long count, Class<T> clazz);

    /**
     * Randomly obtain count elements from the collection and remove duplicates
     *
     * @param redisKeyBuild
     * @param count
     * @param clazz
     * @return
     */
    <T> Set<T> distinctRandomMembersForSet(RedisKeyBuild redisKeyBuild, long count, Class<T> clazz);

    /**
     * Cursor traversal
     * @param redisKeyBuild
     * @param options
     * @return
     */
    Cursor<String> scanForSet(RedisKeyBuild redisKeyBuild, ScanOptions options);



    /**------------------SortedSet related operations--------------------------------*/

    /**
     * Store ordered list
     *
     * @param redisKeyBuild key
     * @param value value
     * @param score score value
     * @return
     */
    void addForSortedSet(RedisKeyBuild redisKeyBuild, Object value, Double score);

    /**
     * Store an ordered list and set the timeout (seconds)
     *
     * @param redisKeyBuild key
     * @param value value
     * @param score score value
     * @param ttl timeout
     * @return
     */
    void addForSortedSet(RedisKeyBuild redisKeyBuild, Object value, Double score, long ttl);

    /**
     * Store an ordered list and set a timeout
     *
     * @param redisKeyBuild key
     * @param value value
     * @param score score value
     * @param ttl timeout
     * @param timeUnit time unit
     * @return
     */
    void addForSortedSet(RedisKeyBuild redisKeyBuild, Object value, Double score, long ttl, TimeUnit timeUnit);

    /**
     * Store ordered list
     *
     * @param redisKeyBuild
     * @param map If the key in the map is a custom object type, you need to re-equals and hashcode methods
     * @return
     */
    Long addForSortedSet(RedisKeyBuild redisKeyBuild, Map<?, Double> map);

    /**
     * Store an ordered list and set the timeout (seconds)
     *
     * @param redisKeyBuild
     * @param map If the key in the map is a custom object type, you need to re-equals and hashcode methods
     * @param ttl
     * @return
     */
    Long addForSortedSet(RedisKeyBuild redisKeyBuild, Map<?, Double> map, long ttl);

    /**
     * Store ordered lists and set timeouts in custom units
     *
     * @param redisKeyBuild      key
     * @param map If the key in the map is a custom object type, you need to re-equals and hashcode methods
     * @param ttl expiration time
     * @param timeUnit Expiration time unit
     * @return the affected items
     */
    Long addForSortedSet(RedisKeyBuild redisKeyBuild, Map<?, Double> map, long ttl, TimeUnit timeUnit);

    /**
     * Get the range entries in the ordered list and convert them to the specified type
     *
     * @param redisKeyBuild   key
     * @param start start index starts from 0
     * @param end end subscript contains this item
     * @param clazz serialization type
     * @param <T> generic parameter
     * @return result set collection
     */
    <T> Set<T> getRangeForSortedSet(RedisKeyBuild redisKeyBuild, long start, long end, Class<T> clazz);

    /**
     * Reverse to obtain the range entries in the ordered list and convert them to the specified type
     *
     * @param redisKeyBuild   key
     * @param start start index starts from 0
     * @param end end subscript contains this item
     * @param clazz serialization type
     * @param <T> generic parameter
     * @return result set collection
     */
    <T> Set<T> getReverseRangeForSortedSet(RedisKeyBuild redisKeyBuild, long start, long end, Class<T> clazz);

    /**
     * Delete zSet entry
     *
     * @param redisKeyBuild   key
     * @param value data
     * @return affects the entry
     */
    Long delForSortedSet(RedisKeyBuild redisKeyBuild, Object value);

    /**
     * Delete zSet entries in batches
     *
     * @param redisKeyBuild             key
     * @param valueCollection data
     * @return affects the entry
     */
    Long delForSortedSet(RedisKeyBuild redisKeyBuild, Collection<?> valueCollection);

    /**
     * Remove range elements
     *
     * @param redisKeyBuild   key
     * @param start start range
     * @param end end range
     * @return affects the entry
     */
    Long delRangeForSortedSet(RedisKeyBuild redisKeyBuild, long start, long end);


    /**
     * Increase the element's score value and return the increased value
     *
     * @param redisKeyBuild key
     * @param value object
     * @param delta value
     * @return result
     */
    Double incrementScoreForSortedSet(RedisKeyBuild redisKeyBuild, Object value, double delta);



    /**
     * Calculate the total number of zSet items
     *
     * @param redisKeyBuild key
     * @return The total number of items. If it does not exist, it will be empty.
     */
    Long sizeForSortedSet(RedisKeyBuild redisKeyBuild);

    /**
     * Returns the ranking of the elements in the set. The ordered set is arranged from small to large according to the score value of the elements.
     *
     * @param redisKeyBuild   key
     * @param value value
     * @return rank
     */
    Long rankForSortedSet(RedisKeyBuild redisKeyBuild, Object value);

    /**
     * Returns the ranking of the elements in the collection, arranged from large to small according to the score value of the elements.
     *
     * @param redisKeyBuild
     * @param value
     * @return
     */
    Long reverseRankForSortedSet(RedisKeyBuild redisKeyBuild, Object value);


    /**
     * Get the collection elements and get the score value as well
     *
     * @param redisKeyBuild
     * @param start
     * @param end
     * @param clazz
     * @return
     */
    <T> Set<ZSetOperations.TypedTuple<T>> rangeWithScoreForSortedSet(RedisKeyBuild redisKeyBuild, long start, long end, Class<T> clazz);


    /**
     * Query collection elements based on Score value
     *
     * @param redisKeyBuild
     * @param min
     *            minimum value
     * @param max
     *            maximum value
     * @param clazz
     * @return
     */
    <T> Set<T> rangeByScoreForSortedSet(RedisKeyBuild redisKeyBuild, double min, double max, Class<T> clazz);

    /**
     * Query collection elements (containing score value) based on Score value, sort from small to large
     *
     * @param redisKeyBuild
     * @param min
     *            minimum value
     * @param max
     *            maximum value
     * @param clazz
     * @return
     */
    <T> Set<ZSetOperations.TypedTuple<T>> rangeByScoreWithScoreForSortedSet(RedisKeyBuild redisKeyBuild, double min, double max, Class<T> clazz);


    /**
     * Query collection elements based on Score value, sort from small to large
     *
     * @param redisKeyBuild
     * @param min
     *            minimum value
     * @param max
     *            maximum value
     * @param start
     * @param end
     * @param clazz
     * @return
     */
    <T> Set<ZSetOperations.TypedTuple<T>> rangeByScoreWithScoreForSortedSet(RedisKeyBuild redisKeyBuild, double min, double max,
                                                                            long start, long end, Class<T> clazz);

    /**
     * Get the elements of the collection, sort them from large to small, and return the score value
     *
     * @param redisKeyBuild
     * @param start
     * @param end
     * @param clazz
     * @return
     */
    <T> Set<ZSetOperations.TypedTuple<T>> reverseRangeWithScoreForSortedSet(RedisKeyBuild redisKeyBuild, long start, long end, Class<T> clazz);

    /**
     * Query collection elements based on Score value, sort from large to small
     *
     * @param redisKeyBuild
     * @param min
     * @param max
     * @param clazz
     * @return
     */
    <T> Set<T> reverseRangeByScoreForSortedSet(RedisKeyBuild redisKeyBuild, double min, double max, Class<T> clazz);

    /**
     * Query the collection elements according to the Score value, sort them from large to small, and return the score value
     *
     * @param redisKeyBuild
     * @param min
     * @param max
     * @param clazz
     * @return
     */
    <T> Set<ZSetOperations.TypedTuple<T>> reverseRangeByScoreWithScoreForSortedSet(RedisKeyBuild redisKeyBuild, double min, double max, Class<T> clazz);

    /**
     * Query collection elements based on Score value, sort from large to small
     *
     * @param redisKeyBuild
     * @param min
     * @param max
     * @param start
     * @param end
     * @param clazz
     * @return
     */
    <T> Set<T> reverseRangeByScoreForSortedSet(RedisKeyBuild redisKeyBuild, double min, double max, long start, long end, Class<T> clazz);

    /**
     * Get the number of collection elements based on the score value
     *
     * @param redisKeyBuild
     * @param min
     * @param max
     * @return
     */
    Long countForSortedSet(RedisKeyBuild redisKeyBuild, double min, double max);

    /**
     * Get collection size
     *
     * @param redisKeyBuild
     * @return
     */
    Long zCardForSortedSet(RedisKeyBuild redisKeyBuild);

    /**
     * Get the score value of the value element in the collection
     *
     * @param redisKeyBuild
     * @param value
     * @return
     */
    Double scoreByValueForSortedSet(RedisKeyBuild redisKeyBuild, Object value);

    /**
     * Remove the member at the specified index position
     *
     * @param redisKeyBuild
     * @param start
     * @param end
     * @return
     */
    Long removeRangeForSortedSet(RedisKeyBuild redisKeyBuild, long start, long end);

    /**
     * Remove members based on a specified range of score values
     *
     * @param redisKeyBuild
     * @param min
     * @param max
     * @return
     */
    Long removeRangeByScoreForSortedSet(RedisKeyBuild redisKeyBuild, double min, double max);

    /**
     * Get the union of key and otherKey and store it in destKey
     *
     * @param redisKeyBuild
     * @param otherRedisKeyBuild
     * @param destRedisKeyBuild
     * @return
     */
    Long unionAndStoreForSortedSet(RedisKeyBuild redisKeyBuild, RedisKeyBuild otherRedisKeyBuild, RedisKeyBuild destRedisKeyBuild);

    /**
     * Get the union of key and otherKeys and store it in destKey
     * @param redisKeyBuild
     * @param otherRedisKeyBuilds
     * @param destRedisKeyBuild
     * @return
     */
    Long unionAndStoreForSortedSet(RedisKeyBuild redisKeyBuild, Collection<RedisKeyBuild> otherRedisKeyBuilds, RedisKeyBuild destRedisKeyBuild);

    /**
     * Get the intersection of key and otherKey and store it in destKey
     *
     * @param redisKeyBuild
     * @param otherRedisKeyBuild
     * @param destRedisKeyBuild
     * @return
     */
    Long intersectAndStoreForSortedSet(RedisKeyBuild redisKeyBuild, RedisKeyBuild otherRedisKeyBuild, RedisKeyBuild destRedisKeyBuild);

    /**
     * Get the intersection of key and otherKeys and store it in destKey
     *
     * @param redisKeyBuild
     * @param otherRedisKeyBuilds
     * @param destRedisKeyBuild
     * @return
     */
    Long intersectAndStoreForSortedSet(RedisKeyBuild redisKeyBuild, Collection<RedisKeyBuild> otherRedisKeyBuilds, RedisKeyBuild destRedisKeyBuild);

    /**
     * Cursor traversal
     * @param redisKeyBuild
     * @param options
     * @return
     */
    Cursor<ZSetOperations.TypedTuple<String>> scanForSortedSet(RedisKeyBuild redisKeyBuild, ScanOptions options);

    /**
     * Not for external use
     * @param redisKeyBuild
     * @param genericReturnType
     * @return
     */
    <T> T getByType(RedisKeyBuild redisKeyBuild, Type genericReturnType);
    /**
     * Get instance
     *
     * @return
     */
    RedisTemplate getInstance();
}
