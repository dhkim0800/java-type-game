package type.common.handler;

import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import type.common.packet.Packet;
import type.common.packet.all.PacketCbAllSetState;
import type.common.packet.all.PacketSbAllClose;
import type.common.packet.login.PacketCbLoginAuthenticateResult;
import type.common.packet.login.PacketCbLoginEncrypt;
import type.common.packet.login.PacketSbLoginAnonymous;
import type.common.packet.login.PacketSbLoginAuthenticate;
import type.common.packet.login.PacketSbLoginEncrypt;
import type.common.packet.login.PacketSbLoginHandshake;
import type.common.packet.login.PacketSbLoginTest;
import type.common.packet.match.PacketCbMatchCanceled;
import type.common.packet.match.PacketCbMatchFound;
import type.common.packet.match.PacketCbMatchStarted;
import type.common.packet.match.PacketSbMatchCancel;
import type.common.packet.play.PacketCbPlayCountdownTime;
import type.common.packet.play.PacketCbPlayGameMessage;
import type.common.packet.play.PacketCbPlayGameOver;
import type.common.packet.play.PacketCbPlayHealthStatus;
import type.common.packet.play.PacketCbPlayLeftUsers;
import type.common.packet.play.PacketCbPlayPrepareGame;
import type.common.packet.play.PacketCbPlaySetHealth;
import type.common.packet.play.PacketCbPlaySetMaxHealth;
import type.common.packet.play.PacketSbPlayScore;
import type.common.packet.singleplayer.PacketSbSingleStopPlay;
import type.common.packet.user.PacketCbUserLobbyChat;
import type.common.packet.user.PacketSbUserLobbyChat;
import type.common.packet.user.PacketSbUserPlaySingle;
import type.common.packet.user.PacketSbUserStartMatchmake;

public enum ChannelState {
	LOGIN(0) {
		{
			add(PacketDirection.SERVERBOUND, PacketSbLoginHandshake.class);
			add(PacketDirection.SERVERBOUND, PacketSbLoginEncrypt.class);
			add(PacketDirection.SERVERBOUND, PacketSbLoginTest.class);
			add(PacketDirection.SERVERBOUND, PacketSbLoginAuthenticate.class);
			add(PacketDirection.SERVERBOUND, PacketSbLoginAnonymous.class);

			add(PacketDirection.CLIENTBOUND, PacketCbLoginEncrypt.class);
			add(PacketDirection.CLIENTBOUND, PacketCbLoginAuthenticateResult.class);

			addAllPackets();
		}

		@Override
		public String toString() {
			return "Login";
		}
	},
	USER(1) {
		{
			add(PacketDirection.SERVERBOUND, PacketSbUserPlaySingle.class);
			add(PacketDirection.SERVERBOUND, PacketSbUserLobbyChat.class);
			add(PacketDirection.SERVERBOUND, PacketSbUserStartMatchmake.class);

			add(PacketDirection.CLIENTBOUND, PacketCbUserLobbyChat.class);

			addAllPackets();
		}

		@Override
		public String toString() {
			return "AuthenticatedUser";
		}
	},
	MATCH(2) {
		{
			add(PacketDirection.CLIENTBOUND, PacketCbMatchCanceled.class);
			add(PacketDirection.CLIENTBOUND, PacketCbMatchStarted.class);
			add(PacketDirection.CLIENTBOUND, PacketCbMatchFound.class);

			add(PacketDirection.SERVERBOUND, PacketSbMatchCancel.class);

			addAllPackets();
		}

		@Override
		public String toString() {
			return "MatchMaking";
		}
	},
	PLAY(3) {
		{
			add(PacketDirection.CLIENTBOUND, PacketCbPlayGameOver.class);
			add(PacketDirection.CLIENTBOUND, PacketCbPlayGameMessage.class);
			add(PacketDirection.CLIENTBOUND, PacketCbPlayLeftUsers.class);
			add(PacketDirection.CLIENTBOUND, PacketCbPlaySetHealth.class);
			add(PacketDirection.CLIENTBOUND, PacketCbPlaySetMaxHealth.class);
			add(PacketDirection.CLIENTBOUND, PacketCbPlayPrepareGame.class);
			add(PacketDirection.CLIENTBOUND, PacketCbPlayCountdownTime.class);
			add(PacketDirection.CLIENTBOUND, PacketCbPlayHealthStatus.class);

			add(PacketDirection.SERVERBOUND, PacketSbPlayScore.class);

			addAllPackets();
		}

		@Override
		public String toString() {
			return "MultiPlayer";
		}
	},
	SINGLEPLAY(3) {
		{
			add(PacketDirection.SERVERBOUND, PacketSbSingleStopPlay.class);

			addAllPackets();
		}

		@Override
		public String toString() {
			return "SinglePlayer";
		}
	};
	public void addAllPackets() {
		add(PacketDirection.SERVERBOUND, PacketSbAllClose.class);

		add(PacketDirection.CLIENTBOUND, PacketCbAllSetState.class);
	}

	public Map<PacketDirection, BiMap<Integer, Class<? extends Packet<?>>>> m;
	private int st = 0;

	ChannelState(int state) {
		st = state;
		m = Maps.newEnumMap(PacketDirection.class);
	}

	public int getId() {
		return st;
	}

	public ChannelState getById(int n) {
		for (ChannelState cs : values()) {
			if (cs.st == n) {
				return cs;
			}
		}
		return null;
	}

	public void add(PacketDirection pd, Class<? extends Packet<?>> packet) {
		BiMap<Integer, Class<? extends Packet<?>>> g = m.get(pd);
		if (g == null) {
			g = HashBiMap.create();
			m.put(pd, g);
		}
		g.put(g.size(), packet);
	}
}
