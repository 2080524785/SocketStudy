# SocketStudy

Introduce my study of socket in this work,which has major use of websocket and netty and the computer language is used by java.
* * * * * * * * * * * * * * *
**Reference**  
<https://blog.csdn.net/weixin_44268792/article/details/106243014>

<https://github.com/shushushv/webrtc-p2p>

* * * * * * * * * * * * * * *
In this project,I make many tests about websocket,socket,netty,negix and webRTC, which let me feel complex.
* * * * * * * * * * * * * * *

* ***webrtc.html*** file is used by javascript and webrtc to achieve a simple solution about video streaming communication between different client.

  There has need advancedly know some information and knowledge about socket protocols.

  * SDP :Session description protocol

    * The type of media (audio, video), media format (codec) that will be used in the browser-to-browser session
    * The IP address and port number used by both communicating parties.
    * P2P data transfer protocol, known as Secure RTP in WebRTC.
    * The bandwidth used for communication.

      .......
  * ICE :Protocol for NAT traversal ( somewhat similar to intranet traversal )

    * Use P2P for direct communication.
    * Use STUN (port mapping to traverse NAT) to achieve P2P communication that breaks through NAT gateways.
    * Use the TURN relay server for trunk communication that breaks through the firewall.

      .......
  * RTCIceCandidate

    * The RTCIceCandidate() constructor creates and returns a new RTCIceCandidate object, which can be configured to represent a single ICE candidate.

