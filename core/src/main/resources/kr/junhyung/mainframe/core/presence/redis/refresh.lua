local ttl = ARGV[1]
local fields = {}
for i = 2, #ARGV, 2 do
    redis.call('HSET', KEYS[1], ARGV[i], ARGV[i + 1])
    fields[#fields + 1] = ARGV[i]
end
redis.call('HEXPIRE', KEYS[1], ttl, 'FIELDS', #fields, unpack(fields))
return #fields
