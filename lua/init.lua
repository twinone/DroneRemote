--load credentials
--SID and PassWord should be saved according wireless router in use

function startup()
    if file.open("init.lua") == nil then
      print("init.lua deleted")
    else
      print("Running")
      file.close("init.lua")
      dofile("main.lua")
    end
end

--init.lua
dofile("credentials.lua")

wifi.sta.disconnect()
-- vdd = adc.readvdd33()
-- print("Vdd = "..vdd.." mV")
print("")
print("set up wifi mode")
wifi.setmode(wifi.STATION)
wifi.sta.config(SSID,PASSWORD,0)
wifi.sta.connect()
tmr.alarm(1, 1000, 1, function()
    if wifi.sta.getip()== nil then
        print("IP unavaiable, Waiting...")
    else
        tmr.stop(1)
        print("Config done, IP is "..wifi.sta.getip())
        print("You have 3 seconds to abort Startup")
        print("Waiting...")
        tmr.alarm(0,3000,0,startup)
    end
 end)
