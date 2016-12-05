PORT = 80
print("Starting server on "..wifi.sta.getip()..":"..PORT)

local function trimString(s)
   return string.match(s ,"^()%s*$") and "" or string.match(s,"^%s*(.*%S)")
end

local function split(str, sep)
        if sep == nil then
                sep = "%s"
        end
        local t={} ; i=1
        for str in string.gmatch(str, "([^"..sep.."]+)") do
                t[i] = str
                i = i + 1
        end
        return t
end

PWMS = { 0,0,0,0 }
PINS = { 1,2,3,4 }

function setup()
  for i,p in pairs(PINS) do
    pwm.setup(p, 500, 0)
  end
end
setup()

function updatePwms()
  for i, p in pairs(PINS) do
    --print("setting pin "..tostring(p).." to "..tostring(PWMS[i]))
    pwm.setduty(p, PWMS[i])
  end
end

function onMessage(sock, msg)
  -- set the new blinking period
  stopblink()
  LED_DELAY = 30
  blink(true)
  tmr.alarm(1,200,tmr.ALARM_SINGLE, function()
    LED_DELAY = LED_ORIG_DELAY
  end)

  msg = trimString(msg)
  print("got msg: ".. msg)
  msgs = split(msg, ":")
  if not #table == 4 then
    return
  end

  for i, val in pairs(msgs) do
    PWMS[i] = tonumber(val)
  end
  updatePwms()
  sock:send("OK")
end

srv = net.createServer(net.TCP, 60)
srv:listen(PORT, function(sock)
  sock:on("receive", onMessage)
end)
