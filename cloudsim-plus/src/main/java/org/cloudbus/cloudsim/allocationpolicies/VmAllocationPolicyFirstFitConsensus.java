package org.cloudbus.cloudsim.allocationpolicies;

import java.util.Random;
import java.util.List;
import java.util.Optional;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

public class VmAllocationPolicyFirstFitConsensus extends VmAllocationPolicyAbstract {

    /**
     * The index of the last host where a VM was placed.
     */
    private int lastHostIndex;

    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        final List<Host> hostList = getHostList();
        Random random = new Random();

        final int maxTries = hostList.size();
        for (int i = 0; i < maxTries; i++) {
            final Host host = hostList.get(lastHostIndex);
            if (host.isSuitableForVm(vm)) {
            	int vote = 1 + random.nextInt(maxTries);
            	if(vote >= maxTries/2) {
            		return Optional.of(host);
            	}   
            }

            lastHostIndex = ++lastHostIndex % hostList.size();
        }
        return Optional.empty();
    }

}

