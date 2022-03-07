//import SslUtil.getSocketFactory
//import org.eclipse.paho.client.mqttv3
//import org.eclipse.paho.client.mqttv3.{IMqttDeliveryToken, MqttCallback, MqttClient, MqttConnectOptions, MqttDeliveryToken, MqttException}
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
//
//import javax.net.ssl.SSLSocketFactory

class TO_BE_DELETED_MQTT {


  //  val trustStore = KeyStore.getInstance(KeyStore.getDefaultType)
  //  trustStore.load(null,null)
  //  trustStore.setCertificateEntry("Custom CA", CertificateFactory.getInstance("X509").generateCertificate(new FileInputStream("/Users/petrahorvath/Desktop/licenta/root-CA.cert")))
  //
  //  val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
  //  tmf.init(trustStore)
  //  val trustManagers = tmf.getTrustManagers
  //
  //  val sslContext = SSLContext.getInstance("SSL")
  //  sslContext.init(null, trustManagers, null)
  //  val sslSocketFactory =  sslContext.getSocketFactory

  //MQTT source stuff
  //  val connectionSettings = MqttConnectionSettings(
  //    "ssl://a2n7tk1kp18wix-ats.iot.us-east-1.amazonaws.com:8883", // the MQTT broker address
  //    "account-48ba2ee5-b789-40e0-b6e8-a693ca51856f", // a unique ID for the client (setting it to the empty string should let the MQTT broker assign it, but not all do you might want to generate it)
  //    new MemoryPersistence, // the MQTT client persistence to use (eg. MemoryPersistence) which allows to control reliability guarantees
  //  ).withSocketFactory(SslUtil.getSocketFactory(
  //    "/Users/petrahorvath/Desktop/licenta/root-CA.cert",
  //    "/Users/petrahorvath/Desktop/licenta/certificate.crt",
  //    "/Users/petrahorvath/Desktop/licenta/privatekey.pem",
  //    ""))
  //
  //
  //  val deviceId = "nrf-352656100442659"
  //
  //  val mqttSource: Source[MqttMessage, Future[Done]] =
  //    MqttSource.atMostOnce(
  //      connectionSettings,
  //      MqttSubscriptions(
  //        Map(s"prod/48ba2ee5-b789-40e0-b6e8-a693ca51856f/m/#" -> MqttQoS.AtLeastOnce)),
  //      bufferSize = 8
  //    )
  //
  //  val (subscribed, streamResult) = mqttSource
  //    .take(100L)
  //    .toMat(Sink.seq)(Keep.both)
  //    .run()
//
//  val serverUrl = "ssl://a2n7tk1kp18wix-ats.iot.us-east-1.amazonaws.com:8883"
//  val caFilePath = "/Users/petrahorvath/Desktop/licenta/root-CA.cert"
//  val clientCrtFilePath = "/Users/petrahorvath/Desktop/licenta/certificate.cert"
//  val clientKeyFilePath = "/Users/petrahorvath/Desktop/licenta/privatekey.pem"
//  val clientId = "account-48ba2ee5-b789-40e0-b6e8-a693ca51856f"
//  //  val mqttUserName = "guest"
//  //  val mqttPassword = "123123"
//
//  val client: MqttClient = new MqttClient(serverUrl, MqttClient.generateClientId(), new MemoryPersistence())
//  val options: MqttConnectOptions = new MqttConnectOptions()
//  //  options.setUserName("account-48ba2ee5-b789-40e0-b6e8-a693ca51856f")
//  //  options.setPassword(mqttPassword.toCharArray())
//
//  options.setConnectionTimeout(60)
//  options.setKeepAliveInterval(60)
//  options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1)
//
//  val socketFactory: SSLSocketFactory = getSocketFactory(caFilePath, clientCrtFilePath, clientKeyFilePath, "")
//  options.setSocketFactory(socketFactory)
//
//  client.setCallback(new MqttCallback() {
//    override def connectionLost(me: Throwable): Unit = {
//      println("Connection lost")
//      println("msg " + me.getMessage)
//      println("loc " + me.getLocalizedMessage)
//      println("cause " + me.getCause)
//      println("excep " + me)
//    }
//
//    override def deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken): Unit = {
//      println("deliverd--------")
//      try {
//        val token = iMqttDeliveryToken.asInstanceOf[MqttDeliveryToken]
//        val h = token.getMessage.toString
//        println("deliverd message :" + h)
//      } catch {
//        case me: MqttException =>
//          println("reason " + me.getReasonCode)
//          println("msg " + me.getMessage)
//          println("loc " + me.getLocalizedMessage)
//          println("cause " + me.getCause)
//          println("excep " + me)
//        case e: Exception =>
//          e.printStackTrace()
//      }
//    }
//
//    override def messageArrived(topic: String, message: mqttv3.MqttMessage): Unit = {
//      println("topic " + topic)
//      println("message " + message)
//    }
//  })
//
//  println("starting connect the server...")
//  val d = client.getDebug
//  d.dumpBaseDebug()
//  client.connect(options)
//  println("connected!")
//  Thread.sleep(1000)
//
//  client.subscribe("prod/48ba2ee5-b789-40e0-b6e8-a693ca51856f/m/d/nrf-352656100442659/d2c", 0)
//  client.disconnect()
//  println("disconnected!")

}
