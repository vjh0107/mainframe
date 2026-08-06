local owner = ARGV[1]
local removed = 0
for i = 2, #ARGV do
    local value = redis.call('HGET', KEYS[1], ARGV[i])
    if value and string.sub(value, 1, #owner) == owner then
        redis.call('HDEL', KEYS[1], ARGV[i])
        removed = removed + 1
    end
end
return removed
