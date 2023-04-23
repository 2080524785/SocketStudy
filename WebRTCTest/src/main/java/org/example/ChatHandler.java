package org.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.internal.StringUtil;

public class ChatHandler extends SimpleChannelInboundHandler<String> {

    /** 用户集合 */
    private static Map<String, Channel> umap = new ConcurrentHashMap<>();

    /** channel绑定自己的用户ID */
    public static final AttributeKey<String> UID = AttributeKey.newInstance("uid");

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        JSONObject parseObj = JSONUtil.parseObj(msg);
        Integer type = parseObj.getInt("t");
        String uid = parseObj.getStr("uid");
        String tid = parseObj.getStr("tid");
        switch (type) {
            case 0:
                // 心跳
                break;
            case 1:
                // 用户加入聊天室
                umap.put(uid, ctx.channel());
                ctx.channel().attr(UID).set(uid);
                umap.forEach((x, y) -> {
                    if (!x.equals(uid)) {
                        JSONObject json = new JSONObject();
                        json.set("t", 2);
                        json.set("uid", uid);
                        json.set("type", "join");
                        y.writeAndFlush(json.toString());
                    }
                });
                break;
            case 2:
                Channel uc = umap.get(tid);
                if (null != uc) {
                    uc.writeAndFlush(msg);
                }
                break;
            case 9:
                // 用户退出聊天室
                umap.remove(uid);
                leave(ctx, uid);
                ctx.close();
                break;
            default:
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String uid = ctx.channel().attr(UID).get();
        if (StringUtil.isNullOrEmpty(uid)) {
            super.channelInactive(ctx);
            return;
        }
        ctx.channel().attr(UID).set(null);
        umap.remove(uid);
        leave(ctx, uid);
        super.channelInactive(ctx);
    }

    /**
     * 用户退出
     *
     * @param ctx
     * @param uid
     */
    private void leave(ChannelHandlerContext ctx, String uid) {
        umap.forEach((x, y) -> {
            if (!x.equals(uid)) {
                // 把数据转到用户服务
                JSONObject json = new JSONObject();
                json.set("t", 9);
                json.set("uid", uid);
                y.writeAndFlush(json.toString());
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
