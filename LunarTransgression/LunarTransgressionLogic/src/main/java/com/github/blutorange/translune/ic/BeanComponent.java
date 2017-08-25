package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import com.github.blutorange.translune.gui.AbstractBean;
import com.github.blutorange.translune.gui.AdminBean;
import com.github.blutorange.translune.gui.ManageBean;
import com.github.blutorange.translune.gui.PlayerBean;
import com.github.blutorange.translune.gui.SessionBean;
import com.github.blutorange.translune.gui.StatusBean;

import dagger.Component;

@Singleton
@Component(modules={DatabaseModule.class, LoggingModule.class, StorageModule.class})
public interface BeanComponent {
	void inject(SessionBean sessionBean);
	void inject(AdminBean sessionBean);
	void inject(PlayerBean abstractBean);
	void inject(ManageBean abstractBean);
	void inject(StatusBean abstractBean);
	void inject(AbstractBean abstractBean);
}
