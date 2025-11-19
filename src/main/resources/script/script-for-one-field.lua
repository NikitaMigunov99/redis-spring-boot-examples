local key     = KEYS[1]
local seconds = tonumber(ARGV[1])
local field   = ARGV[2]

-- Execute HEXPIRE with a single field
-- Equivalent to: HEXPIRE key seconds FIELDS 1 field
local result = redis.call("HEXPIRE", key, seconds, "FIELDS", 1, field)

return result