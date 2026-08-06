local fields = redis.call('HKEYS', KEYS[1])
local removed = 0
local i = 1
while i <= #fields do
    local last = math.min(i + 199, #fields)
    local chunk = {}
    for k = i, last do
        chunk[#chunk + 1] = fields[k]
    end
    local ttls = redis.call('HTTL', KEYS[1], 'FIELDS', #chunk, unpack(chunk))
    local stale = {}
    for index, ttl in ipairs(ttls) do
        if ttl == -1 then
            stale[#stale + 1] = chunk[index]
        end
    end
    if #stale > 0 then
        redis.call('HDEL', KEYS[1], unpack(stale))
        removed = removed + #stale
    end
    i = last + 1
end
return removed
