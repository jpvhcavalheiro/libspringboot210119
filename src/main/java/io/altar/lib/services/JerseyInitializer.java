package io.altar.lib.services;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import io.altar.lib.filters.CORSfilter;

@Component
public class JerseyInitializer extends ResourceConfig {
	public JerseyInitializer() {
		registerEndpoints();
		register(CORSfilter.class);
	}
	
	private void registerEndpoints() {
		register(BookServices.class);
		register(HistoryServices.class);
		register(UserServices.class);
	}
}
