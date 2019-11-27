package org.cloudbus.cloudsim.allocationpolicies;

import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

public class VmAllocationPolicyBestFitConsensus extends VmAllocationPolicyAbstract implements VmAllocationPolicy{

	@Override
	protected Optional<Host> defaultFindHostForVm(Vm vm) {
		final int maxTries = getHostList().size();
        final Comparator<Host> activeComparator = Comparator.comparing(Host::isActive).reversed();
        final Comparator<Host> comparator = activeComparator.thenComparingLong(Host::getFreePesNumber);

        final Stream<Host> stream = isParallelHostSearchEnabled() ? getHostList().stream().parallel() : getHostList().stream();
        return recFindHostForVm(vm, stream, maxTries, comparator);

	}
	
	private Optional<Host> recFindHostForVm(Vm vm, Stream<Host> stream, int maxTries, Comparator<Host> comparator){
		Random random = new Random();
        Optional<Host> opHost = stream.filter(host -> host.isSuitableForVm(vm)).min(comparator);
        if(opHost.isPresent()) {
        	int vote = 1 + random.nextInt(maxTries);
        	Host h = opHost.orElse(null);
        	if(vote >= maxTries/2) {
        		return opHost;
        	}else {
        		final Stream<Host> stream2 = isParallelHostSearchEnabled() ? getHostList().stream().parallel() : getHostList().stream();
        		return recFindHostForVm(vm, stream2.filter(host -> host.isSuitableForVm(vm))
        										   .filter(host -> host != h), maxTries-1, comparator);
        	}
        }else {
        	return Optional.empty();
        }		
	}
}
