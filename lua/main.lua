LED_PIN = 0
LED_ORIG_DELAY = 750
LED_DELAY = LED_ORIG_DELAY

gpio.mode(LED_PIN, gpio.OUTPUT)

function blink(val)
  gpio.write(LED_PIN, val and gpio.HIGH or gpio.LOW)
  tmr.alarm(0, LED_DELAY, tmr.ALARM_SINGLE, function()
    blink(not val)
  end)
end

function stopblink()
  tmr.stop(0)
end

blink(true)

dofile("server.lua")
