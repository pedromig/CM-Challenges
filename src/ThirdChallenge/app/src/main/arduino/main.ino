/*
  Based on:
    https://wokwi.com/projects/322524997423727188
    https://wokwi.com/projects/321525495180034642
*/

#include <DHTesp.h>
#include <PubSubClient.h>
#include <LiquidCrystal_I2C.h>
#include <Wire.h>
#include <WiFi.h>

// Define the LCD screen
LiquidCrystal_I2C LCD = LiquidCrystal_I2C(0x27, 16, 2);

// Define random ID
String ID_MQTT;
char *letters = "abcdefghijklmnopqrstuvwxyz0123456789";

// Define MQTT Topics
//#define TOPIC_TEST       "topicstest"

// Update every 1 seconds
#define PUBLISH_DELAY 500
unsigned long publishUpdate;

// Wi-Fi settinges
const char *SSID = "Wokwi-GUEST";
const char *PASSWORD = "";

const char * TOPIC_TEMPERATURE = "/challenge/temperature";
const char * TOPIC_HUMIDITY = "/challenge/humidity";
const char * TOPIC_LED = "/challenge/led";

// Define MQTT Broker and PORT
const char *BROKER_MQTT = "broker.hivemq.com";
int BROKER_PORT = 1883;

// Defined ports
const int DHT_PIN = 15;
const int LED_PIN = 12;
bool led_state = LOW;

// Global variables
WiFiClient espClient;
PubSubClient MQTT(espClient); 
DHTesp dhtSensor;

// Declarations
void startWifi(void);
void initMQTT(void);
void callbackMQTT(char *topic, byte *payload, unsigned int length);
void reconnectMQTT(void);
void reconnectWiFi(void);
void checkWiFIAndMQTT(void);


// Starts the Wi-Fi
void startWifi(void) {
  reconnectWiFi();
}

// Starts everything from MQTT
void initMQTT(void) {
  MQTT.setServer(BROKER_MQTT, BROKER_PORT);
  MQTT.setCallback(callbackMQTT);
}

// Callback from Android 
// --- Get the messages here
void callbackMQTT(char *topic, byte *payload, unsigned int length) {
  String msg;

  // Convert payload to string
  for (int i = 0; i < length; i++) {
    char c = (char)payload[i];
    msg += c;
  }

  Serial.printf("Topic: %s\n", topic);
  Serial.printf("Message: %s\n", msg, topic);

  if (msg.equals("on")) {
    led_state = HIGH;
  } else if(msg.equals("off")) {
    led_state = LOW;
  }
  digitalWrite(LED_PIN, led_state);
}

// Connects to the Broker with a specific random ID
void reconnectMQTT(void) {
  while (!MQTT.connected()) {
    ID_MQTT = "";
    Serial.print("* Starting connection with broker: ");
    Serial.println(BROKER_MQTT);

    int i = 0;
    for (i = 0; i < 10; i++) {
      ID_MQTT = ID_MQTT + letters[random(0, 36)];
    }

    if (MQTT.connect(ID_MQTT.c_str())) {
      Serial.print("* Connected to broker successfully with ID: ");
      Serial.println(ID_MQTT);
      MQTT.subscribe(TOPIC_LED);
    } else {
      Serial.println("* Failed to connected to broker. Trying again in 2 seconds.");
      delay(2000);
    }
  }
}

// Checks both Wi-Fi and MQTT state, and reconnects if something failed.
void checkWiFIAndMQTT(void) {
  if (!MQTT.connected())
    reconnectMQTT();
  reconnectWiFi();
}

void reconnectWiFi(void) {
  if (WiFi.status() == WL_CONNECTED)
    return;

  WiFi.begin(SSID, PASSWORD); // Conecta na rede WI-FI

  Serial.print("* Connecting to Wifi ");
  while (WiFi.status() != WL_CONNECTED) {
    delay(100);
    Serial.print(".");
  }

  Serial.println("");
  Serial.print("* Successfully connected to Wi-Fi, with local IP: ");
  Serial.println(WiFi.localIP());

  LCD.clear();
  LCD.setCursor(0, 0);
  LCD.print("Finished!");
  LCD.setCursor(0, 1);
  LCD.print("-- ");
  LCD.print(WiFi.localIP());
}

// ==========================================
void setup() {
  Serial.begin(115200);
  pinMode(LED_PIN, OUTPUT);

  LCD.init();
  LCD.backlight();
  LCD.setCursor(0, 0);
  LCD.print("Initializing...");
  LCD.setCursor(0, 1);
  LCD.print("Please wait...");

  dhtSensor.setup(DHT_PIN, DHTesp::DHT22);

  startWifi();
  initMQTT();
}

void loop() {
  // Loop every 1 second
  if ((millis() - publishUpdate) >= PUBLISH_DELAY) {
    publishUpdate = millis();

    // Check if Wi-Fi and MQTT are up.
    checkWiFIAndMQTT();

    // Get data from sensor
    TempAndHumidity  data = dhtSensor.getTempAndHumidity();
    LCD.clear();
    String str_temperature = String(data.temperature, 2);
    String str_humidity = String(data.humidity, 1);
    LCD.setCursor(0, 0);
    LCD.print("LED IS ");
    
    if(led_state == HIGH)
      LCD.print("ON");
    else
      LCD.print("OFF");
    LCD.setCursor(0, 1);
    LCD.print("T:" + str_temperature + "C H:" + str_humidity + "%");


    MQTT.publish(TOPIC_TEMPERATURE, str_temperature.c_str());
    MQTT.publish(TOPIC_HUMIDITY, str_humidity.c_str());
  
    // MQTT Keep-alive loop
    MQTT.loop();
  }
}
