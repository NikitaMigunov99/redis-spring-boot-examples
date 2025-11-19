-- Lua script for Redis: executes HEXPIRE
-- KEYS[1] = key
-- ARGV[1] = seconds (TTL)
-- ARGV[2] = mode flag (one of "NX", "XX", "GT", "LT", or "" for none)
-- ARGV[3] = number of fields (N)
-- ARGV[4..(4+N-1)] = the fields

local key       = KEYS[1]
local seconds   = tonumber(ARGV[1])
local mode      = ARGV[2]
local numfields = tonumber(ARGV[3])

-- Build arguments for HEXPIRE
local args = { key, seconds }

-- If mode flag is non-empty, append it
if mode ~= "" then
    table.insert(args, mode)
end

-- Append the "FIELDS" sub-command: first the word "FIELDS", then numfields, then field names
table.insert(args, "FIELDS")
table.insert(args, tostring(numfields))

for i = 1, numfields do
    local field = ARGV[3 + i]
    table.insert(args, field)
end

-- Execute the command
local result = redis.call("HEXPIRE", unpack(args))

return result