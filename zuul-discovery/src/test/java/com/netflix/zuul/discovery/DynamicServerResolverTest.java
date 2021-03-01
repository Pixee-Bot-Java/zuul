package com.netflix.zuul.discovery;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.truth.Truth;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.Builder;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.netflix.zuul.resolver.ResolverListener;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DynamicServerResolverTest {


    @Test
    public void verifyListenerUpdates() {

        class CustomListener implements ResolverListener<DiscoveryResult> {

            private List<DiscoveryResult> resultSet = Lists.newArrayList();

            @Override
            public void onChange(List<DiscoveryResult> changedSet) {
                resultSet = changedSet;
            }

            public List<DiscoveryResult> updatedList() {
                return resultSet;
            }
        }

        final CustomListener listener = new CustomListener();
        final DynamicServerResolver resolver = new DynamicServerResolver(new DefaultClientConfigImpl(), listener);

        final InstanceInfo first = Builder.newBuilder()
                .setAppName("zuul-discovery-1")
                .setHostName("zuul-discovery-1")
                .setIPAddr("100.10.10.1")
                .setPort(443)
                .build();
        final InstanceInfo second = Builder.newBuilder()
                .setAppName("zuul-discovery-2")
                .setHostName("zuul-discovery-2")
                .setIPAddr("100.10.10.2")
                .setPort(443)
                .build();
        final DiscoveryEnabledServer server1 = new DiscoveryEnabledServer(first, true);
        final DiscoveryEnabledServer server2 = new DiscoveryEnabledServer(second, true);

        resolver.onUpdate(ImmutableList.of(server1, server2), ImmutableList.of());

        Truth.assertThat(listener.updatedList()).containsExactly(new DiscoveryResult(server1), new DiscoveryResult(server2));
    }
}