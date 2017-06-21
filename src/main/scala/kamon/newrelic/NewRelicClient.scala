package kamon.newrelic

import scalaj.http.Http

trait NewRelicClient {
  protected def execute(url: String, payload: Array[Byte]): String

  def execute(url: String, method: String, payload: String): String = {
    //I don't why only "connect" method need to be deflated manually
    val mayBeDeflatePayload =
      if (method.equalsIgnoreCase("connect"))
        KamonDeflater.compress(payload.getBytes("UTF-8"))
      else
        payload.getBytes("UTF-8")

    execute(url, mayBeDeflatePayload)
  }
}

class ScalaJClient extends NewRelicClient {
  override protected def execute(url: String, payload: Array[Byte]): String = {
    Http(url)
      .postData(payload)
      .header("Content-Type", "application/json")
      .header("Accept-Encoding", "deflate")
      .compress(true)
      .asString
      .body
  }
}
