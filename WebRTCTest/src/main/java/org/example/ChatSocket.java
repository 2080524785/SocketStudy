package org.example;



import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class ChatSocket {
    private static EventLoopGroup bossGroup = new NioEventLoopGroup();
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();
    private static ChannelFuture channelFuture;

    /**
     * 启动服务代理
     *
     * @throws Exception
     */
    public static void startServer() throws Exception {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(
                                    new WebSocketServerProtocolHandler("/myim", null, true, Integer.MAX_VALUE, false));
                            pipeline.addLast(new MessageToMessageCodec<TextWebSocketFrame, String>() {
                                @Override
                                protected void decode(ChannelHandlerContext ctx, TextWebSocketFrame frame,
                                                      List<Object> list) throws Exception {
                                    list.add(frame.text());
                                }

                                @Override
                                protected void encode(ChannelHandlerContext ctx, String msg, List<Object> list)
                                        throws Exception {
                                    list.add(new TextWebSocketFrame(msg));
                                }
                            });
                            pipeline.addLast(new ChatHandler());
                        }
                    });
            channelFuture = b.bind(8321).sync();

            channelFuture.channel().closeFuture().sync();
        } finally {
            shutdown();
            // 服务器已关闭
        }
    }

    public static void shutdown() {
        if (channelFuture != null) {
            channelFuture.channel().close().syncUninterruptibly();
        }
        if ((bossGroup != null) && (!bossGroup.isShutdown())) {
            bossGroup.shutdownGracefully();
        }
        if ((workerGroup != null) && (!workerGroup.isShutdown())) {
            workerGroup.shutdownGracefully();
        }
    }

}