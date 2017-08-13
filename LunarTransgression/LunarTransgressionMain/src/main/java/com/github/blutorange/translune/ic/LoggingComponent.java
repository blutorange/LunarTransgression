package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import com.github.blutorange.translune.gui.AbstractBean;
import com.github.blutorange.translune.gui.PlayerBean;
import com.github.blutorange.translune.socket.LunarDecoder;
import com.github.blutorange.translune.socket.LunarEncoder;
import com.github.blutorange.translune.socket.LunarEndpoint;
import com.github.blutorange.translune.socket.TestEndpoint;

import dagger.Component;

@Singleton
@Component(modules={LoggingModule.class})
public interface LoggingComponent {
	void inject(LunarEndpoint injectable);
	void inject(LunarDecoder injectable);
	void inject(LunarEncoder sessionBean);
	void inject(TestEndpoint sessionBean);
	void inject(PlayerBean abstractBean);
	void inject(AbstractBean abstractBean);
}