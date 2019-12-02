package org.apache.rocketmq.example.namesrv;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.rocketmq.common.protocol.RequestCode;
import org.apache.rocketmq.remoting.netty.NettyDecoder;
import org.apache.rocketmq.remoting.netty.NettyEncoder;
import org.apache.rocketmq.remoting.netty.NettyRemotingClient;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaiyanan
 * @date 2019/8/30 09:34
 */
public class NameSrvTest {

    private static ChannelInboundHandlerAdapter handler = new ChannelInboundHandlerAdapter() {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(msg);
        }
    };

    private static Channel channel = null;

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new NettyEncoder());
                            p.addLast(new NettyDecoder());
                            p.addLast(handler);
                        }
                    });

            ChannelFuture future = b.connect(new InetSocketAddress("127.0.0.1", 9876));
            if (!future.isDone()) {
                System.out.println("done");
            }

            if (future.isCancelled()) {
                System.out.println("cancel");
            }

            if (!future.isSuccess()) {
                System.out.println("success");
            }
            channel = future.channel();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            group.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws Exception{
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.GET_NAMESRV_CONFIG, null);
        channel.writeAndFlush(request).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                future.cause().printStackTrace();
            }
        });
    }
}
