package type.server.main;

import static type.common.work.Utils.l;

import java.io.File;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import type.common.work.Utils;
import type.server.game.MatchMaking;

public class Server {
	public static EventLoopGroup gameGroup;

	public static void main(String[] args) throws Exception {
		l.info("ServerBootstrap", "Starting TypeServer.");
		{
			File dbf = new File("database/");
			if (!dbf.exists())
				dbf.mkdirs();
		}

		l.info("ServerBootstrap", "Opening socket...");
		EventLoopGroup boss = new NioEventLoopGroup(5);
		EventLoopGroup work = new NioEventLoopGroup(5);
		EventLoopGroup game = new NioEventLoopGroup(5);
		gameGroup = game;
		try {
			ServerBootstrap sb = new ServerBootstrap();
			sb.group(boss, work).channel(NioServerSocketChannel.class).localAddress(Utils.port)
					.childHandler(new ServerInitializer());

			ChannelFuture ff = sb.bind().sync();
			l.info("ServerBootstrap", "Opened server socket on port " + Utils.port);

			// Server booting here
			MatchMaking.startThread(game);
			
			l.info("ServerBootstrap", "Started server.");
			ff.channel().closeFuture().sync();
		} finally {
			boss.shutdownGracefully().syncUninterruptibly();
			work.shutdownGracefully().syncUninterruptibly();
		}
	}
}
