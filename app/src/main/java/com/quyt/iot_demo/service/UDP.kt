package com.quyt.iot_demo.service

import android.R.attr.port
import android.util.Log
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.*


class ClientSendAndListen(val ip : String,val listener : OnResult) : Runnable {
    override fun run() {
        var run = true
        try {
            val udpSocket = DatagramSocket(2604)
            val serverAddr: InetAddress = InetAddress.getByName(ip)
            val buf = "quythanh24".toByteArray()
            val packet = DatagramPacket(buf, buf.size, serverAddr, 2604)
            udpSocket.send(packet)
            while (run) {
                try {
                    val message = ByteArray(8000)
                    val packetRev = DatagramPacket(message, message.size)
                    Log.i("UDP client: ", "about to wait to receive")
//                    udpSocket.soTimeout = 100000
                    udpSocket.receive(packetRev)
                    val text = String(message, 0, packetRev.length)
                    Log.d("Received text", text)
                    if (text.isNotEmpty()){
                        listener.onResult(text)
                        udpSocket.close()
                    }
                } catch (e: IOException) {
                    Log.e(" UDP client has IOException", "error: ", e)
                    run = false
                    udpSocket.close()
                } catch (e: SocketTimeoutException) {
                    Log.e("Timeout Exception", "UDP Connection:", e)
                    run = false
                    udpSocket.close()
                }
            }
        } catch (e: SocketException) {
            Log.e("Socket Open:", "Error:", e)
        }
    }

    fun getBytesByString(string: String): ByteArray? {
        return try {
            string.toByteArray(charset("utf-8"))
        } catch (e: UnsupportedEncodingException) {
            throw IllegalArgumentException("the charset is invalid")
        }
    }
}

interface OnResult {
    fun onResult(data : String)
}



