package type.client.handler;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import type.client.main.Network;
import type.common.handler.ChannelState;
import type.common.handler.PacketDirection;
import type.common.packet.Packet;
import type.common.work.Utils;

public class TypeClientPacketDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int pid = in.readInt();
		ChannelState cs = Network.state;
		Packet<?> p = cs.m.get(PacketDirection.CLIENTBOUND).get(pid).getDeclaredConstructor().newInstance();
		if(p == null)
			throw new IOException("Bad packet id " + pid);
		p.decode(in);
		Utils.l.info("ClientDecoder", "Processing packet " + p.getClass().getSimpleName() + "(0x" + Integer.toHexString(pid) + ") " + Utils.serialize(p));
		out.add(p);
	}

}
