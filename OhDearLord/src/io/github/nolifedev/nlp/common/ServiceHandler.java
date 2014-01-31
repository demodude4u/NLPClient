package io.github.nolifedev.nlp.common;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.Service.State;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ServiceHandler {

	private class CleanupService extends AbstractScheduledService {

		@Override
		protected void runOneIteration() throws Exception {
			Collections2.filter(services, new Predicate<Service>() {
				@Override
				public boolean apply(Service input) {
					return !input.isRunning();
				}
			});
		}

		@Override
		protected Scheduler scheduler() {
			return Scheduler.newFixedDelaySchedule(0, 5, TimeUnit.SECONDS);
		}

	}

	private final Collection<Service> services = new ConcurrentLinkedQueue<>();

	@Inject
	public ServiceHandler() {
		add(new CleanupService());
	}

	public void add(Service service) {
		Preconditions
				.checkArgument(service.state() == State.NEW
						|| service.state() == State.STARTING
						|| service.state() == State.RUNNING,
						"Service must be NEW, STARTING, or RUNNING to be added to this ServiceHandler!");
		if (service.state() == State.NEW) {
			service.startAsync();
			service.awaitRunning();
			System.out.println("Started Service: "
					+ service.getClass().getSimpleName());
		}
		services.add(service);
	}

}
