-- Compare the thread identifier with the identifier in the lock to see if it is consistent
if(redis.call('get', KEYS[1]) ==  ARGV[1]) then
    -- release lock del key
    return redis.call('del', KEYS[1])
end
return 0